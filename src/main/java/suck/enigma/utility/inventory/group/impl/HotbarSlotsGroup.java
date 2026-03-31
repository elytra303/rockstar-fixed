package suck.enigma.utility.inventory.group.impl;

import java.util.ArrayList;
import java.util.List;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.slots.HotbarSlot;

public class HotbarSlotsGroup extends SlotGroup<HotbarSlot> {
   public HotbarSlotsGroup() {
      super(createSlots());
   }

   private static List<HotbarSlot> createSlots() {
      List<HotbarSlot> slots = new ArrayList<>();

      for (int i = 0; i < 9; i++) {
         slots.add(new HotbarSlot(i));
      }

      return slots;
   }
}
