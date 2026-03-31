package suck.enigma.utility.math.calculator;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.network.SendPacketEvent;
import suck.enigma.utility.interfaces.IMinecraft;
import suck.enigma.utility.math.MathUtility;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;

public class ChatListener implements IMinecraft {
   private final EventListener<SendPacketEvent> onSendPacket = event -> {
      if (event.getPacket() instanceof ChatCommandSignedC2SPacket packet) {
         if (mc.player == null) {
            return;
         }

         String message = packet.command();
         if (message.startsWith("ah me")) {
            mc.player.networkHandler.sendChatMessage("/ah " + mc.player.getName().getString());
            event.cancel();
         }

         if (message.startsWith("ah sell ")) {
            String expression = message.replaceFirst("ah sell ", "");
            String result = MathUtility.calculate(expression);
            mc.player.networkHandler.sendChatMessage("/ah sell " + Math.round(Float.parseFloat(result)));
            event.cancel();
         }
      }
   };

   public ChatListener() {
      enigma.getInstance().getEventManager().subscribe(this);
   }
}
