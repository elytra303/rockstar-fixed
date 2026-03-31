package suck.enigma.systems.modules.modules.visuals;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.game.EntityDeathEvent;
import suck.enigma.systems.event.impl.render.Render3DEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.ColorSetting;
import suck.enigma.utility.animation.base.Animation;
import suck.enigma.utility.animation.base.Easing;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.colors.Colors;
import suck.enigma.utility.math.MathUtility;
import suck.enigma.utility.render.DrawUtility;
import suck.enigma.utility.render.RenderUtility;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "Kill Effects", category = ModuleCategory.VISUALS, desc = "modules.descriptions.kill_effects")
public class KillEffects extends BaseModule {
   private final List<KillEffects.Lightning> lightnings = new ArrayList<>();
   private final ColorSetting color = new ColorSetting(this, "modules.settings.kill_effects.color").color(Colors.ACCENT);
   private final EventListener<EntityDeathEvent> onEntityDeath = event -> {
      if (!event.getEntity().isRemoved()) {
         this.lightnings.add(new KillEffects.Lightning(event.getEntity().getPos(), this.color.getColor()));
      }
   };
   private final EventListener<Render3DEvent> on3DRender = event -> {
      MatrixStack ms = event.getMatrices();
      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      ms.push();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.enableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      Identifier id = enigma.id("textures/bloom.png");
      RenderSystem.setShaderTexture(0, id);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (KillEffects.Lightning lightning : this.lightnings) {
         lightning.render(builder, event.getMatrices(), camera);
         if (lightning.animation.getValue() == 1.0F) {
            lightning.showing = false;
         }
      }

      BuiltBuffer builtBuffer = builder.endNullable();
      if (builtBuffer != null) {
         BufferRenderer.drawWithGlobalProgram(builtBuffer);
      }

      RenderSystem.depthMask(true);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
      RenderSystem.disableDepthTest();
      ms.pop();
      this.lightnings.removeIf(lightningx -> !lightningx.showing && lightningx.animation.getValue() == 0.0F);
   };

   static class Lightning {
      final Vec3d pos;
      final ColorRGBA color;
      boolean showing = true;
      final Animation animation = new Animation(300L, 0.0F, Easing.FIGMA_EASE_IN_OUT);
      final List<Vec3d> poses = new ArrayList<>();

      public Lightning(Vec3d pos, ColorRGBA color) {
         this.pos = pos;
         this.color = color;
         Vec3d lastPos = pos;

         for (int i = 0; i < 200; i++) {
            this.poses.add(lastPos = lastPos.add(MathUtility.random(-0.4F, 0.4F), 0.25, MathUtility.random(-0.4F, 0.4F)));
         }
      }

      void render(BufferBuilder builder, MatrixStack ms, Camera camera) {
         this.animation.setEasing(Easing.BOUNCE_IN);
         this.animation.setDuration(500L);
         this.animation.update(this.showing);

         for (Vec3d pos : this.poses) {
            float size = (float)(2.0 + 5.0 * (pos.y - this.pos.y) / 50.0);
            ms.push();
            RenderUtility.prepareMatrices(ms, pos);
            ms.multiply(camera.getRotation());
            DrawUtility.drawImage(
               ms,
               builder,
               (double)(-size / 2.0F),
               (double)(-size / 2.0F),
               0.0,
               (double)size,
               (double)size,
               this.color.withAlpha(255.0F * this.animation.getValue() * 0.4F)
            );
            ms.pop();
         }
      }
   }
}
