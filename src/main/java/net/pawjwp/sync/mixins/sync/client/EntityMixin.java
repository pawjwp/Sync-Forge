package net.pawjwp.sync.mixins.sync.client;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.pawjwp.sync.common.entity.LookingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void sync$changeLookDirection(double yaw, double pitch, CallbackInfo ci) {
        if (this instanceof LookingEntity && ((LookingEntity)this).changeLookingEntityLookDirection(yaw, pitch)) {
            ci.cancel();
        }
    }
}