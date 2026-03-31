package suck.enigma.systems.modules.modules.other;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.BooleanSetting;
import suck.enigma.systems.waypoints.WayPointsManager;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;

@ModuleInfo(name = "Death Cords", category = ModuleCategory.OTHER, desc = "Отправляет координаты смерти в чат")
public class DeathCords extends BaseModule {
   private boolean death;
   private final BooleanSetting wayDeath = new BooleanSetting(this, "Ставить метку");
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (!(mc.currentScreen instanceof DeathScreen) || mc.player == null) {
         this.death = true;
      } else if (this.death) {
         int xCord = (int)mc.player.getX();
         int yCord = (int)mc.player.getY();
         int zCord = (int)mc.player.getZ();
         MessageUtility.info(Text.of("Координаты смерти: " + xCord + " " + yCord + " " + zCord));
         if (this.wayDeath.isEnabled()) {
            WayPointsManager wayPointsManager = Enigma.getInstance().getWayPointsManager();
            if (wayPointsManager.contains("Death")) {
               wayPointsManager.del("Death");
            }

            wayPointsManager.add("Death", xCord, yCord, zCord);
         }

         this.death = false;
      }
   };
}
