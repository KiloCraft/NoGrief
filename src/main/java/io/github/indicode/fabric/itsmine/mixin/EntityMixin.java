package io.github.indicode.fabric.itsmine.mixin;

import io.github.indicode.fabric.itsmine.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public int timeUntilRegen;

    @Shadow public abstract double offsetX(double widthScale);

    @Shadow protected UUID uuid;
    @Shadow private double x;
    @Shadow private double z;
    @Shadow private double y;
    @Shadow private Vec3d field_22467;
    @Shadow private BlockPos field_22468;
    @Shadow public boolean ignoreCameraFrustum;
    private Claim pclaim = null;
    @Inject(method = "setPos", at = @At("HEAD"))
    public void doPrePosActions(double x, double y, double z, CallbackInfo ci) {
        if (!world.isClient && (Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (player.getSenseCenterPos() == null) return;
            pclaim = ClaimManager.INSTANCE.getClaimAt(player.getSenseCenterPos(), player.world.dimension.getType());
        }
    }
    @Inject(method = "setPos", at = @At("RETURN"))
    public void doPostPosActions(double x, double y, double z, CallbackInfo ci) {
        if (!world.isClient && (Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (player.getSenseCenterPos() == null) return;
            Claim claim = ClaimManager.INSTANCE.getClaimAt(player.getSenseCenterPos(), player.world.dimension.getType());

            if ((Object) this instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;

                if (claim != null && !claim.permissionManager.hasPermission(player.getUuid(), Claim.Permission.ENTER)) {
                    this.x = x;
                    this.y = y;
                    this.z = z;
                    this.field_22467 = new Vec3d(x, y, z);
                    this.field_22468 = new BlockPos(x, y, z);
                    serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new LiteralText(ChatColor.translate(Config.msg_cant_enter))));

                    return;
                }

                if (pclaim != claim) {
                    if (serverPlayerEntity.networkHandler != null) {
                        String message = null;
                        if (claim == null && pclaim != null) message = getFormattedEventMessage(player, pclaim, false);
                        else if (claim != null) message = getFormattedEventMessage(player, claim, true);

                        if (message != null)
                            serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new LiteralText(ChatColor.translate(message)), -1, Config.event_msg_stay_ticks, -1));
                    }
                }
            }
        }
    }

    private String getFormattedEventMessage(PlayerEntity player, Claim claim, boolean enter) {
        String str = enter ? claim.enterMessage : claim.leaveMessage;
        return  ChatColor.translate(str == null ? (enter ? Config.msg_enter_default : Config.msg_leave_default) : str).replace("%claim%", claim.name)
                .replace("%player%", player.getEntityName());
    }

    private int tick = 0;
    @Inject(method = "tick", at = @At("RETURN"))
    public void lemmeFlyToTheSkies(CallbackInfo ci) {
        if (tick >= 8 && !world.isClient && (Object) this instanceof PlayerEntity) {
            tick = 0;

            PlayerEntity player = (PlayerEntity) (Object)this;
            if (player.getSenseCenterPos() == null) return;
            boolean old = player.abilities.allowFlying;
            Claim claim = ClaimManager.INSTANCE.getClaimAt(player.getSenseCenterPos(), player.world.dimension.getType());
            if (player instanceof ServerPlayerEntity) {
                if (player.abilities.allowFlying && ((claim == null || !claim.settings.getSetting(Claim.ClaimSettings.Setting.FLIGHT_ALLOWED) || !claim.hasPermission(player.getGameProfile().getId(), Claim.Permission.FLIGHT)) && Functions.isClaimFlying(player.getGameProfile().getId()))) {
                    player.abilities.allowFlying = false;
                    player.abilities.flying = false;
                    Functions.setClaimFlying(player.getGameProfile().getId(), false);
                } else if (!player.abilities.allowFlying && claim != null && claim.settings.getSetting(Claim.ClaimSettings.Setting.FLIGHT_ALLOWED) && claim.hasPermission(player.getGameProfile().getId(), Claim.Permission.FLIGHT) && Functions.canClaimFly((ServerPlayerEntity) player)) {
                    player.abilities.allowFlying = true;
                    Functions.setClaimFlying(player.getGameProfile().getId(), true);
                }
                if (player.abilities.allowFlying != old) {
                    player.sendAbilitiesUpdate();
                }
            }

        }

        tick++;
    }
}
