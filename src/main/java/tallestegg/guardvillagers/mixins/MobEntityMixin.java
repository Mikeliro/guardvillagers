package tallestegg.guardvillagers.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.entities.GuardEntity;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "onInitialSpawn", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    private void spawnGuards(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag, CallbackInfoReturnable<ILivingEntityData> cir) {
        if (this.getEntity() instanceof IronGolemEntity && reason == SpawnReason.STRUCTURE && ((ISeedReader)worldIn).func_241827_a(SectionPos.from(this.getPosition()), Structure.field_236381_q_).findAny().isPresent()) {
            BlockPos pos = this.getEntity().getPosition();
            for (int i=0; i<5; i++) {
                GuardEntity guard = GuardEntityType.GUARD.get().create(world);
                if (guard == null) return;
                guard.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
                guard.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
                worldIn.addEntity(guard);
            }
        }
    }
}
