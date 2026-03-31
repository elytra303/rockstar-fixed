package suck.enigma.protection.client;

import suck.enigma.enigma;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kotopushka.compiler.sdk.annotations.VMProtect;
import ru.kotopushka.compiler.sdk.enums.VMProtectType;

public class MinecraftClientMixinProtection {
   @VMProtect(type = VMProtectType.MUTATION)
   public static void init() {
      enigma.INSTANCE.initialize();
   }

   @VMProtect(type = VMProtectType.MUTATION)
   public static void shutdown() {
      enigma.INSTANCE.shutdown();
   }

   public static void updateTitle(CallbackInfoReturnable<String> cir) {
      if (!enigma.INSTANCE.isPanic()) {
         String title = "%s %s (%s)".formatted("enigma", "2.0", "Beta");
         cir.setReturnValue(title);
      }
   }
}
