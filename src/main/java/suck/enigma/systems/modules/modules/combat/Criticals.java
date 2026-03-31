package suck.enigma.systems.modules.modules.combat;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.game.InternalAttackEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.utility.math.MathUtility;
import suck.enigma.utility.rotations.Rotation;
import suck.enigma.utility.rotations.RotationHandler;
import suck.enigma.utility.rotations.RotationMath;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

@ModuleInfo(name = "Criticals", category = ModuleCategory.COMBAT)
public class Criticals extends BaseModule {
   private int airTicks;
   private final EventListener<InternalAttackEvent> onAttack = event -> {
      if (!mc.player.isTouchingWater()) {
         if (!mc.player.isOnGround() && mc.player.fallDistance == 0.0F) {
            RotationHandler rotationHandler = Enigma.getInstance().getRotationHandler();
            Rotation rot = rotationHandler.isIdling() ? rotationHandler.getPlayerRotation() : rotationHandler.getCurrentRotation();
            rot = new Rotation(rot.getYaw() + MathUtility.random(-1.0, 1.0), rot.getPitch() + MathUtility.random(-1.0, 1.0));
            rot = RotationMath.correctRotation(rot);
            mc.player
               .networkHandler
               .sendPacket(
                  new Full(
                     mc.player.getX(),
                     mc.player.getY() - (mc.player.fallDistance = MathUtility.random(1.0E-5F, 1.0E-4F)),
                     mc.player.getZ(),
                     rot.getYaw(),
                     rot.getPitch(),
                     mc.player.isOnGround(),
                     mc.player.horizontalCollision
                  )
               );
         }
      }
   };

   @Override
   public void tick() {
      if (mc.player.isOnGround()) {
         this.airTicks = 0;
      } else {
         this.airTicks++;
      }
   }

   public boolean canCritical() {
      return this.isEnabled() && mc.player.fallDistance <= 0.0F && !mc.player.isOnGround();
   }
}
