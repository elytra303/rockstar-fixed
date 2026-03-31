package suck.enigma.systems.modules.modules.other;

import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.SelectSetting;
import net.minecraft.entity.effect.StatusEffects;

@ModuleInfo(name = "Effect Remover", category = ModuleCategory.OTHER)
public class EffectRemover extends BaseModule {
   private final SelectSetting effectsToRemove = new SelectSetting(this, "modules.settings.effect_remover.remove");
   private final SelectSetting.Value levitation = new SelectSetting.Value(this.effectsToRemove, "modules.settings.effect_remover.remove.levitation").select();
   private final SelectSetting.Value jumpBoost = new SelectSetting.Value(this.effectsToRemove, "modules.settings.effect_remover.remove.jump_boost").select();
   private final SelectSetting.Value slowFall = new SelectSetting.Value(this.effectsToRemove, "modules.settings.effect_remover.remove.slow_fall").select();
   private final EventListener<ClientPlayerTickEvent> onPlayerTick = event -> {
      if (mc.player != null) {
         if (this.levitation.isSelected()) {
            mc.player.removeStatusEffect(StatusEffects.LEVITATION);
         }

         if (this.jumpBoost.isSelected()) {
            mc.player.removeStatusEffect(StatusEffects.JUMP_BOOST);
         }

         if (this.slowFall.isSelected()) {
            mc.player.removeStatusEffect(StatusEffects.SLOW_FALLING);
         }
      }
   };
}
