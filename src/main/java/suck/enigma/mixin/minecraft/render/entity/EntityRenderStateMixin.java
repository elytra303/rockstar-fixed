package suck.enigma.mixin.minecraft.render.entity;

import suck.enigma.utility.mixins.EntityRenderStateAddition;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderStateAddition {
   @Unique
   private Entity enigma$entity;

   @Unique
   @Override
   public void enigma$setEntity(Entity entity) {
      this.enigma$entity = entity;
   }

   @Unique
   @Override
   public Entity enigma$getEntity() {
      return this.enigma$entity;
   }
}
