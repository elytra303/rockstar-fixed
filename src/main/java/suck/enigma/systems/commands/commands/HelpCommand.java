package suck.enigma.systems.commands.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import suck.enigma.Enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.CommandContext;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.text.Text;
import ru.kotopushka.compiler.sdk.annotations.Compile;

public class HelpCommand {
   @Compile
   public Command command() {
      return CommandBuilder.begin("help", b -> b.aliases("помощь", "команды", "commands", "helpme").desc("commands.help.description").handler(this::handle))
         .build();
   }

   @Compile
   private void handle(CommandContext ctx) {
      List<Command> list = new ArrayList<>(enigma.getInstance().getCommandManager().commands());
      list.sort(Comparator.comparing(c -> c.names().getFirst(), String.CASE_INSENSITIVE_ORDER));
      List<String> infos = new ArrayList<>();
      int counter = 1;

      for (Command command : list) {
         infos.add(
            String.format(
               "%d) %s%s - %s",
               counter++,
               enigma.getInstance().getCommandManager().getPrefix(),
               command.names().getFirst(),
               Localizator.translate(command.description())
            )
         );
      }

      MessageUtility.info(Text.of("Доступные команды:\n" + String.join("\n", infos)));
   }
}
