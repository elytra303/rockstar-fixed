package suck.enigma.systems.commands.commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import suck.enigma.Enigma;
import suck.enigma.systems.commands.Command;
import suck.enigma.systems.commands.CommandBuilder;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.render.PreHudRenderEvent;
import suck.enigma.utility.animation.base.Animation;
import suck.enigma.utility.animation.base.Easing;
import suck.enigma.utility.colors.Colors;
import suck.enigma.utility.game.WebUtility;
import suck.enigma.utility.interfaces.IMinecraft;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class CatCommand implements IMinecraft {
   private final Animation animation = new Animation(1000L, Easing.CUBIC_IN_OUT);
   private boolean removing = false;
   private Identifier catTexture = null;
   private boolean loading = false;
   private final EventListener<PreHudRenderEvent> onPreHudRender = event -> {
      if (this.catTexture != null) {
         if (this.animation.getValue() == 1.0 && !this.removing) {
            this.removing = true;
         }

         this.animation.update(this.removing ? 0.0F : 1.0F);
         if (this.animation.getValue() != 0.0F || !this.removing) {
            float textureScale = 200.0F;
            float textureX = (mc.getWindow().getScaledWidth() - textureScale) / 2.0F;
            float textureY = (mc.getWindow().getScaledHeight() - textureScale) / 2.0F;
            event.getContext()
               .drawTexture(this.catTexture, textureX, textureY, textureScale, textureScale, Colors.WHITE.withAlpha(255.0F * this.animation.getValue()));
         }
      }
   };

   public CatCommand() {
      Enigma.getInstance().getEventManager().subscribe(this);
   }

   public Command command() {
      return CommandBuilder.begin("cat", b -> b.aliases("kitty").desc("commands.cat.description").handler(ctx -> this.loadRandomCatImage())).build();
   }

   private void loadRandomCatImage() {
      if (!this.loading) {
         this.loading = true;
         CompletableFuture.<NativeImage>supplyAsync(() -> {
            try {
               String json = WebUtility.fetchJson("https://api.thecatapi.com/v1/images/search");
               String imageUrl = WebUtility.extractImageUrl(json);
               if (imageUrl == null) {
                  return null;
               } else {
                  BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
                  return bufferedImage == null ? null : WebUtility.bufferedImageToNativeImage(bufferedImage, false);
               }
            } catch (IOException var3) {
               var3.printStackTrace();
               return null;
            }
         }).thenAccept(nativeImage -> mc.execute(() -> {
            if (nativeImage != null) {
               if (this.catTexture != null) {
                  mc.getTextureManager().destroyTexture(this.catTexture);
               }

               Identifier id = enigma.id("temp/cat/" + UUID.randomUUID());
               mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(nativeImage));
               this.catTexture = id;
               this.animation.update(1.0F);
               this.removing = false;
            }

            this.loading = false;
         }));
      }
   }
}
