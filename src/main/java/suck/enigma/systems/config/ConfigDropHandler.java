package suck.enigma.systems.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.Generated;
import suck.enigma.Enigma;
import suck.enigma.systems.file.FileManager;
import suck.enigma.systems.notifications.NotificationType;
import suck.enigma.utility.game.MessageUtility;
import suck.enigma.utility.interfaces.IMinecraft;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWDropCallbackI;
import ru.kotopushka.compiler.sdk.annotations.Compile;
import ru.kotopushka.compiler.sdk.annotations.Initialization;

public final class ConfigDropHandler implements IMinecraft {
   private static boolean initialized;

   @Compile
   @Initialization
   public static void init() {
      if (!initialized) {
         initialized = true;
         long handle = mc.getWindow().getHandle();
         GLFWDropCallbackI[] previous = new GLFWDropCallbackI[1];
         GLFWDropCallbackI callback = (window, count, names) -> {
            if (previous[0] != null) {
               previous[0].invoke(window, count, names);
            }

            for (int i = 0; i < count; i++) {
               String path = GLFWDropCallback.getName(names, i);
               handleDrop(path);
            }
         };
         previous[0] = GLFW.glfwSetDropCallback(handle, callback);
      }
   }

   private static void handleDrop(String path) {
      try {
         File src = new File(path);
         if (!src.isFile()) {
            return;
         }

         if (!src.getName().endsWith(".rock")) {
            return;
         }

         File destDir = new File(FileManager.DIRECTORY, "configs");
         if (!destDir.exists() && !destDir.mkdirs()) {
            enigma.LOGGER.error("Failed to create directory {}", destDir.getAbsolutePath());
            return;
         }

         File dest = new File(destDir, src.getName());
         Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
         String name = src.getName().substring(0, src.getName().lastIndexOf(46));
         ConfigManager manager = Enigma.getInstance().getConfigManager();
         manager.refresh();
         ConfigFile cfg = manager.getConfig(name);
         if (cfg == null) {
            cfg = new ConfigFile(name);
            manager.getConfigFiles().add(cfg);
         }

         cfg.load();
         MessageUtility.info(Text.of("Конфиг " + name + " загружен"));
         Enigma.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, Text.translatable("configs.loaded").getString());
      } catch (Exception var7) {
         enigma.LOGGER.error("Failed to load dropped config {}", path, var7);
      }
   }

   @Generated
   private ConfigDropHandler() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
