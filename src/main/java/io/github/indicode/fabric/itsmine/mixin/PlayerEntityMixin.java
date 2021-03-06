package io.github.indicode.fabric.itsmine.mixin;

import io.github.indicode.fabric.itsmine.Claim;
import io.github.indicode.fabric.itsmine.ClaimManager;
import io.github.indicode.fabric.itsmine.ClaimShower;
import io.github.indicode.fabric.itsmine.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Indigo Amann
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ClaimShower {
    @Shadow protected abstract void vanishCursedItems();

    @Shadow @Final public PlayerInventory inventory;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }
    private Claim shownClaim = null;
    private BlockPos lastShowPos = null;

    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Z"))
    private boolean dontYouDareTouchMe(Entity entity, PlayerEntity playerEntity_1, Hand hand_1) {
        if (entity.world.isClient()) return entity.interact(playerEntity_1, hand_1);
        Claim claim = ClaimManager.INSTANCE.getClaimAt(entity.getSenseCenterPos(), entity.world.getDimension().getType());
        if (claim != null) {
            if (!claim.hasPermission(playerEntity_1.getGameProfile().getId(), Claim.Permission.INTERACT_ENTITY)) {
                playerEntity_1.sendMessage(new LiteralText("").append(new LiteralText(Config.msg_interact_entity).formatted(Formatting.RED)));
                return false;
            }
        }
        return entity.interact(playerEntity_1, hand_1);
    }
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void hittingIsRude(Entity entity, CallbackInfo ci) {
        if (entity.world.isClient()) return;
        PlayerEntity playerEntity_1 = (PlayerEntity)(Object)this;
        Claim claim = ClaimManager.INSTANCE.getClaimAt(entity.getSenseCenterPos(), entity.world.getDimension().getType());
        if (claim != null) {
            if (!claim.hasPermission(playerEntity_1.getGameProfile().getId(), Claim.Permission.ENTITY_DAMAGE)) {
                playerEntity_1.sendMessage(new LiteralText("").append(new LiteralText(Config.msg_attack_entity).formatted(Formatting.RED)));
                ci.cancel();
            }
        }
    }

    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    public void noTakieMyThingies(CallbackInfo ci) {
        ci.cancel();

        super.dropInventory();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            Claim claim = ClaimManager.INSTANCE.getClaimAt(this.getSenseCenterPos(), this.world.getDimension().getType());
            if (claim != null && !claim.settings.getSetting(Claim.ClaimSettings.Setting.KEEP_INVENTORY)) {
                this.vanishCursedItems();
                this.inventory.dropAll();
                ci.cancel();
            }
        }
    }

    @Override
    public void setLastShowPos(BlockPos pos) {
        lastShowPos = pos;
    }

    /*@Inject(method = "canPlaceOn", at = @At("HEAD"))
        public void iDontWantYerStuff(BlockPos blockPos_1, Direction direction_1, ItemStack itemStack_1, CallbackInfoReturnable<Boolean> cir) {
            Claim claim = ClaimManager.INSTANCE.getClaimAt(blockPos_1, world.getDimension().getType());
            if (claim != null) {
                cir.setReturnValue(false);
            }
        }*/ // Replace with specific undos on certain methods(buttons, containers, etc)
    @Override
    public void setShownClaim(Claim claim) {
        shownClaim = claim;
    }
    @Override
    public Claim getShownClaim() {
        return shownClaim;
    }

    @Override
    public BlockPos getLastShowPos() {
        return lastShowPos;
    }
}
