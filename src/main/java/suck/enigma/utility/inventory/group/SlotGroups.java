package suck.enigma.utility.inventory.group;

import suck.enigma.utility.inventory.group.impl.ArmorSlotsGroup;
import suck.enigma.utility.inventory.group.impl.HotbarSlotsGroup;
import suck.enigma.utility.inventory.group.impl.InventorySlotsGroup;
import suck.enigma.utility.inventory.group.impl.OffhandSlotGroup;
import suck.enigma.utility.inventory.slots.ArmorSlot;
import suck.enigma.utility.inventory.slots.HotbarSlot;
import suck.enigma.utility.inventory.slots.InventorySlot;
import suck.enigma.utility.inventory.slots.OffhandSlot;

public class SlotGroups {
   private SlotGroups() {
   }

   public static SlotGroup<HotbarSlot> hotbar() {
      return new HotbarSlotsGroup();
   }

   public static SlotGroup<InventorySlot> inventory() {
      return new InventorySlotsGroup();
   }

   public static SlotGroup<ArmorSlot> armor() {
      return new ArmorSlotsGroup();
   }

   public static SlotGroup<OffhandSlot> offhand() {
      return new OffhandSlotGroup();
   }
}
