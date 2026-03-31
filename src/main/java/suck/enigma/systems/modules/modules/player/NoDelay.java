package suck.enigma.systems.modules.modules.player;

import lombok.Generated;
import suck.enigma.mixin.minecraft.client.IMinecraftClient;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.BooleanSetting;

@ModuleInfo(name = "No Delay", category = ModuleCategory.PLAYER, desc = "modules.descriptions.no_delay")
public class NoDelay extends BaseModule {
   private final BooleanSetting jump = new BooleanSetting(this, "modules.settings.no_delay.jump", "modules.settings.no_delay.jump.description").enable();
   private final BooleanSetting rightClick = new BooleanSetting(
      this, "modules.settings.no_delay.right_click", "modules.settings.no_delay.right_click.description"
   );

   @Override
   public void tick() {
      if (this.rightClick.isEnabled()) {
         ((IMinecraftClient)mc).setUseCooldown(0);
      }

      super.tick();
   }

   @Generated
   public BooleanSetting getJump() {
      return this.jump;
   }

   @Generated
   public BooleanSetting getRightClick() {
      return this.rightClick;
   }
}
