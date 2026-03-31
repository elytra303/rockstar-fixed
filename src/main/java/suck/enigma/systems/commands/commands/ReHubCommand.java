package suck.enigma.systems.commands.commands;

import suck.enigma.Enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.commands.CommandContext;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.utility.game.MessageUtility;
import suck.enigma.utility.game.server.ServerUtility;
import suck.enigma.utility.interfaces.IMinecraft;
import suck.enigma.utility.time.Timer;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

public class ReHubCommand implements IMinecraft {
   private boolean processing;
   private final Timer timer = new Timer();
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (this.processing && mc.world != null && mc.player != null) {
         if ((ServerUtility.isFT() || ServerUtility.isFT()) && mc.world.getDifficulty() == Difficulty.EASY && this.timer.finished(1000L)) {
            mc.player.networkHandler.sendChatCommand("an" + ServerUtility.ftAn);
            this.timer.reset();
            this.processing = false;
         }
      }
   };

   public ReHubCommand() {
      enigma.getInstance().getEventManager().subscribe(this);
   }

   public Command command() {
      return CommandBuilder.begin("rct").aliases("reconnect").desc("commands.rehub.description").handler(this::handle).build();
   }

   private void handle(CommandContext ctx) {
      if (mc.player != null && mc.world != null) {
         if (ServerUtility.hasCT) {
            MessageUtility.error(Text.of(Localizator.translate("commands_rehub.ct")));
         } else {
            this.timer.reset();
            mc.player.networkHandler.sendChatCommand("hub");
            this.processing = true;
         }
      }
   }
}
