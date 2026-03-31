package suck.enigma.utility.inventory.group.impl;

import java.util.ArrayList;
import java.util.List;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.slots.ArmorSlot;

public class ArmorSlotsGroup extends SlotGroup<ArmorSlot> {
   public ArmorSlotsGroup() {
      super(createSlots());
   }

   private static List<ArmorSlot> createSlots() {
      List<ArmorSlot> slots = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         slots.add(new ArmorSlot(i));
      }

      return slots;
   }
}
