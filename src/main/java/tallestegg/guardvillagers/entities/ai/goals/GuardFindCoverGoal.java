package tallestegg.guardvillagers.entities.ai.goals;

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
            this.walkTimer = 50;
    }

    @Override
    public void tick() {
        super.tick();
        this.walkTimer--;
    }

    @Override
    protected Vector3d getPosition() {
        return guard.getAttackTarget() != null ? RandomPositionGenerator.findRandomTargetBlockAwayFrom(guard, 16, 7, guard.getAttackTarget().getPositionVec()) : super.getPosition();
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
