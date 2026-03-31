package suck.enigma.mixin.minecraft.client.util;

import java.io.InputStream;
import java.util.List;
import suck.enigma.enigma;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Window.class)
public class WindowMixin {
   @Redirect(
      method = "setIcon",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Icons;getIcons(Lnet/minecraft/resource/ResourcePack;)Ljava/util/List;")
   )
   public List<InputSupplier<InputStream>> setCustomIcon(Icons instance, ResourcePack resourcePack) {
      if (enigma.getInstance().isPanic()) {
         try {
            return instance.getIcons(resourcePack);
         } catch (Exception var5) {
         }
      }

      InputStream icon16x = enigma.class.getResourceAsStream("/assets/%s/icons/window/icon16x16.png".formatted(enigma.MOD_ID));
      InputStream icon32x = enigma.class.getResourceAsStream("/assets/%s/icons/window/icon32x32.png".formatted(enigma.MOD_ID));
      return List.of(() -> icon16x, () -> icon32x);
   }
}
