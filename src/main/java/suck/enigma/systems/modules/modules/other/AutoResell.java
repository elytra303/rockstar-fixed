package suck.enigma.systems.modules.modules.other;

import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.utility.game.server.ServerUtility;
import suck.enigma.utility.time.Timer;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

@ModuleInfo(name = "Auto Resell", category = ModuleCategory.OTHER, desc = "modules.descriptions.auto_resell")
public class AutoResell extends BaseModule {
   private final Timer openTimer = new Timer();
   private final Timer clickTimer = new Timer();
   private boolean isAutoProcess;
   private boolean auctionHandled;
   private boolean storageHandled;
   private final EventListener<ClientPlayerTickEvent> onUpdateEvent = event -> {
      if (mc.player != null && mc.interactionManager != null && ServerUtility.isFT()) {
         if (this.openTimer.finished(60000L)) {
            if (!this.isAuctionOrStorageOpen()) {
               mc.player.networkHandler.sendChatCommand("ah " + mc.player.getName().getString());
               this.isAutoProcess = true;
               this.clickTimer.reset();
            }

            this.openTimer.reset();
         }

         this.handleAuctionAndStorage();
      }
   };

   private void handleAuctionAndStorage() {
      boolean isAuctionOpen = this.isTitleContains("аукционы");
      boolean isStorageOpen = this.isTitleContains("хранилище");
      if (this.isAutoProcess && isAuctionOpen && !this.auctionHandled && this.clickTimer.finished(300L)) {
         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 46, 0, SlotActionType.PICKUP, mc.player);
         this.auctionHandled = true;
         this.clickTimer.reset();
      }

      if (this.isAutoProcess && isStorageOpen && !this.storageHandled && this.clickTimer.finished(300L)) {
         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 52, 0, SlotActionType.PICKUP, mc.player);
         mc.player.closeHandledScreen();
         this.storageHandled = true;
         this.isAutoProcess = false;
         this.clickTimer.reset();
      }

      if (!isAuctionOpen) {
         this.auctionHandled = false;
      }

      if (!isStorageOpen) {
         this.storageHandled = false;
      }
   }

   private boolean isAuctionOrStorageOpen() {
      return this.isTitleContains("аукционы") || this.isTitleContains("хранилище");
   }

   private boolean isTitleContains(String string) {
      if (mc.currentScreen != null && mc.player != null) {
         String title = mc.currentScreen.getTitle().getString().toLowerCase();
         return mc.player.currentScreenHandler instanceof GenericContainerScreenHandler && title.contains(string.toLowerCase());
      } else {
         return false;
      }
   }
}
