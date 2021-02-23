package tallestegg.guardvillagers;

import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.entities.GuardEntity;
import tallestegg.guardvillagers.entities.ai.goals.AttackEntityDaytimeGoal;
import tallestegg.guardvillagers.entities.ai.goals.HealGolemGoal;
import tallestegg.guardvillagers.entities.ai.goals.HealGuardAndPlayerGoal;

@Mod.EventBusSubscriber(modid = GuardVillagers.MODID)
public class HandlerEvents {
    
    @SubscribeEvent
    public static void onEntityTarget(LivingSetAttackTargetEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        LivingEntity target = event.getTarget();
        if (target == null || entity.getType() == GuardEntityType.GUARD.get())
            return;
        if (target.getType() == EntityType.VILLAGER)  {
            List<MobEntity> list = entity.world.getEntitiesWithinAABB(MobEntity.class, entity.getBoundingBox().grow(GuardConfig.GuardVillagerHelpRange, 5.0D, GuardConfig.GuardVillagerHelpRange));
            for (MobEntity mob : list) {
                if (mob.getType() == GuardEntityType.GUARD.get()) {
                    mob.setAttackTarget(entity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        LivingEntity trueSource = (LivingEntity) event.getSource().getTrueSource();
        if (entity == null || trueSource != null && trueSource.getType() == GuardEntityType.GUARD.get())
            return;
        boolean isVillager = entity.getType() == EntityType.VILLAGER;
        if (isVillager && event.getSource().getTrueSource() instanceof MobEntity && trueSource == entity) {
            List<MobEntity> list = trueSource.world.getEntitiesWithinAABB(MobEntity.class, trueSource.getBoundingBox().grow(GuardConfig.GuardVillagerHelpRange, 5.0D, GuardConfig.GuardVillagerHelpRange));
            for (MobEntity mob : list) {
                if (mob.getType() == GuardEntityType.GUARD.get() && mob.getAttackTarget() == null) {
                    mob.setAttackTarget((MobEntity) event.getSource().getTrueSource());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinWorldEvent event) {
        if (GuardConfig.AttackAllMobs) {
            if (event.getEntity() instanceof IMob && !GuardConfig.MobBlackList.contains(event.getEntity().getEntityString()) && !(event.getEntity() instanceof SpiderEntity)) {
                MobEntity mob = (MobEntity) event.getEntity();
                mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, GuardEntity.class, false));
            }
            if (event.getEntity() instanceof IMob && !GuardConfig.MobBlackList.contains(event.getEntity().getEntityString()) && event.getEntity() instanceof SpiderEntity) {
                SpiderEntity spider = (SpiderEntity) event.getEntity();
                spider.targetSelector.addGoal(3, new AttackEntityDaytimeGoal<>(spider, GuardEntity.class));
            }
        }

        if (event.getEntity() instanceof AbstractIllagerEntity) {
            AbstractIllagerEntity illager = (AbstractIllagerEntity) event.getEntity();
            illager.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(illager, GuardEntity.class, false));
            if (GuardConfig.IllagersRunFromPolarBears) {
                illager.goalSelector.addGoal(2, new AvoidEntityGoal<>(illager, PolarBearEntity.class, 6.0F, 1.0D, 1.2D));
            } // common sense.
            if (GuardConfig.RaidAnimals) {
                if (illager.isRaidActive()) {
                    illager.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(illager, AnimalEntity.class, false));
                }
            }
        }

        if (event.getEntity() instanceof AbstractVillagerEntity) {
            AbstractVillagerEntity villager = (AbstractVillagerEntity) event.getEntity();
            if (GuardConfig.VillagersRunFromPolarBears)
                villager.goalSelector.addGoal(2, new AvoidEntityGoal<>(villager, PolarBearEntity.class, 6.0F, 1.0D, 1.2D)); // common sense.
            if (GuardConfig.WitchesVillager)
                villager.goalSelector.addGoal(2, new AvoidEntityGoal<>(villager, WitchEntity.class, 6.0F, 1.0D, 1.2D));
        }

        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();
            if (GuardConfig.BlackSmithHealing)
                villager.goalSelector.addGoal(1, new HealGolemGoal(villager)); // TODO mixin into the villagers brain and make these tasks instead of goals.
            if (GuardConfig.ClericHealing)
                villager.goalSelector.addGoal(1, new HealGuardAndPlayerGoal(villager, 1.0D, 100, 0, 10.0F));
        }

        if (event.getEntity() instanceof IronGolemEntity) {
            IronGolemEntity golem = (IronGolemEntity) event.getEntity();
            HurtByTargetGoal tolerateFriendlyFire = new HurtByTargetGoal(golem, GuardEntity.class).setCallsForHelp();
            golem.targetSelector.goals.stream().map(it -> it.inner).filter(it -> it instanceof HurtByTargetGoal).findFirst().ifPresent(angerGoal -> {
                golem.targetSelector.removeGoal(angerGoal);
                golem.targetSelector.addGoal(2, tolerateFriendlyFire);
            });
        }

        if (event.getEntity() instanceof ZombieEntity) {
            ZombieEntity zombie = (ZombieEntity) event.getEntity();
            zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(zombie, GuardEntity.class, false));
        }

        if (event.getEntity() instanceof RavagerEntity) {
            RavagerEntity ravager = (RavagerEntity) event.getEntity();
            ravager.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ravager, GuardEntity.class, false));
            if (GuardConfig.RaidAnimals) {
                if (ravager.isRaidActive()) {
                    ravager.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(ravager, AnimalEntity.class, false));
                }
            }
        }

        if (event.getEntity() instanceof WitchEntity) {
            WitchEntity witch = (WitchEntity) event.getEntity();
            if (GuardConfig.WitchesVillager) {
                witch.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(witch, AbstractVillagerEntity.class, true));
                witch.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(witch, IronGolemEntity.class, true));
                witch.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(witch, GuardEntity.class, false));
            }
            if (GuardConfig.IllagersRunFromPolarBears) {
                witch.goalSelector.addGoal(2, new AvoidEntityGoal<>(witch, PolarBearEntity.class, 6.0F, 1.0D, 1.2D));
            }
            if (GuardConfig.RaidAnimals) {
                if (witch.isRaidActive()) {
                    witch.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(witch, AnimalEntity.class, false));
                }
            }
        }

        if (event.getEntity() instanceof CatEntity) {
            CatEntity cat = (CatEntity) event.getEntity();
            cat.goalSelector.addGoal(1, new AvoidEntityGoal<>(cat, AbstractIllagerEntity.class, 12.0F, 1.0D, 1.2D));
        }

        if (event.getEntity() instanceof IllusionerEntity) {
            IllusionerEntity illusioner = (IllusionerEntity) event.getEntity();
            illusioner.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(illusioner, GuardEntity.class, false));
            if (GuardConfig.RaidAnimals) {
                if (illusioner.isRaidActive()) {
                    illusioner.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(illusioner, AnimalEntity.class, false));
                }
            }
        }
    }
}
