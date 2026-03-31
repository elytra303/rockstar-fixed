package suck.enigma.ui.hud.impl.island;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.utility.animation.base.Animation;
import suck.enigma.utility.animation.base.Easing;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.colors.Colors;
import suck.enigma.utility.interfaces.IScaledResolution;

public abstract class IslandStatus extends SelectSetting.Value implements IScaledResolution {
   protected IslandSize size = new IslandSize(48.0F, 15.0F);
   protected final Animation animation = new Animation(500L, Easing.BAKEK_SIZE);

   public IslandStatus(SelectSetting parent, String name) {
      super(parent, "hud.dynamic_island.statuses." + name);
      this.select();
   }

   public void draw(CustomDrawContext context) {
   }

   public void drawWithAlpha(CustomDrawContext context) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.animation.getValue());
      this.draw(context);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void click(float mouseX, float mouseY, int button) {
   }

   public abstract boolean canShow();

   public ColorRGBA getColor() {
      return Colors.getBackgroundColor();
   }

   @Generated
   public IslandSize getSize() {
      return this.size;
   }

   @Generated
   public Animation getAnimation() {
      return this.animation;
   }
}
