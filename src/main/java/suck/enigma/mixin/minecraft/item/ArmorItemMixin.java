package suck.enigma.mixin.minecraft.item;

import suck.enigma.utility.mixins.ArmorItemAddition;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin implements ArmorItemAddition {
   @Unique
   private EquipmentType enigma$type;
   @Unique
   private ArmorMaterial enigma$material;

   @Inject(method = "<init>", at = @At("TAIL"))
   public void saveArgs(ArmorMaterial material, EquipmentType type, Settings settings, CallbackInfo ci) {
      this.enigma$type = type;
      this.enigma$material = material;
   }

   @Override
   public ArmorMaterial enigma$getMaterial() {
      return this.enigma$material;
   }

   @Override
   public EquipmentType enigma$getType() {
      return this.enigma$type;
   }
}
