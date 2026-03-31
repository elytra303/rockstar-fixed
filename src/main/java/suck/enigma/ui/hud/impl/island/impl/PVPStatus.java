package suck.enigma.ui.hud.impl.island.impl;

import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.ui.hud.impl.island.TimerStatus;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.game.server.ServerUtility;

public class PVPStatus extends TimerStatus {
   public PVPStatus(SelectSetting setting) {
      super(setting, "pvp");
   }

   @Override
   public void draw(CustomDrawContext context) {
      this.update("s", ServerUtility.ctTime, "Вы в PVP режиме", new ColorRGBA(185.0F, 28.0F, 28.0F));
      super.draw(context);
   }

   @Override
   public boolean canShow() {
      return ServerUtility.hasCT;
   }
}
