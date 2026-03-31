package suck.enigma.ui.hud.impl.island.impl;

import suck.enigma.enigma;
import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.framework.msdf.Fonts;
import suck.enigma.framework.objects.BorderRadius;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.ui.hud.impl.island.DynamicIsland;
import suck.enigma.ui.hud.impl.island.IslandStatus;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.colors.Colors;

public class DefaultStatus extends IslandStatus {
   public DefaultStatus(SelectSetting setting) {
      super(setting, "default");
   }

   @Override
   public void draw(CustomDrawContext context) {
      DynamicIsland island = enigma.getInstance().getHud().getIsland();
      float x = sr.getScaledWidth() / 2.0F - island.getSize().width / 2.0F;
      float y = 7.0F;
      float width = this.size.width = 20.0F + Fonts.MEDIUM.getFont(7.0F).width("enigma");
      float height = this.size.height = 15.0F;
      context.drawRoundedRect(x - 6.0F + 10.0F * this.animation.getValue(), y + 4.0F, 7.0F, 7.0F, BorderRadius.all(3.0F), new ColorRGBA(115.0F, 0.0F, 255.0F));
      context.drawText(Fonts.MEDIUM.getFont(7.0F), "enigma", x + 25.0F - 10.0F * this.animation.getValue(), y + 5.0F, Colors.getTextColor());
   }

   @Override
   public boolean canShow() {
      return true;
   }
}
