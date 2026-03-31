package suck.enigma.ui.hud.impl.island.impl;

import java.util.List;
import suck.enigma.enigma;
import suck.enigma.framework.base.CustomDrawContext;
import suck.enigma.framework.msdf.Fonts;
import suck.enigma.framework.objects.BorderRadius;
import suck.enigma.systems.notifications.Notification;
import suck.enigma.systems.setting.settings.SelectSetting;
import suck.enigma.ui.hud.impl.island.DynamicIsland;
import suck.enigma.ui.hud.impl.island.IslandStatus;
import suck.enigma.utility.colors.Colors;

public class NotificationStatus extends IslandStatus {
   public NotificationStatus(SelectSetting setting) {
      super(setting, "alerts");
   }

   @Override
   public void draw(CustomDrawContext context) {
      DynamicIsland island = enigma.getInstance().getHud().getIsland();
      List<Notification> notifications = enigma.getInstance().getNotificationManager().getNotifications();
      if (!notifications.isEmpty()) {
         Notification active = notifications.getLast();
         float x = sr.getScaledWidth() / 2.0F - island.getSize().width / 2.0F;
         float y = 7.0F;
         float width = this.size.width = 18.0F + Fonts.MEDIUM.getFont(7.0F).width(active.getText());
         float height = this.size.height = 15.0F;

         for (Notification notification : notifications) {
            notification.getShowing().setDuration(500L);
            notification.getShowing().update(active == notification);
            context.drawRoundedRect(
               x - 6.0F + 10.0F * this.animation.getValue() * notification.getShowing().getValue(),
               y + 4.0F,
               7.0F,
               7.0F,
               BorderRadius.all(3.0F),
               notification.getType().getColor().withAlpha(255.0F * notification.getShowing().getValue())
            );
            context.drawText(
               Fonts.MEDIUM.getFont(7.0F),
               notification.getText(),
               x + 25.0F - 10.0F * this.animation.getValue() * notification.getShowing().getValue(),
               y + 5.0F,
               Colors.getTextColor().withAlpha(255.0F * notification.getShowing().getValue())
            );
         }
      }
   }

   @Override
   public boolean canShow() {
      List<Notification> notifications = enigma.getInstance().getNotificationManager().getNotifications();
      return !notifications.isEmpty() && !notifications.getLast().getTimer().finished(notifications.getLast().getDuration());
   }
}
