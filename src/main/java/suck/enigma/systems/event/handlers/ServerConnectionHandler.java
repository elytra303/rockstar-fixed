package suck.enigma.systems.event.handlers;

// import com.viaversion.viafabricplus.ViaFabricPlus;
import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.network.ServerConnectionEvent;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.utility.game.MessageUtility;
import suck.enigma.utility.interfaces.IMinecraft;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class ServerConnectionHandler implements IMinecraft {
   private boolean messageSent = false;
   private boolean connected;
   private final EventListener<ServerConnectionEvent> onServerConnection = event -> {
      this.connected = true;
      this.messageSent = false;
   };
   private final EventListener<ClientPlayerTickEvent> onClientPlayerTick = event -> {
      if (this.connected
            && !this.messageSent
            && mc.player != null
            && mc.player.age > 100
            && mc.getCurrentServerEntry() != null
            && FabricLoader.getInstance().isModLoaded("viafabricplus")) {
         // String warning = Localizator.translate("chat.connection_warning",
         // mc.getCurrentServerEntry().address,
         // ViaFabricPlus.getImpl().getTargetVersion());
         // MessageUtility.info(Text.of(warning));
         // this.messageSent = true;
      }
   };

   public ServerConnectionHandler() {
      enigma.getInstance().getEventManager().subscribe(this);
   }
}
