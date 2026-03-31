package suck.enigma.ui.menu.api;

import suck.enigma.Enigma;
import suck.enigma.framework.base.UIContext;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.render.HudRenderEvent;
import suck.enigma.systems.modules.modules.visuals.MenuModule;
import suck.enigma.ui.menu.MenuScreen;
import suck.enigma.ui.menu.dropdown.DropDownScreen;
import suck.enigma.ui.menu.modern.ModernScreen;
import suck.enigma.utility.interfaces.IMinecraft;
import net.minecraft.client.MinecraftClient;

public class MenuCloseListener implements IMinecraft {
   private final EventListener<HudRenderEvent> onHudRender = event -> {
      MenuScreen menuScreen = Enigma.getInstance().getMenuScreen();
      if (mc.currentScreen == null) {
         if (Enigma.getInstance().getModuleManager().getModule(MenuModule.class).getModern().isSelected()) {
            if (!(menuScreen instanceof ModernScreen)) {
               Enigma.getInstance().setMenuScreen(new ModernScreen());
            }
         } else if (!(menuScreen instanceof DropDownScreen)) {
            Enigma.getInstance().setMenuScreen(new DropDownScreen());
         }
      }

      if (menuScreen != null) {
         menuScreen.getMenuAnimation().update(menuScreen.isClosing() ? 0.0F : 1.0F);
         if (!(mc.currentScreen instanceof MenuScreen) && Enigma.getInstance().getModuleManager().getModule(MenuModule.class).isEnabled()) {
            Enigma.getInstance().getModuleManager().getModule(MenuModule.class).setEnabled(false);
         }

         if (menuScreen.getMenuAnimation().getValue() > 0.1F && !(mc.currentScreen instanceof MenuScreen) && menuScreen.isClosing()) {
            UIContext context = UIContext.of(event.getContext(), -1, -1, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
            menuScreen.render(context);
         }
      }
   };

   public MenuCloseListener() {
      Enigma.getInstance().getEventManager().subscribe(this);
   }
}
