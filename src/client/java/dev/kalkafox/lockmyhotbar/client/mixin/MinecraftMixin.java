package dev.kalkafox.lockmyhotbar.client.mixin;

import dev.kalkafox.lockmyhotbar.client.LockMyHotbarClient;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Final public Options options;

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void handleDropItem(CallbackInfo ci) {
        var player = this.player;
        if (player == null) return;

        int selectedSlot = player.getInventory().selected;
        var locked = LockMyHotbarClient.isSlotLocked(selectedSlot);

        if (locked) {

            // consume the keybind
            this.options.keyDrop.consumeClick();
        }

        if (LockMyHotbarClient.CONFIG.notifyPlayer() && this.options.keyDrop.isDown()) {

            player.displayClientMessage(Component.literal("This hotbar is locked! Press the hotbar lock key again to unlock it."), true);
        }


    }

    @Inject(method = "saveReport", at = @At("HEAD"))
    private static void saveConfig(File file, CrashReport crashReport, CallbackInfoReturnable<Integer> cir) {
        System.out.println("Saving LockMyHotbar config...");
        LockMyHotbarClient.CONFIG.save();
    }

}
