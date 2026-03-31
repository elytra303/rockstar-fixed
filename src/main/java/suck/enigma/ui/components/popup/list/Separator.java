package suck.enigma.ui.components.popup.list;

import suck.enigma.framework.base.UIContext;
import suck.enigma.ui.components.popup.PopupComponent;
import suck.enigma.utility.colors.Colors;

public class Separator extends PopupComponent {
   @Override
   protected void renderComponent(UIContext context) {
      context.drawRect(this.x, this.y, this.width, this.height, Colors.getSeparatorColor());
   }

   @Override
   public float getHeight() {
      return this.height = 4.0F;
   }
}
