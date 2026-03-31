package suck.enigma.systems.commands.commands;

import suck.enigma.enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.CommandContext;
import suck.enigma.systems.commands.ValidationResult;
import suck.enigma.systems.target.TargetManager;
import ru.kotopushka.compiler.sdk.annotations.Compile;

public class TargetCommand {
   @Compile
   public Command command() {
      return CommandBuilder.begin(
            "target",
            b -> b.aliases("targets")
               .desc("Управление списком таргетов")
               .param("action", p -> p.literal("add", "remove", "del", "delete", "clear", "list"))
               .param("id", p -> p.optional().validator(ValidationResult::ok))
               .handler(this::handle)
         )
         .build();
   }

   @Compile
   private void handle(CommandContext ctx) {
      String action = (String)ctx.arguments().get(0);
      String id = (String)ctx.arguments().get(1);
      TargetManager tm = enigma.getInstance().getTargetManager();
      String var5 = action.toLowerCase();
      switch (var5) {
         case "add":
            tm.addTarget(id);
            break;
         case "remove":
         case "del":
         case "delete":
            tm.removeTarget(id);
            break;
         case "clear":
            tm.clearTarget();
            break;
         case "list":
            tm.listTarget();
      }
   }
}
