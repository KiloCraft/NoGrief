package io.github.indicode.fabric.itsmine.mixin;

import blue.endless.jankson.annotation.Nullable;
import io.github.indicode.fabric.itsmine.claim.Claim;
import io.github.indicode.fabric.itsmine.ClaimManager;
import io.github.indicode.fabric.itsmine.Config;
import io.github.indicode.fabric.itsmine.util.ClaimUtil;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin {

    @Shadow @Nullable
    public abstract ServerPlayerEntity getLovingPlayer();

    @Shadow public abstract void resetLoveTicks();

    @Inject(method = "canBreedWith", at = @At(value = "HEAD"), cancellable = true)
    private void canBreed(AnimalEntity other, CallbackInfoReturnable<Boolean> cir){
        World world = other.getEntityWorld();
        Claim claim = ClaimManager.INSTANCE.getClaimAt(other.getBlockPos(), other.dimension);
        if(claim != null){
            if(claim.isChild){
                claim = ClaimUtil.getParentClaim(claim);
            }
            if(claim.getEntities(other.getEntityWorld().getServer().getWorld(other.getEntityWorld().getDimension().getType())) > Config.claim_max_entities_passive){
                ServerPlayerEntity player = this.getLovingPlayer();
                if(player != null){
                    player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new LiteralText("You reached the passive entity limit in your claim!").formatted(Formatting.RED), -1, Config.event_msg_stay_ticks, -1));
                }
                this.resetLoveTicks();
                cir.setReturnValue(false);
            }
        }
    }
}
