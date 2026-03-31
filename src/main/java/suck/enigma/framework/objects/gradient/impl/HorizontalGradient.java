package suck.enigma.framework.objects.gradient.impl;

import suck.enigma.framework.objects.gradient.Gradient;
import suck.enigma.utility.colors.ColorRGBA;

public class HorizontalGradient extends Gradient {
   public HorizontalGradient(ColorRGBA startColor, ColorRGBA endColor) {
      super(startColor, startColor, endColor, endColor);
   }

   public HorizontalGradient rotate() {
      return new HorizontalGradient(this.bottomRightColor, this.topLeftColor);
   }
}
