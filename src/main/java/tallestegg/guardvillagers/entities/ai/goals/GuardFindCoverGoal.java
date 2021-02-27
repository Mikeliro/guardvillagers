package tallestegg.guardvillagers.entities.ai.goals;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;
import tallestegg.guardvillagers.entities.GuardEntity;

public class GuardFindCoverGoal extends RandomWalkingGoal {

    protected final GuardEntity guard;
    private int walkTimer;

    public GuardFindCoverGoal(GuardEntity guard, double speedIn) {
        super(guard, speedIn);
        this.guard = guard;
    }

    @Override
    public boolean shouldExecute() {
        return guard.getHealth() < guard.getMaxHealth() / 2 && this.findPosition() && GuardEatFoodGoal.isConsumable(guard.getHeldItemOffhand()) && !guard.isEating() && guard.getAttackTarget() != null;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (walkTimer <= 0)
            this.walkTimer = 100;
    }

    @Override
    public void tick() {
        super.tick();
        List<LivingEntity> list = this.guard.world.getEntitiesWithinAABB(LivingEntity.class, this.guard.getBoundingBox().grow(5.0D, 3.0D, 5.0D));
        if (!list.isEmpty()) {
            for (LivingEntity mob : list) {
                if (mob != null) {
                    if (mob.getLastAttackedEntity() instanceof GuardEntity || mob instanceof MobEntity && ((MobEntity) mob).getAttackTarget() instanceof GuardEntity) {
                        this.walkTimer += 10;
                    }
                }
            }
        }
        this.walkTimer--;
    }

    @Override
    protected Vector3d getPosition() {
        List<LivingEntity> list = this.guard.world.getEntitiesWithinAABB(LivingEntity.class, this.guard.getBoundingBox().grow(10.0D, 3.0D, 10.0D));
        if (!list.isEmpty()) {
            for (LivingEntity mob : list) {
                if (mob != null) {
                    if (mob.getLastAttackedEntity() instanceof GuardEntity || mob instanceof MobEntity && ((MobEntity) mob).getAttackTarget() instanceof GuardEntity) {
                        return RandomPositionGenerator.findRandomTargetBlockAwayFrom(guard, 16, 7, mob.getPositionVec());
                    }
                }
            }
        }
        return super.getPosition();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return guard.getHealth() < guard.getMaxHealth() && GuardEatFoodGoal.isConsumable(guard.getHeldItemOffhand()) && !guard.isEating() && super.shouldContinueExecuting() && walkTimer > 0;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        if (walkTimer <= 0)
            guard.setEating(true);
        if (guard.isEating()) 
            guard.setEating(false);
    }

    public boolean findPosition() {
        Vector3d vector3d = this.getPosition();
        if (vector3d == null) {
            return false;
        } else {
            this.x = vector3d.x;
            this.y = vector3d.y;
            this.z = vector3d.z;
            return true;
        }
    }
}
