package suck.enigma.systems.event.impl.game;

import lombok.Generated;
import suck.enigma.systems.event.Event;
import net.minecraft.entity.Entity;

public class PostAttackEvent extends Event {
   private final Entity entity;

   public PostAttackEvent(Entity entity) {
      this.entity = entity;
   }

   @Generated
   public Entity getEntity() {
      return this.entity;
   }
}
