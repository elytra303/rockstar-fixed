package suck.enigma.systems.modules.modules.other;

import java.util.Map;
import java.util.Map.Entry;
import suck.enigma.enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.game.PickupEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.notifications.NotificationType;
import suck.enigma.utility.game.ItemUtility;
import suck.enigma.utility.game.MessageUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@ModuleInfo(name = "Item Pickup", category = ModuleCategory.OTHER, enabledByDefault = true, desc = "Уведомляет вас при поднятии донатного предмета")
public class ItemPickup extends BaseModule {
   private final Map<String, String> don = Map.of(
      "krush-helmet",
      "Вы подобрали Шлем крушителя!",
      "krush-chestplate",
      "Вы подобрали Нагрудник крушителя!",
      "krush-leggings",
      "Вы подобрали Поножи крушителя!",
      "krush-boots",
      "Вы подобрали Ботинки крушителя!",
      "krush-sword",
      "Вы подобрали донатный предмет: Меч крушителя"
   );
   private final EventListener<PickupEvent> onPickupEvent = event -> {
      ItemStack stack = event.getItemStack();

      for (Entry<String, String> entry : this.don.entrySet()) {
         if (ItemUtility.checkDonItem(stack, entry.getKey())) {
            MessageUtility.info(Text.of(entry.getValue()));
            return;
         }
      }

      if (ItemUtility.isDonItem(stack)) {
         String name = stack.getName().getString();
         enigma.getInstance()
            .getNotificationManager()
            .addNotificationOther(NotificationType.INFO, "Донатный предмет", "Вы подобрали донатный предмет: " + name);
      }
   };
}
