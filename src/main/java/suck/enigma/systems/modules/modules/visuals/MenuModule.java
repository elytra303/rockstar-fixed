package suck.enigma.systems.modules.modules.visuals;

import lombok.Generated;
import suck.enigma.enigma;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.modules.modules.other.Sounds;
import suck.enigma.systems.setting.settings.ModeSetting;
import suck.enigma.ui.menu.MenuScreen;
import suck.enigma.ui.menu.api.MenuCloseListener;
import suck.enigma.utility.sounds.ClientSounds;

@ModuleInfo(name = "Menu", category = ModuleCategory.VISUALS, key = 344, desc = "modules.descriptions.menu")
public class MenuModule extends BaseModule {
   private static final MenuCloseListener menuCloseListener = new MenuCloseListener();
   private final ModeSetting mode = new ModeSetting(this, "modules.settings.menu.mode");
   private final ModeSetting.Value dropdown = new ModeSetting.Value(this.mode, "modules.settings.menu.mode.dropdown");
   private final ModeSetting.Value modern = new ModeSetting.Value(this.mode, "modules.settings.menu.mode.modern");

   @Override
   public void onEnable() {
      if (!(mc.currentScreen instanceof MenuScreen)) {
         MenuScreen menuScreen = enigma.getInstance().getMenuScreen();
         mc.setScreen(menuScreen);
         Sounds soundsModule = enigma.getInstance().getModuleManager().getModule(Sounds.class);
         if (soundsModule.isEnabled()) {
            ClientSounds.CLICKGUI_OPEN.play(soundsModule.getVolume().getCurrentValue());
         }

         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      if (mc.currentScreen instanceof MenuScreen) {
         mc.setScreen(null);
         enigma.getInstance().getMenuScreen().setClosing(true);
      }

      super.onDisable();
   }

   @Generated
   public ModeSetting.Value getModern() {
      return this.modern;
   }
}
