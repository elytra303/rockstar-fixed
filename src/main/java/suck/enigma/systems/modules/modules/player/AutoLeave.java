package suck.enigma.systems.modules.modules.player;

import suck.enigma.enigma;
import suck.enigma.systems.event.EventListener;
import suck.enigma.systems.event.impl.network.ReceivePacketEvent;
import suck.enigma.systems.event.impl.player.ClientPlayerTickEvent;
import suck.enigma.systems.localization.Localizator;
import suck.enigma.systems.modules.api.ModuleCategory;
import suck.enigma.systems.modules.api.ModuleInfo;
import suck.enigma.systems.modules.impl.BaseModule;
import suck.enigma.systems.setting.settings.ModeSetting;
import suck.enigma.systems.setting.settings.SliderSetting;
import suck.enigma.utility.game.TextUtility;
import suck.enigma.utility.game.server.ServerUtility;
import suck.enigma.utility.time.Timer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

@ModuleInfo(name = "Auto Leave", category = ModuleCategory.PLAYER)
public class AutoLeave extends BaseModule {
   private final ModeSetting leave = new ModeSetting(this, "modules.settings.auto_leave.leave");
   private final ModeSetting.Value distLeave = new ModeSetting.Value(this.leave, "modules.settings.auto_leave.leave.distance");
   private final ModeSetting.Value healthLeave = new ModeSetting.Value(this.leave, "modules.settings.auto_leave.leave.health");
   private final ModeSetting.Value banLeave = new ModeSetting.Value(this.leave, "modules.settings.auto_leave.leave.ban");
   private final SliderSetting dist = new SliderSetting(
         this, "modules.settings.auto_leave.distance", () -> this.healthLeave.isSelected() || this.banLeave.isSelected()
      )
      .suffix(number -> " %s".formatted(Localizator.translate("block")) + TextUtility.makeCountTranslated(number))
      .step(1.0F)
      .min(1.0F)
      .max(150.0F)
      .currentValue(30.0F);
   private final SliderSetting health = new SliderSetting(
         this, "modules.settings.auto_leave.health", () -> this.distLeave.isSelected() || this.banLeave.isSelected()
      )
      .step(1.0F)
      .min(1.0F)
      .max(20.0F)
      .currentValue(10.0F);
   private final SliderSetting delay = new SliderSetting(
         this, "modules.settings.auto_leave.delay", () -> !this.banLeave.isSelected() || this.distLeave.isSelected() || this.healthLeave.isSelected()
      )
      .suffix(Localizator.translate("sec") + ".")
      .step(1.0F)
      .min(1.0F)
      .max(60.0F)
      .currentValue(40.0F);
   private final Timer timer = new Timer();
   private boolean waiting;
   private final ModeSetting mode = new ModeSetting(this, "modules.settings.auto_leave.mode");
   private final ModeSetting.Value hub = new ModeSetting.Value(this.mode, "modules.settings.auto_leave.mode.hub");
   private final ModeSetting.Value serverLeave = new ModeSetting.Value(this.mode, "modules.settings.auto_leave.mode.server");
   private final ModeSetting.Value spawn = new ModeSetting.Value(this.mode, "modules.settings.auto_leave.mode.spawn");
   private final EventListener<ClientPlayerTickEvent> onClientPlayerTickEvent = event -> {
      if (this.distLeave.isSelected()) {
         for (PlayerEntity e : mc.world.getPlayers()) {
            if (e != null
               && e != mc.player
               && !(e.distanceTo(e) > this.dist.getCurrentValue())
               && mc.player != null
               && !ServerUtility.hasCT
               && !enigma.getInstance().getFriendManager().isFriend(e.getName().getString())) {
               if (this.hub.isSelected()) {
                  mc.player.networkHandler.sendChatCommand("hub");
               } else if (this.serverLeave.isSelected()) {
                  mc.player.networkHandler.getConnection().disconnect(Text.of(Localizator.translate("modules.auto_leave.near_player")));
               } else if (this.spawn.isSelected()) {
                  mc.player.networkHandler.sendChatCommand("spawn");
               }

               this.toggle();
               break;
            }
         }
      }

      if (this.healthLeave.isSelected() && mc.player != null && mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.health.getCurrentValue()) {
         if (this.hub.isSelected()) {
            mc.player.networkHandler.sendChatCommand("hub");
         } else if (this.serverLeave.isSelected()) {
            mc.player.networkHandler.getConnection().disconnect(Text.of(Localizator.translate("modules.auto_leave.low_health")));
         } else if (this.spawn.isSelected()) {
            mc.player.networkHandler.sendChatCommand("spawn");
         }

         this.toggle();
      }

      if (this.waiting) {
         if (this.timer.finished((long)this.delay.getCurrentValue() * 1000L)) {
            mc.player.networkHandler.sendChatCommand("an" + ServerUtility.ftAn);
            this.waiting = false;
         }
      }
   };
   private final EventListener<ReceivePacketEvent> onReceivePacketEvent = event -> {
      if (event.getPacket() instanceof GameMessageS2CPacket packet
         && packet.content().getString().contains(Localizator.translate("modules.auto_leave.banned_word"))
         && this.banLeave.isSelected()) {
         mc.player.networkHandler.sendChatCommand("hub");
         this.timer.reset();
         this.waiting = true;
      }
   };
}
