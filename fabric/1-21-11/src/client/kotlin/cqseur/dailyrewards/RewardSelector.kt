package cqseur.dailyrewards

import cqseur.dailyrewards.config.ConfigManager
import kotlin.random.Random

object RewardSelector {
    fun bestIndex(offer: RewardOffer): Int {
        if (offer.cards.isEmpty()) return -1

        val scored = offer.cards.mapIndexed { index, card ->
            ScoredCard(index, score(card), parseAmount(card.amount))
        }
        val bestScore = scored.maxOf { it.score }
        val bestAmount = scored.filter { it.score == bestScore }.maxOf { it.amount }
        val best = scored.filter { it.score == bestScore && it.amount == bestAmount }

        return best[Random.nextInt(best.size)].index
    }

    private fun score(card: RewardCard): Int {
        val weights = ConfigManager.config.cardWeight
        val baseWeight = when (card.rewardType) {
            "experience" -> when (card.rarity) {
                "common" -> weights.hypixelExpCommon
                "rare" -> weights.hypixelExpRare
                "epic" -> weights.hypixelExpEpic
                "legendary" -> weights.hypixelExpLegendary
                else -> 0
            }
            "dust" -> weights.mysteryDust
            "souls" -> weights.skywarsSouls
            "adsense_token" -> weights.rewardTokens
            "tokens" -> if (isClassic(card)) weights.classicTokens else gameWeight(card)
            "coins" -> gameWeight(card)
            "mystery_box" -> when (card.rarity) {
                "common" -> weights.housingCardCommon
                "rare" -> weights.housingCardRare
                "epic" -> weights.housingCardEpic
                else -> weights.special
            }
            else -> weights.special
        }

        return baseWeight * 100_000 + parseAmount(card.amount).coerceIn(0, 99_999)
    }

    private fun gameWeight(card: RewardCard): Int {
        val config = ConfigManager.config
        return if (isPreferredGame(card)) {
            config.cardWeight.preferredGame
        } else {
            config.cardWeight.otherGames
        }
    }

    private fun isPreferredGame(card: RewardCard): Boolean {
        val haystacks = listOfNotNull(card.gameType, card.gameName, card.name).map(::normalize)
        return ConfigManager.config.preferGameRewards.any { preferred ->
            val needle = normalize(preferred)
            haystacks.any { it.contains(needle) || needle.contains(it) }
        }
    }

    private fun isClassic(card: RewardCard): Boolean {
        val gameType = card.gameType ?: return false
        return gameType.equals("LEGACY", ignoreCase = true) || card.gameName.equals("Classic", ignoreCase = true)
    }

    private fun parseAmount(amount: String): Int {
        return amount.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    private fun normalize(value: String): String {
        return value.lowercase().filter { it.isLetterOrDigit() }
    }

    private data class ScoredCard(
        val index: Int,
        val score: Int,
        val amount: Int
    )
}
