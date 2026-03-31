package suck.enigma.systems.modules.modules.combat;

import lombok.Generated;
import suck.enigma.enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.BooleanSetting;
import suck.enigma.systems.setting.settings.ModeSetting;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.systems.setting.settings.SliderSetting;
import suck.enigma.systems.target.TargetComparators;
import suck.enigma.systems.target.TargetSettings;
import suck.enigma.utility.animation.base.Animation;
import suck.enigma.utility.animation.base.Easing;
import suck.enigma.utility.game.CombatUtility;
import suck.enigma.utility.game.EntityUtility;
import suck.enigma.utility.game.TextUtility;
import suck.enigma.utility.game.prediction.ElytraPredictionSystem;
import suck.enigma.utility.game.prediction.FallingPlayer;
import suck.enigma.utility.game.server.ServerUtility;
import suck.enigma.utility.inventory.slots.HotbarSlot;
import suck.enigma.utility.math.MathUtility;
import suck.enigma.utility.math.PerlinNoise;
import suck.enigma.utility.rotations.MoveCorrection;
import suck.enigma.utility.rotations.Rotation;
import suck.enigma.utility.rotations.RotationHandler;
import suck.enigma.utility.rotations.RotationMath;
import suck.enigma.utility.rotations.RotationPriority;
import suck.enigma.utility.time.Timer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.kotopushka.compiler.sdk.annotations.VMProtect;
import ru.kotopushka.compiler.sdk.enums.VMProtectType;

@ModuleInfo(name = "Aura", category = ModuleCategory.COMBAT, desc = "Бьёт женщин и детей")
public class Aura extends BaseModule {
   private SliderSetting attackDistance;
   private SliderSetting aimDistance;
   private SelectSetting targets;
   private SelectSetting.Value players;
   private SelectSetting.Value animals;
   private SelectSetting.Value mobs;
   private SelectSetting.Value invisibles;
   private SelectSetting.Value nakedPlayers;
   private SelectSetting.Value friends;
   private ModeSetting sortingMode;
   private ModeSetting.Value distanceSorting;
   private ModeSetting.Value healthSorting;
   private ModeSetting.Value fovSorting;
   private ModeSetting rotationMode;
   private ModeSetting.Value noRotation;
   private ModeSetting.Value simpleRotation;
   private ModeSetting.Value funTimeRotation;
   private ModeSetting.Value spookyTimeRotation;
   private ModeSetting.Value holyWorldRotation;
   private ModeSetting.Value intaveRotation;
   private ModeSetting moveCorrectionMode;
   private ModeSetting.Value noMoveCorrection;
   private ModeSetting.Value directMoveCorrection;
   private ModeSetting.Value silentMoveCorrection;
   private ModeSetting styleAttack;
   private ModeSetting.Value fastPvp;
   private ModeSetting.Value slowPvp;
   private BooleanSetting onlyCriticals;
   private BooleanSetting walls;
   private BooleanSetting rayTrace;
   private BooleanSetting onlyWeapon;
   private BooleanSetting targeting;
   private final Animation nononoYaw = new Animation(300L, Easing.LINEAR);
   private final Animation nononoPitch = new Animation(1000L, Easing.LINEAR);
   private Timer attackTimer;
   boolean shield;
   private PerlinNoise noise = new PerlinNoise();
   private long rotationStartTime = 0L;
   private float noiseFactor = 0.0F;
   private int attacks;
   private Rotation additional;
   private final Timer collideTimer = new Timer();
   private final EventListener<ClientPlayerTickEvent> onPlayerTick = event -> {
      if (mc.player != null) {
         float requiredAimDistance = enigma.getInstance().getModuleManager().getModule(ElytraTarget.class).isEnabled()
            ? 50.0F
            : this.aimDistance.getCurrentValue();
         TargetSettings.Builder builder = new TargetSettings.Builder()
            .targetPlayers(this.players.isSelected())
            .targetAnimals(this.animals.isSelected())
            .targetMobs(this.mobs.isSelected())
            .targetInvisibles(this.invisibles.isSelected())
            .targetNakedPlayers(this.nakedPlayers.isSelected())
            .targetFriends(this.friends.isSelected())
            .requiredRange(requiredAimDistance);
         if (this.sortingMode.is(this.distanceSorting)) {
            builder.sortBy(TargetComparators.DISTANCE);
         } else if (this.sortingMode.is(this.healthSorting)) {
            builder.sortBy(TargetComparators.HEALTH);
         } else if (this.sortingMode.is(this.fovSorting)) {
            builder.sortBy(TargetComparators.FOV);
         }

         TargetSettings settings = builder.build();
         LivingEntity target = enigma.getInstance().getTargetManager().getCurrentTarget() instanceof LivingEntity living ? living : null;
         if (!this.targeting.isEnabled()
            || target == null
            || MathHelper.sqrt((float)mc.player.squaredDistanceTo(RotationMath.getNearestPoint(target))) > requiredAimDistance
            || !mc.world.hasEntity(target)
            || !target.isAlive()) {
            enigma.getInstance().getTargetManager().update(settings);
         }

         if (target != null) {
            this.rotateHead(target);
            if (this.shouldAttackEntity(target)) {
               this.attack(target);
            }
         } else {
            this.rotationStartTime = System.currentTimeMillis();
            this.noise = new PerlinNoise();
            this.noiseFactor = 1.0F;
         }
      }
   };

   public Aura() {
      this.initialize();
   }

   @VMProtect(type = VMProtectType.VIRTUALIZATION)
   private void initialize() {
      this.rotationMode = new ModeSetting(this, "modules.settings.aura.rotationMode");
      this.noRotation = new ModeSetting.Value(this.rotationMode, "modules.settings.aura.noRotation");
      this.simpleRotation = new ModeSetting.Value(this.rotationMode, "modules.settings.aura.simpleRotation").select();
      this.funTimeRotation = new ModeSetting.Value(this.rotationMode, "FunTime");
      this.spookyTimeRotation = new ModeSetting.Value(this.rotationMode, "SpookyTime");
      this.holyWorldRotation = new ModeSetting.Value(this.rotationMode, "HolyWorld");
      this.intaveRotation = new ModeSetting.Value(this.rotationMode, "Intave");
      this.attackDistance = new SliderSetting(this, "modules.settings.aura.attackDistance")
         .min(0.1F)
         .max(6.0F)
         .step(0.1F)
         .currentValue(3.0F)
         .suffix(number -> " %s".formatted(Localizator.translate("block")) + TextUtility.makeCountTranslated(number));
      this.aimDistance = new SliderSetting(this, "modules.settings.aura.aimDistance")
         .min(0.1F)
         .max(6.0F)
         .step(0.1F)
         .currentValue(3.0F)
         .suffix(number -> " %s".formatted(Localizator.translate("block")) + TextUtility.makeCountTranslated(number));
      this.onlyCriticals = new BooleanSetting(this, "only_crits");
      this.walls = new BooleanSetting(this, "modules.settings.aura.walls").enable();
      this.rayTrace = new BooleanSetting(this, "modules.settings.aura.rayTrace").enable();
      this.targeting = new BooleanSetting(this, "modules.settings.aura.targeting").enable();
      this.onlyWeapon = new BooleanSetting(this, "modules.settings.aura.onlyWeapon");
      this.targets = new SelectSetting(this, "targets");
      this.players = new SelectSetting.Value(this.targets, "players").select();
      this.animals = new SelectSetting.Value(this.targets, "animals").select();
      this.mobs = new SelectSetting.Value(this.targets, "mobs").select();
      this.invisibles = new SelectSetting.Value(this.targets, "invisibles").select();
      this.nakedPlayers = new SelectSetting.Value(this.targets, "nakedPlayers").select();
      this.friends = new SelectSetting.Value(this.targets, "friends");
      this.sortingMode = new ModeSetting(this, "sorting");
      this.distanceSorting = new ModeSetting.Value(this.sortingMode, "modules.settings.aura.distanceSorting").select();
      this.healthSorting = new ModeSetting.Value(this.sortingMode, "modules.settings.aura.healthSorting");
      this.fovSorting = new ModeSetting.Value(this.sortingMode, "modules.settings.aura.fovSorting");
      this.moveCorrectionMode = new ModeSetting(this, "modules.settings.aura.moveCorrectionMode");
      this.noMoveCorrection = new ModeSetting.Value(this.moveCorrectionMode, "modules.settings.aura.noMoveCorrection");
      this.directMoveCorrection = new ModeSetting.Value(this.moveCorrectionMode, "modules.settings.aura.directMoveCorrection");
      this.silentMoveCorrection = new ModeSetting.Value(this.moveCorrectionMode, "modules.settings.aura.silentMoveCorrection").select();
      this.styleAttack = new ModeSetting(this, "modules.settings.aura.styleAttack");
      this.fastPvp = new ModeSetting.Value(this.styleAttack, "1.8");
      this.slowPvp = new ModeSetting.Value(this.styleAttack, "1.9").select();
      this.attackTimer = new Timer();
   }

   private boolean shouldAttackEntity(LivingEntity targetedEntity) {
      if (!this.isCooledDown()) {
         return false;
      } else if (this.onlyWeapon.isEnabled() && !EntityUtility.isHoldingWeapon()) {
         return false;
      } else if (mc.player.isUsingItem() && mc.player.getActiveHand() == Hand.MAIN_HAND) {
         return false;
      } else if (this.inRange(targetedEntity)) {
         return false;
      } else if (this.walls.isEnabled()
         && this.spookyTimeRotation.isSelected()
         && mc.world
               .raycast(
                  new RaycastContext(
                     mc.player.getEyePos(),
                     mc.player
                        .getEyePos()
                        .add(
                           mc.player
                              .getRotationVector(-90.0F, enigma.getInstance().getRotationHandler().getCurrentRotation().getYaw())
                              .multiply(this.attackDistance.getCurrentValue())
                        ),
                     ShapeType.COLLIDER,
                     FluidHandling.NONE,
                     mc.player
                  )
               )
               .getType()
            == Type.BLOCK) {
         return false;
      } else {
         return !MathUtility.canTraceWithBlock(
                  this.attackDistance.getCurrentValue(),
                  enigma.getInstance().getRotationHandler().getCurrentRotation().getYaw(),
                  enigma.getInstance().getRotationHandler().getCurrentRotation().getPitch(),
                  mc.player,
                  targetedEntity,
                  !this.walls.isEnabled()
               )
               && this.rayTrace.isEnabled()
            ? false
            : !this.onlyCriticals.isEnabled() || !this.isCriticalRequired(targetedEntity) || CombatUtility.canPerformCriticalHit(targetedEntity, true);
      }
   }

   private boolean isCriticalRequired(LivingEntity targetedEntity) {
      float damage = this.calculateDamage(targetedEntity);
      return damage <= targetedEntity.getHealth();
   }

   public boolean isCooledDown() {
      if (mc.player == null) {
         return false;
      } else {
         return CombatUtility.getMace() != null
            ? this.attackTimer.finished(500L)
            : mc.player.getAttackCooldownProgress(1.5F) > 0.93F && this.attackTimer.finished(500L)
               || this.fastPvp.isSelected() && this.attackTimer.finished(50L);
      }
   }

   public float calculateDamage(LivingEntity targetedEntity) {
      return 0.0F;
   }

   private void attack(LivingEntity targetedEntity) {
      if (mc.interactionManager != null && mc.player != null) {
         this.shield = mc.player.isUsingItem() && mc.player.getActiveItem().getItem().getUseAction(mc.player.getActiveItem()) == UseAction.BLOCK;
         if (this.shield) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
         }

         if (CombatUtility.shouldBreakShield(targetedEntity) && CombatUtility.canBreakShield(targetedEntity)) {
            CombatUtility.tryBreakShield(targetedEntity);
         }

         HotbarSlot slot = CombatUtility.getMace();
         if (slot != null) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot.getSlotId()));
            CombatUtility.tryBreakShield(targetedEntity);
         }

         mc.interactionManager.attackEntity(mc.player, targetedEntity);
         mc.player.swingHand(Hand.MAIN_HAND);
         if (slot != null) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
         }

         if (this.shield) {
            mc.interactionManager
               .sendSequencedPacket(
                  mc.world,
                  sequence -> new PlayerInteractItemC2SPacket(
                     mc.player.getActiveHand(),
                     sequence,
                     enigma.getInstance().getRotationHandler().getCurrentRotation().getYaw(),
                     enigma.getInstance().getRotationHandler().getCurrentRotation().getPitch()
                  )
               );
         }

         this.additional = new Rotation(MathUtility.random(5.0, 20.0), MathUtility.random(5.0, 10.0));
         this.attackTimer.reset();
         this.attacks++;
      }
   }

   private void rotateHead(LivingEntity targetedEntity) {
      if (!this.onlyWeapon.isEnabled() || EntityUtility.isHoldingWeapon()) {
         if (!this.rotationMode.is(this.noRotation)) {
            MoveCorrection moveCorrection;
            if (this.moveCorrectionMode.is(this.silentMoveCorrection)) {
               moveCorrection = MoveCorrection.SILENT;
            } else if (this.moveCorrectionMode.is(this.directMoveCorrection)) {
               moveCorrection = MoveCorrection.DIRECT;
            } else {
               moveCorrection = MoveCorrection.NONE;
            }

            RotationHandler handler = enigma.getInstance().getRotationHandler();
            if (this.rotationMode.is(this.simpleRotation)) {
               Rotation rot = RotationMath.getRotationTo(
                       RotationMath.getNearestPoint(
                               targetedEntity,
                               enigma.getInstance().getModuleManager().getModule(ElytraTarget.class).isEnabled() && targetedEntity instanceof PlayerEntity player
                                       ? ElytraPredictionSystem.predictPlayerPosition(player)
                                       : targetedEntity.getPos()
                       )
               );
               if (mc.player.getEyePos().distanceTo(targetedEntity.getEyePos()) > 3.0) {
                  rot.setYaw(
                          RotationMath.getRotationTo(
                                          (enigma.getInstance().getModuleManager().getModule(ElytraTarget.class).isEnabled()
                                                  && targetedEntity instanceof PlayerEntity playerx
                                                  ? ElytraPredictionSystem.predictPlayerPosition(playerx)
                                                  : targetedEntity.getPos())
                                                  .add(0.0, targetedEntity.getEyeHeight(targetedEntity.getPose()), 0.0)
                                  )
                                  .getYaw()
                  );
               }

               handler.rotate(rot, moveCorrection, 180.0F, 180.0F, 180.0F, RotationPriority.TO_TARGET);
            }

            if (this.rotationMode.is(this.holyWorldRotation)) {
               Rotation current = enigma.getInstance().getRotationHandler().getCurrentRotation();
               Box box = targetedEntity.getBoundingBox();
               double offsetX = this.getSensitivity((float) Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
               double offsetY = this.getSensitivity((float) Math.cos(System.currentTimeMillis() / 10000.0)) * 0.15;
               double offsetZ = this.getSensitivity((float) Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
               Vec3d nearY = RotationMath.getNearestPoint(targetedEntity);
               Vec3d targetPos = new Vec3d(
                       nearY.x,
                       MathHelper.clamp(
                               MathUtility.interpolate(mc.player.getY(), targetedEntity.getEyeY(), 0.5),
                               targetedEntity.getBoundingBox().minY,
                               targetedEntity.getBoundingBox().maxY
                       ),
                       nearY.z
               );
               double clampedX = MathHelper.clamp(targetPos.getX() + offsetX, box.minX, box.maxX);
               double clampedY = targetPos.getY() + targetedEntity.getHeight() / 2.0F + offsetY;
               double clampedZ = MathHelper.clamp(targetPos.getZ() + offsetZ, box.minZ, box.maxZ);
               Vec3d vec = new Vec3d(clampedX, clampedY, clampedZ).subtract(mc.player.getEyePos());
               float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
               float yawDelta = MathHelper.wrapDegrees(yawToTarget - current.getYaw());
               float yaw = current.getYaw() + yawDelta;
               float pitch = Math.clamp(current.getPitch(), -90.0F, 90.0F);
               if (!MathUtility.canTraceWithBlock(this.attackDistance.getCurrentValue(), yaw, pitch, mc.player, targetedEntity, !this.walls.isEnabled())
                       && this.rayTrace.isEnabled()) {
                  pitch = RotationMath.getRotationTo(targetPos).getPitch();
               }

               handler.rotate(new Rotation(yaw, pitch), moveCorrection, 180.0F, 180.0F, 180.0F, RotationPriority.TO_TARGET);
            }

            if (this.rotationMode.is(this.funTimeRotation) || this.rotationMode.is(this.intaveRotation)) {
               if (mc.player.age % 500 == 0) {
                  this.noise = new PerlinNoise();
                  this.noiseFactor = 1.0F;
               }

               Vec3d nearY = RotationMath.getNearestPoint(targetedEntity);
               Rotation targetRot = RotationMath.getRotationTo(
                       new Vec3d(
                               nearY.x,
                               MathHelper.clamp(
                                       MathUtility.interpolate(mc.player.getY(), targetedEntity.getEyeY(), 0.5),
                                       targetedEntity.getBoundingBox().minY,
                                       targetedEntity.getBoundingBox().maxY
                               ),
                               nearY.z
                       )
               );
               Rotation multipoint = RotationMath.getRotationTo(RotationMath.getNearestPoint(targetedEntity));
               boolean idle = this.attackTimer.finished(300L);
               if (this.additional == null) {
                  this.additional = new Rotation(0.0F, 0.0F);
               }

               float targetYaw = targetRot.getYaw();
               float targetPitch = targetRot.getPitch();
               Rotation currentRot = handler.getCurrentRotation();
               float currentYaw = currentRot.getYaw();
               float currentPitch = currentRot.getPitch();
               float yawDiff = RotationMath.getAngleDifference(currentYaw, targetYaw);
               float pitchDiff = RotationMath.getAngleDifference(currentPitch, targetPitch);
               if (idle) {
                  if (this.shouldPreventSprinting()) {
                     targetYaw += 5.0F;
                     targetPitch -= 10.0F;
                  } else {
                     targetYaw -= 5.0F;
                  }
               }

               if (!this.rotationMode.is(this.intaveRotation) && !idle) {
                  targetYaw += this.additional.getYaw();
                  targetPitch += this.additional.getPitch();
               }

               float yawSpeed = Math.max(
                       (90.0F - Math.abs(yawDiff)) / (idle ? (mc.player.fallDistance > 0.0F ? 20.0F : 60.0F) : 40.0F), MathUtility.random(1.0, 5.0)
               )
                       * MathUtility.random(0.9, 1.1);
               float pitchSpeed = Math.abs(pitchDiff) / (idle ? (mc.player.fallDistance > 0.0F ? 60.0F : 100.0F) : 30.0F) * MathUtility.random(0.9, 1.1);
               long timeElapsed = System.currentTimeMillis() - this.rotationStartTime;
               float yawNoise = (float) this.noise.noise(timeElapsed * 5.0E-4);
               float pitchNoise = (float) this.noise.noise(timeElapsed * 5.0E-4, 10.0);
               float yawOffset = yawNoise * 25.0F * this.noiseFactor;
               float pitchOffset = pitchNoise * 25.0F * this.noiseFactor;
               float finalTargetYaw = targetYaw + yawOffset;
               float finalTargetPitch = targetPitch + pitchOffset;
               float totalDiff = Math.abs(yawDiff) + Math.abs(pitchDiff);
               if (totalDiff < 10.0F) {
                  this.noiseFactor = Math.max(0.0F, this.noiseFactor - 0.05F);
               }

               handler.rotate(
                       new Rotation(targetYaw, Math.clamp(targetPitch, -90.0F, 90.0F)),
                       moveCorrection,
                       yawSpeed * 25.0F,
                       pitchSpeed * 25.0F,
                       MathUtility.random(5.0, 50.0),
                       RotationPriority.TO_TARGET
               );
            }

            if (this.rotationMode.is(this.spookyTimeRotation)) {
               // ============================================================================
// Refactored SpookyTime rotation block for rotateHead(LivingEntity)
// Drop-in replacement for: if (this.rotationMode.is(this.spookyTimeRotation))
// ============================================================================

               if (this.rotationMode.is(this.spookyTimeRotation)) {
                  // Periodically reset Perlin noise seed for natural variation
                  if (mc.player.age % 500 == 0) {
                     this.noise = new PerlinNoise();
                     this.noiseFactor = 1.0F;
                  }

                  // --- Base target rotation ---
                  boolean collide = EntityUtility.collideWith(targetedEntity, 1.0F);
                  Vec3d nearPoint = RotationMath.getNearestPoint(targetedEntity);

                  Rotation targetRot = RotationMath.getRotationTo(new Vec3d(
                          nearPoint.x,
                          MathHelper.clamp(
                                  MathUtility.interpolate(mc.player.getY(), targetedEntity.getEyeY(), 0.5),
                                  targetedEntity.getBoundingBox().minY,
                                  targetedEntity.getBoundingBox().maxY
                          ),
                          nearPoint.z
                  ));

                  Rotation multipoint = RotationMath.getRotationTo(RotationMath.getNearestPoint(targetedEntity));

                  if (collide) {
                     targetRot = RotationMath.getRotationTo(targetedEntity.getPos().add(0.0, 0.5, 0.0));
                  }

                  // Correct pitch when ray-trace fails at longer distances
                  if (!MathUtility.canTraceWithBlock(
                          this.attackDistance.getCurrentValue(),
                          targetRot.getYaw(),
                          targetRot.getPitch(),
                          mc.player,
                          targetedEntity,
                          !this.walls.isEnabled()
                  ) && mc.player.getEyePos().distanceTo(targetedEntity.getEyePos()) > 3.0) {
                     targetRot.setPitch(multipoint.getPitch() + 10.0F);
                  }

                  boolean idle = this.attackTimer.finished(collide ? 500L : 200L);

                  if (this.additional == null) {
                     this.additional = new Rotation(0.0F, 0.0F);
                  }

                  float targetYaw = targetRot.getYaw();
                  float targetPitch = targetRot.getPitch();
                  Rotation currentRot = handler.getCurrentRotation();
                  float currentYaw = currentRot.getYaw();
                  float currentPitch = currentRot.getPitch();
                  float yawDiff = RotationMath.getAngleDifference(currentYaw, targetYaw);
                  float pitchDiff = RotationMath.getAngleDifference(currentPitch, targetPitch);

                  // --- GCD Fix: compute legal mouse-step granularity ---
                  float gcd = this.getGCDValue();

                  // --- Idle mode: smooth sine/cosine sway (0.5–1.0°) instead of sharp jumps ---
                  if (idle && !collide) {
                     float swayTime = mc.player.age * 0.05F;
                     float swayAmplitudeYaw = (float) MathUtility.random(0.5, 1.0);
                     float swayAmplitudePitch = (float) MathUtility.random(0.3, 0.7);
                     targetYaw += (float) Math.sin(swayTime) * swayAmplitudeYaw;
                     targetPitch += (float) Math.cos(swayTime * 0.7F) * swayAmplitudePitch;
                  }

                  // Active combat: apply post-attack additional offsets
                  if (!idle && EntityUtility.getBlock(0.0, 2.0, 0.0) == Blocks.AIR && !collide) {
                     targetYaw += this.additional.getYaw();
                     targetPitch += this.additional.getPitch();
                  }

                  // Wall check: look straight up when target is fully occluded
                  if (this.walls.isEnabled()
                          && !MathUtility.canSeen(nearPoint)
                          && mc.player.fallDistance <= CombatUtility.getFallDistance(targetedEntity)) {
                     targetPitch = -90.0F;
                  }

                  // --- Dynamic speed: per-tick randomization for human-like acceleration ---
                  // baseFactor jitters the overall speed each tick
                  float baseFactor = (float) MathUtility.random(0.6, 1.4);

                  // urgency ramps speed up when the cursor is far from the target,
                  // and slows it down as we converge
                  float absDiffSum = Math.abs(yawDiff) + Math.abs(pitchDiff);
                  float urgency = Math.min(1.0F, absDiffSum / 90.0F);
                  float dynamicScale = 0.3F + urgency * 0.7F;

                  float yawSpeed = Math.max(
                          (Math.abs(yawDiff) * dynamicScale * baseFactor) / (idle ? 8.0F : 3.0F),
                          (float) MathUtility.random(0.5, 2.0)
                  );
                  float pitchSpeed = Math.max(
                          (Math.abs(pitchDiff) * dynamicScale * baseFactor) / (idle ? 12.0F : 4.0F),
                          (float) MathUtility.random(0.3, 1.5)
                  );

                  // --- Perlin noise for organic micro-jitter ---
                  long timeElapsed = System.currentTimeMillis() - this.rotationStartTime;
                  float yawNoise = (float) this.noise.noise(timeElapsed * 5.0E-4);
                  float pitchNoise = (float) this.noise.noise(timeElapsed * 5.0E-4, 10.0);

                  // Gradually dampen noise as we converge on the target
                  if (absDiffSum < 10.0F) {
                     this.noiseFactor = Math.max(0.0F, this.noiseFactor - 0.05F);
                  }

                  float finalYaw = targetYaw + yawNoise * 15.0F * this.noiseFactor;
                  float finalPitch = targetPitch + pitchNoise * 15.0F * this.noiseFactor;

                  // --- Apply GCD fix: snap rotation deltas to legal mouse increments ---
                  float yawDelta = finalYaw - currentYaw;
                  float pitchDelta = finalPitch - currentPitch;
                  yawDelta -= yawDelta % gcd;
                  pitchDelta -= pitchDelta % gcd;
                  finalYaw = currentYaw + yawDelta;
                  finalPitch = currentPitch + pitchDelta;

                  handler.rotate(
                          new Rotation(finalYaw, Math.clamp(finalPitch, -90.0F, 90.0F)),
                          moveCorrection,
                          yawSpeed * 20.0F,
                          pitchSpeed * 20.0F,
                          (float) MathUtility.random(25.0, 60.0),
                          RotationPriority.TO_TARGET
                  );
               }

               public float getGCDValue() {
      double sensitivity = (Double)mc.options.getMouseSensitivity().getValue();
      double value = sensitivity * 0.6 + 0.2;
      double result = Math.pow(value, 3.0) * 0.8;
      return (float)result * 0.15F;
   }

   public float getSensitivity(float rot) {
      return this.getDeltaMouse(rot) * this.getGCDValue();
   }

   public float getDeltaMouse(float delta) {
      return Math.round(delta / this.getGCDValue());
   }

   public boolean shouldPreventSprinting() {
      LivingEntity target = enigma.getInstance().getTargetManager().getCurrentTarget() instanceof LivingEntity living ? living : null;
      if (target == null || mc.player == null) {
         return false;
      } else if (this.styleAttack.is(this.fastPvp)) {
         return false;
      } else {
         Criticals criticals = enigma.getInstance().getModuleManager().getModule(Criticals.class);
         boolean predict = criticals.isEnabled() && (criticals.canCritical() || mc.player.isOnGround())
            || !mc.player.isOnGround() && FallingPlayer.fromPlayer(mc.player).findFall(CombatUtility.getFallDistance(target));
         return this.onlyCriticals.isEnabled()
            && this.isCriticalRequired(target)
            && (
               predict
                  || CombatUtility.canPerformCriticalHit(target, true)
                  || !this.attackTimer.finished(!ServerUtility.isHW() && !ServerUtility.isST() ? 50L : (long)MathUtility.random(50.0, 150.0))
            );
      }
   }

   private boolean inRange(LivingEntity target) {
      return MathHelper.sqrt((float)mc.player.squaredDistanceTo(RotationMath.getNearestPoint(target))) > this.attackDistance.getCurrentValue();
   }

   @Override
   public void onEnable() {
      this.rotationStartTime = System.currentTimeMillis();
      this.noise = new PerlinNoise();
      this.noiseFactor = 1.0F;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      enigma.getInstance().getTargetManager().reset();
      super.onDisable();
   }

   @Generated
   public ModeSetting.Value getFastPvp() {
      return this.fastPvp;
   }

   @Generated
   public ModeSetting.Value getSlowPvp() {
      return this.slowPvp;
   }

   @Generated
   public Timer getAttackTimer() {
      return this.attackTimer;
   }

   @Generated
   public int getAttacks() {
      return this.attacks;
   }
}
