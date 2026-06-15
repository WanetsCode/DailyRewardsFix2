package cqseur.dailyrewards.config

/**
 * Data class containing all configuration settings for the mod
 **/
data class ConfigData(
    // Core Settings //
    var modEnabled: Boolean = true,
    var dailyReminder: Boolean = true,
    var autoClaim: Boolean = true,
    var autoClaimWeightsEnabled: Boolean = true,
    var quickDeliveryKeybind: Boolean = true,
    var flipAnimation: Boolean = true,
    var flipSpeed: Float = 0.5f,
    var showOverlay: Boolean = true,
    var lastClaimDate: String = "",
    var lastClaimTimestamp: Long = 0L,
    var currentStreakDays: Int = 0,
    var totalClaimsCount: Int = 0,
    var preferGameRewards: List<String> = listOf(
        "Arcade", "Arena", "Bedwars", "Blitz SG", "Build Battle", 
        "Cops and crims", "Duels", "Mega Walls", "Murder Mystery", 
        "Paintball", "Quakecraft", "SkyWars Coins", "SkyWars Tokens", 
        "Smash Heroes", "TNT Games", "Turbo Kart Racers", "UHC"
    ),

    // Preferences - getting the best thing the user wants //
    var cardWeight: CardWeight = CardWeight()
) {
    data class CardWeight(
        // Weights with the same value are picked at random

        /// Housing Cards
        var housingCardCommon: Int = 0,
        var housingCardRare: Int = 0,
        var housingCardEpic: Int = 0,

        /// General cards
        var hypixelExpCommon: Int = 0,
        var hypixelExpRare: Int = 0,
        var hypixelExpEpic: Int = 0,
        var hypixelExpLegendary: Int = 0,

        /// General cards - value-specific
        // If the weight of this card is bigger than the other weights the card is picked,
        // the system automatically prefers the bigger values
        var mysteryDust: Int = 0,
        var skywarsSouls: Int = 0,
        var rewardTokens: Int = 0,
        var classicTokens: Int = 0,

        // Coins and tokens have different weights if the game is set as preferred (line 12)
        var preferredGame: Int = 0,
        var otherGames: Int = 0,

        // For Emotes, Gestures & Treasure hunter suit pieces (https://shorturl.at/rgt7G)
        var special: Int = 0
    )
}
