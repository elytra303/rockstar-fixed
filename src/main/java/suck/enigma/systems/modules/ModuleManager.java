package suck.enigma.systems.modules;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import suck.enigma.enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.event.impl.render.HudRenderEvent;
import suck.enigma.systems.event.impl.window.KeyPressEvent;
import suck.enigma.systems.event.impl.window.MouseEvent;
import suck.enigma.systems.modules.exception.UnknownModuleException;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.modules.modules.combat.AimBot;
import suck.enigma.systems.modules.modules.combat.AntiBot;
import suck.enigma.systems.modules.modules.combat.Aura;
import suck.enigma.systems.modules.modules.combat.AutoArmor;
import suck.enigma.systems.modules.modules.combat.AutoExplosion;
import suck.enigma.systems.modules.modules.combat.AutoGapple;
import suck.enigma.systems.modules.modules.combat.AutoPotion;
import suck.enigma.systems.modules.modules.combat.AutoSoup;
import suck.enigma.systems.modules.modules.combat.AutoTotem;
import suck.enigma.systems.modules.modules.combat.BackTrack;
import suck.enigma.systems.modules.modules.combat.Criticals;
import suck.enigma.systems.modules.modules.combat.ElytraTarget;
import suck.enigma.systems.modules.modules.combat.Hitboxes;
import suck.enigma.systems.modules.modules.combat.TriggerBot;
import suck.enigma.systems.modules.modules.combat.Velocity;
import suck.enigma.systems.modules.modules.movement.AutoSprint;
import suck.enigma.systems.modules.modules.movement.ElytraStrafe;
import suck.enigma.systems.modules.modules.movement.Flight;
import suck.enigma.systems.modules.modules.movement.NoSlow;
import suck.enigma.systems.modules.modules.movement.NoWeb;
import suck.enigma.systems.modules.modules.movement.Speed;
import suck.enigma.systems.modules.modules.movement.Spider;
import suck.enigma.systems.modules.modules.movement.Timer;
import suck.enigma.systems.modules.modules.movement.WindHop;
import suck.enigma.systems.modules.modules.other.Assist;
import suck.enigma.systems.modules.modules.other.Auction;
import suck.enigma.systems.modules.modules.other.AutoAccept;
import suck.enigma.systems.modules.modules.other.AutoAuth;
import suck.enigma.systems.modules.modules.other.AutoDuels;
import suck.enigma.systems.modules.modules.other.AutoJoin;
import suck.enigma.systems.modules.modules.other.AutoResell;
import suck.enigma.systems.modules.modules.other.CounterMine;
import suck.enigma.systems.modules.modules.other.DeathCords;
import suck.enigma.systems.modules.modules.other.EffectRemover;
import suck.enigma.systems.modules.modules.other.FastItemUse;
import suck.enigma.systems.modules.modules.other.InventoryCleaner;
import suck.enigma.systems.modules.modules.other.ItemPickup;
import suck.enigma.systems.modules.modules.other.NameProtect;
import suck.enigma.systems.modules.modules.other.Panic;
import suck.enigma.systems.modules.modules.other.RussianRoulette;
import suck.enigma.systems.modules.modules.other.Sounds;
import suck.enigma.systems.modules.modules.other.TestModule;
import suck.enigma.systems.modules.modules.player.AutoBrew;
import suck.enigma.systems.modules.modules.player.AutoEat;
import suck.enigma.systems.modules.modules.player.AutoFarm;
import suck.enigma.systems.modules.modules.player.AutoInvisible;
import suck.enigma.systems.modules.modules.player.AutoLeave;
import suck.enigma.systems.modules.modules.player.AutoSwap;
import suck.enigma.systems.modules.modules.player.Blink;
import suck.enigma.systems.modules.modules.player.CreeperFarm;
import suck.enigma.systems.modules.modules.player.ElytraUtils;
import suck.enigma.systems.modules.modules.player.FreeCam;
import suck.enigma.systems.modules.modules.player.GuiMove;
import suck.enigma.systems.modules.modules.player.InvUtils;
import suck.enigma.systems.modules.modules.player.MiddleClick;
import suck.enigma.systems.modules.modules.player.MineHelper;
import suck.enigma.systems.modules.modules.player.NoDelay;
import suck.enigma.systems.modules.modules.player.NoFall;
import suck.enigma.systems.modules.modules.player.NoInteract;
import suck.enigma.systems.modules.modules.player.NoPush;
import suck.enigma.systems.modules.modules.player.NoRotate;
import suck.enigma.systems.modules.modules.player.Nuker;
import suck.enigma.systems.modules.modules.player.PlayerUtils;
import suck.enigma.systems.modules.modules.player.Scaffold;
import suck.enigma.systems.modules.modules.player.Stealer;
import suck.enigma.systems.modules.modules.player.TargetPearl;
import suck.enigma.systems.modules.modules.visuals.Ambience;
import suck.enigma.systems.modules.modules.visuals.AntiInvisible;
import suck.enigma.systems.modules.modules.visuals.Arrows;
import suck.enigma.systems.modules.modules.visuals.CustomFog;
import suck.enigma.systems.modules.modules.visuals.FriendMarkers;
import suck.enigma.systems.modules.modules.visuals.Interface;
import suck.enigma.systems.modules.modules.visuals.KillEffects;
import suck.enigma.systems.modules.modules.visuals.MenuModule;
import suck.enigma.systems.modules.modules.visuals.Nametags;
import suck.enigma.systems.modules.modules.visuals.ObjectInfo;
import suck.enigma.systems.modules.modules.visuals.Prediction;
import suck.enigma.systems.modules.modules.visuals.Removals;
import suck.enigma.systems.modules.modules.visuals.SoundESP;
import suck.enigma.systems.modules.modules.visuals.StorageESP;
import suck.enigma.systems.modules.modules.visuals.SwingAnimation;
import suck.enigma.systems.modules.modules.visuals.TNTTimer;
import suck.enigma.systems.modules.modules.visuals.TargetESP;
import suck.enigma.systems.modules.modules.visuals.TrapESP;
import suck.enigma.systems.modules.modules.visuals.ViewModel;
import suck.enigma.systems.modules.modules.visuals.World;
import suck.enigma.systems.modules.modules.visuals.XRay;
import net.minecraft.client.MinecraftClient;
import ru.kotopushka.compiler.sdk.annotations.CompileBytecode;

public class ModuleManager {
   private final List<Module> modules = new ArrayList<>();
   private final EventListener<ClientPlayerTickEvent> tickListener;
   private final EventListener<HudRenderEvent> moduleWidgetRenderer;
   private final EventListener<KeyPressEvent> onKeyPress = event -> {
      if (MinecraftClient.getInstance().currentScreen == null) {
         for (Module module : this.getModules()) {
            if (module.getKey() == event.getKey() && module.getKey() != -1 && event.getAction() == 1) {
               module.toggle();
            }
         }
      }
   };
   private final EventListener<MouseEvent> onMouseButtonPress = event -> {
      if (MinecraftClient.getInstance().currentScreen == null) {
         for (Module module : this.getModules()) {
            if (module.getKey() == event.getButton() && module.getKey() != -1 && event.getAction() == 1) {
               module.toggle();
            }
         }
      }
   };

   public ModuleManager(EventListener<ClientPlayerTickEvent> tickListener, EventListener<HudRenderEvent> moduleWidgetRenderer) {
      this.tickListener = tickListener;
      this.moduleWidgetRenderer = moduleWidgetRenderer;
      enigma.getInstance().getEventManager().subscribe(this);
   }

   @CompileBytecode
   public void registerModules() {
      this.register(new Aura());
      this.register(new AutoTotem());
      this.register(new TriggerBot());
      this.register(new AutoGapple());
      this.register(new AimBot());
      this.register(new AutoPotion());
      this.register(new AntiBot());
      this.register(new Velocity());
      this.register(new AutoArmor());
      this.register(new AutoExplosion());
      this.register(new BackTrack());
      this.register(new Hitboxes());
      this.register(new ElytraTarget());
      this.register(new Criticals());
      this.register(new AutoSoup());
      this.register(new AutoSprint());
      this.register(new WindHop());
      this.register(new NoWeb());
      this.register(new Flight());
      this.register(new Speed());
      this.register(new Timer());
      this.register(new NoSlow());
      this.register(new Spider());
      this.register(new ElytraStrafe());
      this.register(new MenuModule());
      this.register(new Nametags());
      this.register(new Removals());
      this.register(new Ambience());
      this.register(new SwingAnimation());
      this.register(new SoundESP());
      this.register(new FriendMarkers());
      this.register(new Arrows());
      this.register(new TNTTimer());
      this.register(new ViewModel());
      this.register(new TrapESP());
      this.register(new Blink());
      this.register(new Interface());
      this.register(new TargetESP());
      this.register(new StorageESP());
      this.register(new XRay());
      this.register(new AntiInvisible());
      this.register(new CustomFog());
      this.register(new World());
      this.register(new KillEffects());
      this.register(new Prediction());
      this.register(new InventoryCleaner());
      this.register(new AutoInvisible());
      this.register(new MineHelper());
      this.register(new TargetPearl());
      this.register(new Stealer());
      this.register(new MiddleClick());
      this.register(new AutoBrew());
      this.register(new AutoFarm());
      this.register(new InvUtils());
      this.register(new AutoEat());
      this.register(new FreeCam());
      this.register(new NoDelay());
      this.register(new PlayerUtils());
      this.register(new NoPush());
      this.register(new ItemPickup());
      this.register(new Scaffold());
      this.register(new ObjectInfo());
      this.register(new CreeperFarm());
      this.register(new Nuker());
      this.register(new NoRotate());
      this.register(new NoInteract());
      this.register(new NoFall());
      this.register(new EffectRemover());
      this.register(new NameProtect());
      this.register(new ElytraUtils());
      this.register(new CounterMine());
      this.register(new FastItemUse());
      this.register(new AutoResell());
      this.register(new Panic());
      this.register(new Auction());
      this.register(new AutoAccept());
      this.register(new DeathCords());
      this.register(new AutoLeave());
      this.register(new AutoSwap());
      this.register(new RussianRoulette());
      this.register(new AutoDuels());
      this.register(new AutoAuth());
      this.register(new AutoJoin());
      this.register(new GuiMove());
      this.register(new Assist());
      this.register(new Sounds());
      this.register(new TestModule());
   }

   @CompileBytecode
   public void enableModules() {
      for (Module module : this.modules) {
         if (module.getInfo().enabledByDefault()) {
            module.enable();
         }
      }
   }

   public void register(BaseModule module) {
      this.modules.add(module);
   }

   public <T extends Module> T getModule(String name) {
      return (T)this.modules
         .stream()
         .filter(module -> module.getName().replace(" ", "").equalsIgnoreCase(name) || module.getName().equalsIgnoreCase(name))
         .findFirst()
         .orElseThrow(() -> new UnknownModuleException(name));
   }

   public <T extends Module> T getModule(Class<T> clazz) {
      return (T)this.modules
         .stream()
         .filter(module -> module.getClass().equals(clazz))
         .findFirst()
         .orElseThrow(() -> new UnknownModuleException(clazz.getSimpleName()));
   }

   @Generated
   public List<Module> getModules() {
      return this.modules;
   }

   @Generated
   public EventListener<ClientPlayerTickEvent> getTickListener() {
      return this.tickListener;
   }

   @Generated
   public EventListener<HudRenderEvent> getModuleWidgetRenderer() {
      return this.moduleWidgetRenderer;
   }

   @Generated
   public EventListener<KeyPressEvent> getOnKeyPress() {
      return this.onKeyPress;
   }

   @Generated
   public EventListener<MouseEvent> getOnMouseButtonPress() {
      return this.onMouseButtonPress;
   }
}
