package suck.enigma.systems.modules.listeners;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.Module;

public class ModuleTickListener implements EventListener<ClientPlayerTickEvent> {
   public void onEvent(ClientPlayerTickEvent event) {
      for (Module module : Enigma.getInstance().getModuleManager().getModules()) {
         if (module.isEnabled()) {
            module.tick();
         }
      }
   }
}
