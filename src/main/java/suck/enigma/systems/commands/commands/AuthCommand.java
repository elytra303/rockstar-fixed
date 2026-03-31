package suck.enigma.systems.commands.commands;

import java.util.Map;
import java.util.Map.Entry;
import suck.enigma.enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.CommandContext;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.modules.modules.other.AutoAuth;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.text.Text;
import ru.kotopushka.compiler.sdk.annotations.Compile;

public class AuthCommand {
   @Compile
   public Command command() {
      return CommandBuilder.begin("auth", b -> b.aliases("autoAuth", "пароли", "passwords").desc("commands.auth.description").handler(this::handle)).build();
   }

   @Compile
   private void handle(CommandContext ctx) {
      Map<String, String> map = enigma.getInstance().getModuleManager().getModule(AutoAuth.class).listPassword();
      int counter = 1;
      if (map.isEmpty()) {
         MessageUtility.error(Text.of(Localizator.translate("commands.auth.empty")));
      } else {
         MessageUtility.info(Text.of(Localizator.translate("commands.auth.passwords")));

         for (Entry<String, String> entry : map.entrySet()) {
            String nickname = entry.getKey();
            String password = entry.getValue();
            MessageUtility.info(Text.of(counter++ + ") Ник: " + nickname + " | Пароль: " + password));
         }
      }
   }
}
