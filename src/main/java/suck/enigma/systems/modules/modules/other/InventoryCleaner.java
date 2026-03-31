package suck.enigma.systems.modules.modules.other;

import java.util.ArrayList;
import java.util.List;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.utility.inventory.ItemSlot;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.group.SlotGroups;
import suck.enigma.utility.time.Timer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

@ModuleInfo(name = "Inventory Cleaner", category = ModuleCategory.OTHER, desc = "Очищает инвентарь от определенных блоков")
public class InventoryCleaner extends BaseModule {
   private final Timer timer = new Timer();
   private final List<Item> items = List.of(Items.STONE, Items.COBBLESTONE, Items.GRANITE, Items.IRON_ORE, Items.GOLD_ORE, Items.LAPIS_ORE);
   private final List<ItemSlot> slots = new ArrayList<>();
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (this.isEnabled() && mc.player != null && mc.player.currentScreenHandler != null) {
         if (this.timer.finished(150L)) {
            this.slots.clear();
            SlotGroup<ItemSlot> slotsToSearch = SlotGroups.inventory().and(SlotGroups.hotbar());

            for (Item item : this.items) {
               ItemSlot itemSlot = slotsToSearch.findItem(item);
               if (itemSlot != null) {
                  this.slots.add(itemSlot);
               }
            }

            if (this.slots.isEmpty()) {
               return;
            }

            ItemSlot slot = this.slots.removeFirst();
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot.getIdForServer(), 1, SlotActionType.THROW, mc.player);
            this.timer.reset();
         }
      }
   };
}
