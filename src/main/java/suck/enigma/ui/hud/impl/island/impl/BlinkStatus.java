package suck.enigma.ui.hud.impl.island.impl;

import suck.enigma.enigma;
import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.framework.msdf.Font;
import suck.enigma.framework.msdf.Fonts;
import suck.enigma.framework.objects.BorderRadius;
import suck.enigma.systems.modules.modules.player.Blink;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.ui.hud.impl.island.DynamicIsland;
import suck.enigma.ui.hud.impl.island.IslandStatus;
import suck.enigma.utility.colors.Colors;
import suck.enigma.utility.game.TextUtility;

public class BlinkStatus extends IslandStatus {
   public BlinkStatus(SelectSetting setting) {
      super(setting, "blink");
   }

   @Override
   public void draw(CustomDrawContext context) {
      DynamicIsland island = enigma.getInstance().getHud().getIsland();
      Blink blink = this.blink();
      Font font = Fonts.MEDIUM.getFont(7.0F);
      float x = sr.getScaledWidth() / 2.0F - island.getSize().width / 2.0F;
      float y = 7.0F;
      float width = this.size.width = 80.0F;
      float height = this.size.height = 15.0F;
      context.drawText(font, "Blink", x - 4.0F + 10.0F * this.animation.getValue(), y + 5.0F, Colors.getTextColor());
      if (!blink.getPulse().isEnabled()) {
         context.drawRightText(
            font,
            TextUtility.formatNumber((float)blink.getTimer().getElapsedTime() / 1000.0F) + " сек",
            x + width + 4.0F - 10.0F * this.animation.getValue(),
            y + 5.0F,
            Colors.getTextColor()
         );
      } else {
         float blinkWidth = width - font.width("Blink") - 14.0F;
         float progress = blinkWidth
            * ((blink.getTime().getCurrentValue() * 50.0F - (float)blink.getTimer().getElapsedTime()) / (blink.getTime().getCurrentValue() * 50.0F));
         context.drawRoundedRect(x + width - 5.0F - blinkWidth, y + 4.5F, blinkWidth, 6.0F, BorderRadius.all(2.5F), Colors.getAdditionalColor());
         context.drawRoundedRect(x + width - 5.0F - progress, y + 4.5F, progress, 6.0F, BorderRadius.all(2.5F), Colors.ACCENT);
      }
   }

   @Override
   public boolean canShow() {
      return this.blink().isEnabled();
   }

   private Blink blink() {
      return enigma.getInstance().getModuleManager().getModule(Blink.class);
   }
}
