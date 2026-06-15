package cqseur.dailyrewards

import cqseur.dailyrewards.config.ConfigManager
import cqseur.dailyrewards.utils.MessageUtils
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

object DeliveryClaimer {
    private const val MENU_TIMEOUT_TICKS = 80
    private const val CLICK_COOLDOWN_TICKS = 8

    private lateinit var keyBinding: KeyBinding
    private var waitingForMenu = false
    private var ticksWaiting = 0
    private var clickCooldown = 0

    fun init() {
        keyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.dailyrewards.claim_delivery",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                KeyBinding.Category.create(Identifier.of("dailyrewards", "dailyrewards"))
            )
        )
    }

    fun tick(client: MinecraftClient) {
        while (keyBinding.wasPressed()) {
            claimFromDelivery(client)
        }

        if (!waitingForMenu) return

        ticksWaiting++
        if (clickCooldown > 0) {
            clickCooldown--
            return
        }

        if (ticksWaiting > MENU_TIMEOUT_TICKS) {
            waitingForMenu = false
            MessageUtils.sendError("Could not find Daily Reward in /delivery.")
            return
        }

        clickDailyRewardSlot(client)
    }

    fun claimFromDelivery(client: MinecraftClient = MinecraftClient.getInstance()) {
        if (!ConfigManager.config.modEnabled || !ConfigManager.config.quickDeliveryKeybind) return

        val player = client.player ?: return
        player.networkHandler.sendChatCommand("delivery")
        waitingForMenu = true
        ticksWaiting = 0
        clickCooldown = CLICK_COOLDOWN_TICKS
        MessageUtils.sendInfo("Opening /delivery for daily reward...")
    }

    private fun clickDailyRewardSlot(client: MinecraftClient) {
        val player = client.player ?: return
        val interactionManager = client.interactionManager ?: return
        val handler = player.currentScreenHandler
        if (handler == player.playerScreenHandler) return

        val slot = handler.slots.firstOrNull { slot ->
            val stack = slot.stack
            !stack.isEmpty && stack.name.string.contains("Daily Reward", ignoreCase = true)
        } ?: return

        interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.PICKUP, player)
        waitingForMenu = false
        client.setScreen(null)
        MessageUtils.sendSuccess("Daily Reward selected from /delivery.")
    }
}
