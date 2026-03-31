package suck.enigma.systems.modules.modules.player;

import java.util.function.Predicate;
import suck.enigma.mixin.minecraft.client.IMinecraftClient;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.SliderSetting;
import suck.enigma.utility.inventory.ItemSlot;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.group.SlotGroups;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "Auto Eat", category = ModuleCategory.PLAYER)
public class AutoEat extends BaseModule {
   private boolean eating;
   private final SliderSetting food = new SliderSetting(this, "modules.settings.auto_eat.food").step(1.0F).min(1.0F).max(20.0F).currentValue(15.0F);
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (mc.player.getHungerManager().getFoodLevel() <= this.food.getCurrentValue()) {
         SlotGroup<ItemSlot> search = SlotGroups.inventory().and(SlotGroups.hotbar());
         ItemSlot foodSlot = search.findItem((Predicate<ItemStack>)(stack -> stack.getItem().getDefaultStack().contains(DataComponentTypes.FOOD)));
         if (!mc.player.getOffHandStack().contains(DataComponentTypes.FOOD) && foodSlot != null) {
            foodSlot.moveToOffHand();
         }

         this.eating = true;
         if (mc.currentScreen != null && !mc.player.isUsingItem()) {
            ((IMinecraftClient)mc).idoItemUse();
         } else {
            mc.options.useKey.setPressed(true);
         }
      } else if (this.eating) {
         this.eating = false;
         mc.options.useKey.setPressed(false);
      }
   };
}
