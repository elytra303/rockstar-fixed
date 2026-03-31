package suck.enigma.utility.rotations;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.event.impl.player.InputEvent;
import suck.enigma.systems.event.impl.render.Render3DEvent;

public class RotationUpdateListener {
   private final EventListener<ClientPlayerTickEvent> onTick = event -> Enigma.getInstance().getRotationHandler().update();
   private final EventListener<Render3DEvent> onRender = event -> Enigma.getInstance().getRotationHandler().updateRender(event.getTickDelta());
   private final EventListener<InputEvent> onInputEvent = event -> {
      RotationHandler rotationHandler = enigma.INSTANCE.getRotationHandler();
      RotationTask currentTask = rotationHandler.getCurrentTask();
      if (!rotationHandler.isIdling() && currentTask != null && currentTask.getMoveCorrection() == MoveCorrection.SILENT) {
         event.setYaw(Enigma.getInstance().getRotationHandler().getCurrentRotation().getYaw());
      }
   };

   public RotationUpdateListener() {
      Enigma.getInstance().getEventManager().subscribe(this);
   }
}
