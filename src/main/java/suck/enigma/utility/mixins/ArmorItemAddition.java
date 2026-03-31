package suck.enigma.utility.mixins;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public interface ArmorItemAddition {
   EquipmentType enigma$getType();

   ArmorMaterial enigma$getMaterial();
}
