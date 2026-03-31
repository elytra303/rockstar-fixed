package suck.enigma;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import lombok.Generated;
import suck.enigma.framework.shader.GlProgram;
import suck.enigma.systems.ai.AIPredict;
import suck.enigma.systems.commands.CommandRegistry;
import suck.enigma.systems.config.ConfigDropHandler;
import suck.enigma.systems.config.ConfigManager;
import suck.enigma.systems.discord.DiscordManager;
import suck.enigma.systems.event.EventIntegration;
import suck.enigma.systems.event.EventManager;
import suck.enigma.systems.event.handlers.ServerConnectionHandler;
import suck.enigma.systems.file.FileManager;
import suck.enigma.systems.friends.FriendManager;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.modules.ModuleManager;
import suck.enigma.systems.modules.constructions.swinganim.SwingManager;
import suck.enigma.systems.modules.constructions.swinganim.presets.SwingPresetManager;
import suck.enigma.systems.modules.listeners.ModuleTickListener;
import suck.enigma.systems.modules.listeners.ModuleWidgetRenderer;
import suck.enigma.systems.notifications.NotificationManager;
import suck.enigma.systems.poshalko.PoshalkoHandler;
import suck.enigma.systems.target.TargetManager;
import suck.enigma.systems.theme.ThemeManager;
import suck.enigma.systems.waypoints.WayPointsManager;
import suck.enigma.ui.hud.Hud;
import suck.enigma.ui.menu.MenuScreen;
import suck.enigma.utility.game.TitleBarHelper;
import suck.enigma.utility.game.WebUtility;
import suck.enigma.utility.game.server.TPSHandler;
import suck.enigma.utility.interfaces.IMinecraft;
import suck.enigma.utility.math.calculator.ChatListener;
import suck.enigma.utility.render.DrawUtility;
import suck.enigma.utility.rotations.RotationHandler;
import suck.enigma.utility.rotations.RotationUpdateListener;
import suck.enigma.utility.sounds.MusicTracker;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kotopushka.compiler.sdk.annotations.Compile;
import ru.kotopushka.compiler.sdk.annotations.CompileBytecode;
import ru.kotopushka.compiler.sdk.annotations.Initialization;

public enum Enigma implements IMinecraft {
   INSTANCE;

   public static final String NAME = "Enigma";
   public static final String BUILD_TYPE = "Release";
   public static final String VERSION = "1.0";
   public static final String MOD_ID = "enigma".toLowerCase();
   public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
   private EventManager eventManager;
   private ThemeManager themeManager;
   private ModuleManager moduleManager;
   private CommandRegistry commandManager;
   private FriendManager friendManager;
   private DiscordManager discordManager;
   private RotationHandler rotationHandler;
   private TargetManager targetManager;
   private MusicTracker musicTracker;
   private FileManager fileManager;
   private NotificationManager notificationManager;
   private ConfigManager configManager;
   private SwingManager swingManager;
   private TPSHandler tpsHandler;
   private AIPredict ai;
   private Hud hud;
   private ServerConnectionHandler serverConnectionHandler;
   private PoshalkoHandler poshalkoHandler;
   private WayPointsManager wayPointsManager;
   private SwingPresetManager swingPresetManager;
   private MenuScreen menuScreen;
   private ChatListener chatListener;
   private boolean panic;

   @Compile
   @Initialization
   public void initialize() {
      LOGGER.info("Initializing {}...", "enigma");
      this.musicTracker = new MusicTracker();
      this.wayPointsManager = new WayPointsManager();
      this.eventManager = new EventManager();
      this.friendManager = new FriendManager();
      this.themeManager = new ThemeManager();
      this.discordManager = new DiscordManager();
      this.rotationHandler = new RotationHandler(new RotationUpdateListener());
      this.targetManager = new TargetManager();
      this.fileManager = new FileManager();
      this.moduleManager = new ModuleManager(new ModuleTickListener(), new ModuleWidgetRenderer());
      this.hud = new Hud();
      this.tpsHandler = new TPSHandler();
      this.notificationManager = new NotificationManager();
      this.fileManager.registerClientFiles();
      this.moduleManager.registerModules();
      this.moduleManager.enableModules();
      this.configManager = new ConfigManager();
      this.configManager.handle();
      this.commandManager = new CommandRegistry();
      this.commandManager.initCommands();
      this.swingManager = new SwingManager();
      this.swingPresetManager = new SwingPresetManager();
      this.swingPresetManager.handle();
      this.fileManager.loadClientFiles();
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return enigma.id("after_shader_load");
         }

         public void reload(ResourceManager manager) {
            GlProgram.loadAndSetupPrograms();
         }
      });
      DrawUtility.initializeShaders();
      Localizator.loadTranslations();
      this.chatListener = new ChatListener();
      this.serverConnectionHandler = new ServerConnectionHandler();
      this.poshalkoHandler = new PoshalkoHandler();
      String osName = System.getProperty("os.name");
      String pcName = System.getProperty("user.name");
      if (osName.toLowerCase().contains("windows") && !pcName.equals("sheluvparis")) {
         this.discordManager.connect();
      }

      ConfigDropHandler.init();
      TitleBarHelper.setDarkTitleBar();
      new EventIntegration();
      this.createAvatar();
      LOGGER.info("{} initialized", "enigma");
   }

   public void shutdown() {
      LOGGER.info("Shutting down...");
      this.fileManager.saveClientFiles();
      if (!this.isPanic()) {
         this.configManager.getAutoSaveConfig().save();
      }

      if (!this.isPanic()) {
         this.swingPresetManager.getAutoSavePreset().save();
      }

      this.setPanic(false);
   }

   public static enigma getInstance() {
      return INSTANCE;
   }

   public static Identifier id(String path) {
      return Identifier.of(MOD_ID, path);
   }

   @CompileBytecode
   private void createAvatar() {
      try {
         BufferedImage bufferedImage = ImageIO.read(new URL("https://enigma.pub/api/avatars/ConeTin.jpg?t=1754613855632"));
         if (bufferedImage == null) {
            return;
         }

         Identifier id = id("temp/avatar");
         mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(WebUtility.bufferedImageToNativeImage(bufferedImage, true)));
      } catch (Exception var3) {
      }
   }

   @Generated
   public EventManager getEventManager() {
      return this.eventManager;
   }

   @Generated
   public ThemeManager getThemeManager() {
      return this.themeManager;
   }

   @Generated
   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   @Generated
   public CommandRegistry getCommandManager() {
      return this.commandManager;
   }

   @Generated
   public FriendManager getFriendManager() {
      return this.friendManager;
   }

   @Generated
   public DiscordManager getDiscordManager() {
      return this.discordManager;
   }

   @Generated
   public RotationHandler getRotationHandler() {
      return this.rotationHandler;
   }

   @Generated
   public TargetManager getTargetManager() {
      return this.targetManager;
   }

   @Generated
   public MusicTracker getMusicTracker() {
      return this.musicTracker;
   }

   @Generated
   public FileManager getFileManager() {
      return this.fileManager;
   }

   @Generated
   public NotificationManager getNotificationManager() {
      return this.notificationManager;
   }

   @Generated
   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public SwingManager getSwingManager() {
      return this.swingManager;
   }

   @Generated
   public TPSHandler getTpsHandler() {
      return this.tpsHandler;
   }

   @Generated
   public AIPredict getAi() {
      return this.ai;
   }

   @Generated
   public Hud getHud() {
      return this.hud;
   }

   @Generated
   public ServerConnectionHandler getServerConnectionHandler() {
      return this.serverConnectionHandler;
   }

   @Generated
   public PoshalkoHandler getPoshalkoHandler() {
      return this.poshalkoHandler;
   }

   @Generated
   public WayPointsManager getWayPointsManager() {
      return this.wayPointsManager;
   }

   @Generated
   public SwingPresetManager getSwingPresetManager() {
      return this.swingPresetManager;
   }

   @Generated
   public MenuScreen getMenuScreen() {
      return this.menuScreen;
   }

   @Generated
   public ChatListener getChatListener() {
      return this.chatListener;
   }

   @Generated
   public boolean isPanic() {
      return this.panic;
   }

   @Generated
   public void setMenuScreen(MenuScreen menuScreen) {
      this.menuScreen = menuScreen;
   }

   @Generated
   public void setPanic(boolean panic) {
      this.panic = panic;
   }
}
