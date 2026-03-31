package suck.enigma.mixin.minecraft.client.gui.screen;

import suck.enigma.enigma;
import suck.enigma.ui.mainmenu.CustomTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
   @Inject(method = "init", at = @At("HEAD"), cancellable = true)
   public void setCustomScreen(CallbackInfo ci) {
      if (!enigma.INSTANCE.isPanic()) {
         ci.cancel();
         MinecraftClient.getInstance().setScreen(new CustomTitleScreen());
      }
   }
}
