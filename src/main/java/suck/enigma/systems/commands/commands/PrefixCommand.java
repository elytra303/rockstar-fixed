package suck.enigma.systems.commands.commands;

import suck.enigma.Enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.CommandContext;
import suck.enigma.systems.commands.CommandRegistry;
import suck.enigma.systems.commands.ValidationResult;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.text.Text;
import ru.kotopushka.compiler.sdk.annotations.Compile;

public class PrefixCommand {
   @Compile
   public Command command() {
      return CommandBuilder.begin(
            "prefix",
            b -> b.desc("commands.prefix.description")
               .param("action", p -> p.optional().literal("list", "clear", "default", "set", "create"))
               .param(
                  "new",
                  p -> p.optional()
                     .validator(
                        text -> (ValidationResult)(text.length() > 1
                           ? ValidationResult.error(Localizator.translate("commands.prefix.invalid_length"))
                           : ValidationResult.ok(text))
                     )
               )
               .handler(this::handle)
         )
         .build();
   }

   @Compile
   private void handle(CommandContext ctx) {
      String action = (String)ctx.arguments().get(0);
      String newPrefix = (String)ctx.arguments().get(1);
      CommandRegistry registry = Enigma.getInstance().getCommandManager();
      String current = registry.getPrefix();
      if (action == null) {
         MessageUtility.info(Text.of(Localizator.translate("commands.prefix.current", current)));
      } else {
         String var6 = action.toLowerCase();
         switch (var6) {
            case "list":
               MessageUtility.info(Text.of(Localizator.translate("commands.prefix.current", current)));
               break;
            case "clear":
            case "default":
            case "reset":
               registry.setPrefix(".");
               MessageUtility.info(Text.of(Localizator.translate("commands.prefix.reset")));
               break;
            case "set":
            case "create":
               if (newPrefix == null || newPrefix.isEmpty()) {
                  MessageUtility.error(Text.of(Localizator.translate("commands.prefix.empty")));
                  return;
               }

               registry.setPrefix(newPrefix);
               MessageUtility.info(Text.of(Localizator.translate("commands.prefix.set", newPrefix)));
         }
      }
   }
}
