package suck.enigma.systems.event.impl.render;

import lombok.Generated;
import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.systems.event.Event;

public class ChatRenderEvent extends Event {
   private final CustomDrawContext context;
   private final float tickDelta;

   @Generated
   public CustomDrawContext getContext() {
      return this.context;
   }

   @Generated
   public float getTickDelta() {
      return this.tickDelta;
   }

   @Generated
   public ChatRenderEvent(CustomDrawContext context, float tickDelta) {
      this.context = context;
      this.tickDelta = tickDelta;
   }
}
