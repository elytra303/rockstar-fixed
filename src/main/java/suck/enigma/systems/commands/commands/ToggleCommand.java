package suck.enigma.systems.commands.commands;

import java.util.List;
import suck.enigma.Enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.ParameterBuilder;
import suck.enigma.systems.commands.ParameterValidator;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.modules.Module;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.text.Text;
import ru.kotopushka.compiler.sdk.annotations.Compile;

public class ToggleCommand {
      @Compile
      public Command command() {
            List<String> moduleNames = enigma.getInstance().getModuleManager().getModules().stream()
                        .map(module -> module.getName().replace(" ", "")).toList();
            return CommandBuilder.begin("toggle")
                        .aliases("t")
                        .desc("commands.toggle.description")
                        .param("module",
                                    p -> p.validator(
                                                (suck.enigma.systems.commands.ParameterValidator) ParameterBuilder.MODULE)
                                                .suggests(moduleNames))
                        .handler(context -> {
                              Module module = (Module) context.arguments().getFirst();
                              module.toggle();
                              MessageUtility.info(Text.of(Localizator
                                          .translate("commands.toggle." + (module.isEnabled() ? "enabled" : "disabled"),
                                                      module.getName())));
                        })
                        .build();
      }
}
