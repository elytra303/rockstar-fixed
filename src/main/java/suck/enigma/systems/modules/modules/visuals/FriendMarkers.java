package suck.enigma.systems.modules.modules.visuals;

import lombok.Generated;
import suck.enigma.Enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.render.Render3DEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.ModeSetting;
import suck.enigma.utility.colors.ColorRGBA;
import suck.enigma.utility.render.CrystalRenderer;
import suck.enigma.utility.render.RenderUtility;
import suck.enigma.utility.render.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "Friend Markers", desc = "Выделяет друзей", category = ModuleCategory.VISUALS)
public class FriendMarkers extends BaseModule {
   private final ModeSetting setting = new ModeSetting(this, "modules.settings.friends_markers.setting");
   private final ModeSetting.Value heads = new ModeSetting.Value(this.setting, "modules.settings.friends_markers.heads");
   private final ModeSetting.Value sims = new ModeSetting.Value(this.setting, "Sims");
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (this.sims.isSelected()) {
         RenderUtility.setupRender3D(true);
         MatrixStack ms = event.getMatrices();
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d cameraPos = camera.getPos();
         ColorRGBA color = new ColorRGBA(52.0F, 199.0F, 89.0F);
         BufferBuilder builder = CrystalRenderer.createBuffer();

         for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (enigma.getInstance().getFriendManager().isFriend(player.getName().getString())) {
               ms.push();
               RenderUtility.prepareMatrices(ms, Utils.getInterpolatedPos(player, event.getTickDelta()));
               float size = 0.1F;
               CrystalRenderer.render(ms, builder, 0.0F, player.getHeight() + 0.4F, 0.0F, size, color.withAlpha(255.0F));
               ms.pop();
            }
         }

         BuiltBuffer built = builder.endNullable();
         if (built != null) {
            BufferRenderer.drawWithGlobalProgram(built);
         }

         RenderUtility.endRender3D();
      }
   };

   @Generated
   public ModeSetting.Value getHeads() {
      return this.heads;
   }
}
