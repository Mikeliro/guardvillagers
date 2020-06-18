package tallestegg.guardvillagers.entities.goals;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Vec3d;
import tallestegg.guardvillagers.entities.GuardEntity;

public class FollowShieldGuards extends Goal
{
	   private final GuardEntity taskOwner;
	   private GuardEntity guardtofollow;
	   private double x;
	   private double y;
	   private double z;

	   public FollowShieldGuards(GuardEntity taskOwnerIn) {
	      this.taskOwner = taskOwnerIn;
	   }

	   public boolean shouldExecute() {
	         List<GuardEntity> list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), this.taskOwner.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
		      if (!list.isEmpty()) {
			         for(GuardEntity guard : list) {
			            if (!guard.isInvisible() && guard.getHeldItemOffhand().getItem() instanceof ShieldItem && this.taskOwner.world.getTargettableEntitiesWithinAABB(GuardEntity.class, (new EntityPredicate()).setDistance(3.0D), guard, this.taskOwner.getBoundingBox().grow(5.0D)).size() < 5) {
			               this.guardtofollow = guard;
			               Vec3d vec3d = this.getPosition();
			               if (vec3d == null) {
			                  return false;
			               } else {
			                   this.x = vec3d.x;
			                   this.y = vec3d.y;
			                   this.z = vec3d.z;
			                  return true;
			               }
			            }
			         }
			      }
		 return false;
	   }
	   
	   @Nullable
	   protected Vec3d getPosition() {
	      return RandomPositionGenerator.findRandomTargetBlockTowards(taskOwner, 1, 1, guardtofollow.getPositionVec());
	   }
	   
	   public boolean shouldContinueExecuting() {
		      return !this.taskOwner.getNavigator().noPath() && !this.taskOwner.isBeingRidden();
	   }
	   
	  public void resetTask() 
	  {
		this.taskOwner.getNavigator().clearPath();
		super.resetTask();
	  }

	   
	   @Override
	   public void startExecuting()
	   {
		 this.taskOwner.getNavigator().tryMoveToXYZ(x, y, z, 0.4D);
	   }
}
