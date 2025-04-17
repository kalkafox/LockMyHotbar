package dev.kalkafox.lockmyhotbar.client.mixin;

import dev.kalkafox.lockmyhotbar.client.LockMyHotbarClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void onDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        var selectedSlot = ((LocalPlayer)(Object)this).getInventory().selected;

        if (LockMyHotbarClient.isSlotLocked(selectedSlot)) {
            cir.setReturnValue(false);
        }

    }
}
