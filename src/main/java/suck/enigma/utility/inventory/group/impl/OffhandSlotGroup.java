package suck.enigma.utility.inventory.group.impl;

import java.util.List;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.slots.OffhandSlot;

public class OffhandSlotGroup extends SlotGroup<OffhandSlot> {
   public OffhandSlotGroup() {
      super(List.of(new OffhandSlot()));
   }
}
