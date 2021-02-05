package tallestegg.guardvillagers.entities.ai.goals;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;

public class HelpVillagerGoal extends TargetGoal {
    protected final MobEntity mob;
    protected LivingEntity villageAggressorTarget;

    public HelpVillagerGoal(MobEntity mob) {
        super(mob, false, true);
        this.mob = mob;
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET, Goal.Flag.MOVE));
    }

    public boolean shouldExecute() {
        List<MobEntity> list = this.goalOwner.world.getEntitiesWithinAABB(MobEntity.class, this.goalOwner.getBoundingBox().grow(50.0D));

        for (MobEntity entity : list) {
            LivingEntity attackTarget = entity.getAttackTarget();
            if ((entity).getAttackTarget() instanceof VillagerEntity && entity.canEntityBeSeen(attackTarget)) {
                this.villageAggressorTarget = entity;
            }
        }

        if (this.villageAggressorTarget == null) {
            return false;
        } else {
            return !this.villageAggressorTarget.isSpectator();
        }
    }

    @Override
    protected double getTargetDistance() {
        return villageAggressorTarget.getDistance(goalOwner) + 10.0F; // broadcasts location
    }

    @Override
    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.villageAggressorTarget);
    }
}