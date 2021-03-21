package tallestegg.guardvillagers.entities.ai.goals;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal;
import tallestegg.guardvillagers.entities.GuardEntity;

public class GuardSetRunningToEatGoal extends Goal {
    protected final GuardEntity guard;
    private boolean setRunning;

    public GuardSetRunningToEatGoal(GuardEntity guard, double speedIn) {
        super();
        this.guard = guard;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        return !setRunning && !guard.isRunningToEat() && guard.getHealth() < guard.getMaxHealth() / 2 && GuardEatFoodGoal.isConsumable(guard.getHeldItemOffhand()) && !guard.isEating() && guard.getAttackTarget() != null;
    }

    @Override
    public void startExecuting() {
        this.guard.setAttackTarget(null);
        if (!guard.isRunningToEat()) {
            this.setRunning = true;
            this.guard.setRunningToEat(true);
        }
    }
}
