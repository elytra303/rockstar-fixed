package suck.enigma.systems.ai;

import suck.enigma.Enigma;
import suck.enigma.framework.msdf.Fonts;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.game.AttackEvent;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.event.impl.render.HudRenderEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.rotations.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@ModuleInfo(name = "Player", category = ModuleCategory.OTHER)
public class RotationPlayer extends BaseModule {
   private static final long LOG_WINDOW_MS = 2000L;
   private long lastSwingTimeMs;
   private LivingEntity lastTarget;
   private final EventListener<AttackEvent> onAttackEvent = event -> {
      LivingEntity tgt = (LivingEntity)event.getEntity();
      if (tgt != null) {
         this.lastTarget = tgt;
         this.lastSwingTimeMs = System.currentTimeMillis();
      }
   };
   private final EventListener<HudRenderEvent> onHudRender = event -> {
      long now = System.currentTimeMillis();
      if (this.lastTarget != null && now - this.lastSwingTimeMs <= 2000L) {
         event.getContext().drawCenteredText(Fonts.MEDIUM.getFont(8.0F), "Playing", sr.getScaledWidth() / 2.0F, 40.0F, ColorRGBA.WHITE);
      }
   };
   private final EventListener<ClientPlayerTickEvent> onPlayerTick = event -> {
      ClientPlayerEntity p = MinecraftClient.getInstance().player;
      if (p != null && this.lastTarget != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastSwingTimeMs > 2000L) {
            this.lastTarget = null;
         } else {
            Rotation predicted = Enigma.getInstance()
               .getAi()
               .predictRotation(Enigma.getInstance().getRotationHandler().getPlayerRotation(), this.lastTarget);
            mc.player.setYaw(predicted.getYaw());
            mc.player.setPitch(predicted.getPitch());
         }
      }
   };

   private float normalizeAngle(float angle) {
      angle %= 360.0F;
      if (angle > 180.0F) {
         angle -= 360.0F;
      }

      if (angle < -180.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private float calcTargetDeltaYaw(ClientPlayerEntity p, Entity t) {
      double dx = t.getX() - p.getX();
      double dz = t.getZ() - p.getZ();
      float targetYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      return this.normalizeAngle(targetYaw - p.getYaw());
   }

   private float calcTargetDeltaPitch(ClientPlayerEntity p, Entity t) {
      double dx = t.getX() - p.getX();
      double dz = t.getZ() - p.getZ();
      double dy = t.getEyeY() - p.getEyeY();
      double dist = Math.sqrt(dx * dx + dz * dz);
      float targetPitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));
      return this.normalizeAngle(targetPitch - p.getPitch());
   }
}
