package suck.enigma.systems.modules.modules.player;

import java.util.function.Predicate;
import suck.enigma.enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.game.StartBreakBlockEvent;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.event.impl.window.KeyPressEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.notifications.NotificationType;
import suck.enigma.systems.setting.settings.BindSetting;
import suck.enigma.systems.setting.settings.BooleanSetting;
import suck.enigma.systems.setting.settings.SliderSetting;
import suck.enigma.utility.inventory.InventoryUtility;
import suck.enigma.utility.inventory.ItemSlot;
import suck.enigma.utility.inventory.group.SlotGroup;
import suck.enigma.utility.inventory.group.SlotGroups;
import suck.enigma.utility.inventory.slots.HotbarSlot;
import suck.enigma.utility.rotations.MoveCorrection;
import suck.enigma.utility.rotations.Rotation;
import suck.enigma.utility.rotations.RotationHandler;
import suck.enigma.utility.rotations.RotationPriority;
import suck.enigma.utility.time.Timer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(name = "Mine Helper", category = ModuleCategory.PLAYER, desc = "Помощник в шахте")
public class MineHelper extends BaseModule {
   private final BooleanSetting save = new BooleanSetting(this, "Сохранять кирку", "Не дает сломать блок, если предмет достиг определенной прочности").enable();
   public final SliderSetting percent = new SliderSetting(this, "Прочность").step(1.0F).min(1.0F).max(70.0F).currentValue(10.0F).suffix("%");
   private final BooleanSetting autoReplace = new BooleanSetting(this, "Авто замена", "Автоматически меняет кирку при низкой прочности");
   private final BooleanSetting autoRepair = new BooleanSetting(this, "Авто починка", "Чинит кирку с использованием опыта");
   private final BindSetting bind = new BindSetting(this, "Кнопка починки", () -> !this.autoRepair.isEnabled());
   private final Timer timer = new Timer();
   private boolean rotate;
   private final EventListener<KeyPressEvent> onKeyPressEvent = event -> {
      if (this.bind.isKey(event.getKey()) && event.getAction() == 1) {
         this.rotate = true;
         this.repairPickaxeWithBottle();
      }
   };
   private final EventListener<StartBreakBlockEvent> onStartBreakBlockEvent = event -> {
      if (mc.player != null) {
         ItemStack currentStack = mc.player.getMainHandStack();
         if (this.isValidPickaxe(currentStack)) {
            double durabilityPercent = this.getDurabilityPercent(currentStack);
            if (this.save.isEnabled() && !(durabilityPercent >= this.percent.getCurrentValue())) {
               event.cancel();
               this.handleLowDurability(currentStack);
            }
         }
      }
   };
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (mc.player != null && this.rotate) {
         enigma.getInstance()
            .getRotationHandler()
            .rotate(new Rotation(mc.player.getYaw(), 90.0F), MoveCorrection.SILENT, 180.0F, 180.0F, 180.0F, RotationPriority.USE_ITEM);
      }
   };

   private void handleLowDurability(ItemStack currentStack) {
      boolean switched = false;
      if (this.autoReplace.isEnabled()) {
         switched = this.trySwitchPickaxe(currentStack);
      }

      if (!switched && this.timer.finished(800L)) {
         enigma.getInstance().getNotificationManager().addNotificationOther(NotificationType.ERROR, "Кирка почти сломана!", "Нет замены/опыта для починки");
         this.timer.reset();
      }
   }

   private void repairPickaxeWithBottle() {
      if (mc.player != null && mc.currentScreen == null) {
         ItemStack pickaxe = mc.player.getMainHandStack();
         if (!this.isValidPickaxe(pickaxe) || pickaxe.getDamage() == 0) {
            enigma.getInstance().getNotificationManager().addNotificationOther(NotificationType.ERROR, "Ошибка", "Нет кирки или она не повреждена!");
            this.rotate = false;
         } else if (this.ensureBottleInOffhand()) {
            this.useExperienceBottle();
         }
      }
   }

   private boolean ensureBottleInOffhand() {
      ItemStack offhand = mc.player.getOffHandStack();
      if (offhand.getItem() == Items.EXPERIENCE_BOTTLE) {
         return true;
      } else {
         SlotGroup<ItemSlot> searchArea = SlotGroups.inventory().and(SlotGroups.hotbar());
         ItemSlot bottleSlot = searchArea.findItem((Predicate<ItemStack>)(stack -> stack.getItem() == Items.EXPERIENCE_BOTTLE));
         if (bottleSlot == null) {
            enigma.getInstance()
               .getNotificationManager()
               .addNotificationOther(NotificationType.ERROR, "Нет бутылок опыта!", "Вам необходимо иметь бутылочки опыта в инвентаре");
            this.rotate = false;
            return false;
         } else {
            InventoryUtility.moveItem(bottleSlot, InventoryUtility.getOffHandSlot());
            return true;
         }
      }
   }

   private void useExperienceBottle() {
      if (mc.player.getOffHandStack().getItem() != Items.EXPERIENCE_BOTTLE) {
         mc.options.useKey.setPressed(false);
         this.rotate = false;
      } else {
         RotationHandler rotation = enigma.getInstance().getRotationHandler();
         mc.interactionManager
            .sendSequencedPacket(
               mc.world,
               sequence -> new PlayerInteractItemC2SPacket(
                  Hand.OFF_HAND, sequence, rotation.getServerRotation().getYaw(), rotation.getServerRotation().getYaw()
               )
            );
      }
   }

   private boolean trySwitchPickaxe(ItemStack currentStack) {
      HotbarSlot bestSlot = this.findBestPickaxeSlot(currentStack);
      if (bestSlot == null) {
         return false;
      } else {
         InventoryUtility.selectHotbarSlot(bestSlot);
         if (this.timer.finished(800L)) {
            ItemStack newStack = bestSlot.itemStack();
            enigma.getInstance()
               .getNotificationManager()
               .addNotificationOther(
                  NotificationType.SUCCESS,
                  "Замена кирки",
                  String.format("Заменил кирку с %.1f%% на %.1f%%", this.getDurabilityPercent(currentStack), this.getDurabilityPercent(newStack))
               );
            this.timer.reset();
         }

         return true;
      }
   }

   private HotbarSlot findBestPickaxeSlot(ItemStack currentStack) {
      double currentDurability = this.getDurabilityPercent(currentStack);
      HotbarSlot bestSlot = null;
      double bestDurability = currentDurability;

      for (int i = 0; i < 9; i++) {
         HotbarSlot slot = InventoryUtility.getHotbarSlot(i);
         ItemStack stack = slot.itemStack();
         if (this.isValidPickaxe(stack)) {
            double durability = this.getDurabilityPercent(stack);
            if (durability > bestDurability) {
               bestDurability = durability;
               bestSlot = slot;
            }
         }
      }

      return bestSlot;
   }

   private boolean isValidPickaxe(ItemStack stack) {
      return stack != null && stack.isDamageable() && stack.getItem() instanceof PickaxeItem;
   }

   private double getDurabilityPercent(ItemStack stack) {
      return (double)(stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage() * 100.0;
   }
}
