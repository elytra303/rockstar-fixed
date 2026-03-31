package suck.enigma.systems.modules.modules.player;

import suck.enigma.Enigma;
import suck.enigma.systems.event.EventIntegration;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.window.KeyPressEvent;
import suck.enigma.systems.event.impl.window.MouseEvent;
import suck.enigma.systems.friends.FriendManager;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.BindSetting;
import suck.enigma.systems.setting.settings.SelectSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

@ModuleInfo(name = "Middle Click", category = ModuleCategory.PLAYER, desc = "Выполняет действие при нажатии на колесико мыши")
public class MiddleClick extends BaseModule {
   private final SelectSetting actions = new SelectSetting(this, "Действие").min(1);
   private final SelectSetting.Value clickPearl = new SelectSetting.Value(this.actions, "Бросать жемчуг").select();
   private final SelectSetting.Value clickFriend = new SelectSetting.Value(this.actions, "Добавлять друзей");
   private final BindSetting clickFriendKey = new BindSetting(this, "Клавиша друзей", () -> !this.clickFriend.isSelected());
   private final BindSetting clickPearlKey = new BindSetting(this, "Клавиша перла", () -> !this.clickPearl.isSelected());
   private final EventListener<KeyPressEvent> onKeyPressEvent = event -> this.handleKey(event.getKey(), event.getAction());
   private final EventListener<MouseEvent> onMouseEvent = event -> this.handleKey(event.getButton(), event.getAction());

   private void handleKey(int key, int action) {
      if (mc.currentScreen == null && action == 1) {
         if (this.clickFriend.isSelected() && this.clickFriendKey.isKey(key) && mc.targetedEntity instanceof PlayerEntity) {
            String nick = mc.targetedEntity.getName().getString();
            FriendManager friend = enigma.getInstance().getFriendManager();
            if (friend.isFriend(nick)) {
               friend.remove(nick);
            } else {
               friend.add(nick);
            }
         }

         if (this.clickPearl.isSelected() && this.clickPearlKey.isKey(key)) {
            EventIntegration.SWAP_INTEGRATION.useItem(Items.ENDER_PEARL);
         }
      }
   }
}
