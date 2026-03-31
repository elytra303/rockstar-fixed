package suck.enigma.systems.modules.constructions.swinganim;

import java.util.ArrayList;
import java.util.List;
import suck.enigma.systems.setting.Setting;
import suck.enigma.systems.setting.SettingsContainer;

public class SwingSettings implements SettingsContainer {
   protected final List<Setting> settings = new ArrayList<>();

   @Override
   public List<Setting> getSettings() {
      return this.settings;
   }
}
