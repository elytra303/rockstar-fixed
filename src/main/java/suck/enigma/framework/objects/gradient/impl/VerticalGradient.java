package suck.enigma.framework.objects.gradient.impl;

import suck.enigma.framework.objects.gradient.Gradient;
import suck.enigma.utility.colors.ColorRGBA;

public class VerticalGradient extends Gradient {
   public VerticalGradient(ColorRGBA startColor, ColorRGBA endColor) {
      super(startColor, endColor, startColor, endColor);
   }

   public VerticalGradient rotate() {
      return new VerticalGradient(this.bottomRightColor, this.topLeftColor);
   }
}
