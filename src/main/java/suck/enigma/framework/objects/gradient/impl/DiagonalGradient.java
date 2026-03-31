package suck.enigma.framework.objects.gradient.impl;

import suck.enigma.framework.objects.gradient.Gradient;
import suck.enigma.utility.colors.ColorRGBA;

class DiagonalGradient extends Gradient {
   public DiagonalGradient(ColorRGBA startColor, ColorRGBA endColor) {
      super(startColor, endColor, endColor, startColor);
   }

   public DiagonalGradient rotate() {
      return new DiagonalGradient(this.topRightColor, this.bottomLeftColor);
   }
}
