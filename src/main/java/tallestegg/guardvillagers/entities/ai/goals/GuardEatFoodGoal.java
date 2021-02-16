package tallestegg.guardvillagers.entities.ai.goals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import tallestegg.guardvillagers.entities.GuardEntity;

public class GuardEatFoodGoal extends Goal {

    public final GuardEntity guard;

    public GuardEatFoodGoal(GuardEntity guard) {
        this.guard = guard;
    }

    @Override
    public boolean shouldExecute() {
        return guard.getHealth() < guard.getMaxHealth() && GuardEatFoodGoal.isConsumable(guard.getHeldItemOffhand()) && guard.isEating() || guard.getHealth() < guard.getMaxHealth() && GuardEatFoodGoal.isConsumable(guard.getHeldItemOffhand()) && guard.getAttackTarget() == null;
    }

    public static boolean isConsumable(ItemStack stack) {
        return stack.getUseAction() == UseAction.EAT  && stack.getCount() > 0 || stack.getUseAction() == UseAction.DRINK && !(stack.getItem() instanceof SplashPotionItem) && stack.getCount() > 0;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return guard.isHandActive() && guard.getAttackTarget() == null && guard.getHealth() < guard.getMaxHealth() || guard.getAttackTarget() != null && guard.getHealth() < guard.getMaxHealth() / 2 && guard.isEating(); 
        // Guards will only keep eating until they're up to full health if they're not hostile, otherwise they will just heal back above half health and then join back the fight.
    }

    @Override
    public void startExecuting() {
        if (guard.getAttackTarget() == null)
            guard.setEating(true);
        guard.setActiveHand(Hand.OFF_HAND);
    }

    @Override
    public void resetTask() {
        guard.setEating(false);
        guard.resetActiveHand();
    }
}
