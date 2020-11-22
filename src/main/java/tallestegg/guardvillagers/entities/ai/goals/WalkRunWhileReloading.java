package tallestegg.guardvillagers.entities.ai.goals;

import javax.annotation.Nullable;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import tallestegg.guardvillagers.entities.GuardEntity;

public class WalkRunWhileReloading extends RandomWalkingGoal {

    public WalkRunWhileReloading(CreatureEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn);
    }

    @Override
    public boolean shouldExecute() {
        return ((GuardEntity) creature).isCharging() && creature.getAttackTarget() != null && this.findPosition() && !CrossbowItem.isCharged(creature.getActiveItemStack());
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

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.creature.setActiveHand(ProjectileHelper.getHandWith(this.creature, Items.CROSSBOW));
        ((GuardEntity) creature).setCharging(true);
        if (creature.getAttackTarget() != null) {
            creature.faceEntity(creature.getAttackTarget(), 30.0F, 30.0F);
            creature.getLookController().setLookPositionWithEntity(creature.getAttackTarget(), 30.0F, 30.0F);
        }
    }

    @Override
    public void tick() {
        int i = this.creature.getItemInUseMaxCount();
        ItemStack itemstack = this.creature.getActiveItemStack();
        if (i >= CrossbowItem.getChargeTime(itemstack)) {
            this.creature.stopActiveHand();
            ((GuardEntity) creature).setCharging(false);
        }
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.creature.stopActiveHand();
        ((GuardEntity) creature).setCharging(false);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !CrossbowItem.isCharged(creature.getActiveItemStack()) && ((GuardEntity) creature).isCharging() && super.shouldContinueExecuting();
    }

    @Override
    @Nullable
    protected Vector3d getPosition() {
        return RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 7, this.creature.getAttackTarget().getPositionVec());
    }
}