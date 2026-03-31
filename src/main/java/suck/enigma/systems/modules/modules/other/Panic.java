package suck.enigma.systems.modules.modules.other;

import java.nio.file.Path;
import suck.enigma.enigma;
import suck.enigma.systems.modules.Module;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.utility.game.TitleBarHelper;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.minecraft.client.util.Icons;

@ModuleInfo(name = "Panic", category = ModuleCategory.OTHER, desc = "modules.descriptions.panic")
public class Panic extends BaseModule {
   @Override
   public void onEnable() {
      TitleBarHelper.setLightTitleBar();
      enigma.getInstance().setPanic(true);
      enigma.getInstance().getFileManager().saveClientFiles();

      for (Module module : enigma.getInstance().getModuleManager().getModules()) {
         module.setKey(-1);
         module.disable();
      }

      try {
         mc.getWindow().setIcon(mc.getDefaultResourcePack(), Icons.RELEASE);
      } catch (Exception var4) {
      }

      ModContainerImpl enigmaMod = this.getenigmaMod();
      if (enigmaMod != null) {
         for (Path path : this.getenigmaMod().getOrigin().getPaths()) {
            path.toFile().delete();
         }

         FabricLoaderImpl.INSTANCE.getModsInternal().remove(this.getenigmaMod());
      }

      super.onEnable();
   }

   private ModContainerImpl getenigmaMod() {
      return FabricLoaderImpl.INSTANCE
            .getAllMods()
            .stream()
            .filter(modContainer -> modContainer.getMetadata().getId().equals(enigma.MOD_ID))
            .map(m -> (ModContainerImpl) m)
            .findFirst()
            .orElse(null);
   }
}
