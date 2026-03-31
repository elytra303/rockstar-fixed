package suck.enigma.ui.menu.dropdown.components.settings;

import lombok.Generated;
import suck.enigma.Enigma;
import suck.enigma.framework.base.CustomComponent;
import suck.enigma.framework.base.UIContext;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.setting.Setting;
import suck.enigma.ui.components.popup.Popup;
import suck.enigma.ui.menu.dropdown.DropDownScreen;
import suck.enigma.ui.menu.dropdown.components.module.ModuleComponent;
import suck.enigma.utility.animation.base.Animation;
import suck.enigma.utility.animation.base.Easing;

public abstract class MenuSettingComponent<T extends Setting> extends CustomComponent {
   private final CustomComponent parent;
   protected final T setting;
   private final Animation visibilityAnimation = new Animation(300L, Easing.BAKEK_PAGES);
   protected final Animation hoverAnimation = new Animation(300L, Easing.FIGMA_EASE_IN_OUT);

   public MenuSettingComponent(T setting, CustomComponent parent) {
      this.parent = parent;
      this.setting = setting;
   }

   @Override
   public void update(UIContext context) {
      String translatedDescription = Localizator.translateOrEmpty(this.setting.getDescription());
      if (this.parent instanceof ModuleComponent component
         && (component.getParent().isHovered(context) && this.isHovered(context) || Enigma.getInstance().getMenuScreen() instanceof DropDownScreen)) {
         ((DropDownScreen)Enigma.getInstance().getMenuScreen()).setDesc(Localizator.translate(translatedDescription));
      }

      if (this.parent instanceof Popup && this.isHovered(context)) {
         Enigma.getInstance().getHud().setDesc(Localizator.translate(translatedDescription));
      }

      super.update(context);
   }

   @Override
   public void onInit() {
      super.onInit();
   }

   public float getOpacity() {
      return this.visibilityAnimation.getValue();
   }

   public void drawRegular8(UIContext context) {
   }

   public void drawSplit(UIContext context) {
   }

   @Generated
   public CustomComponent getParent() {
      return this.parent;
   }

   @Generated
   public T getSetting() {
      return this.setting;
   }

   @Generated
   public Animation getVisibilityAnimation() {
      return this.visibilityAnimation;
   }

   @Generated
   public Animation getHoverAnimation() {
      return this.hoverAnimation;
   }
}
