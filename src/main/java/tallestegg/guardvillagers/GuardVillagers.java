package tallestegg.guardvillagers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.renderer.GuardRenderer;

@Mod(GuardVillagers.MODID)
public class GuardVillagers
{  
	public static final String MODID = "guardvillagers";

    public GuardVillagers() 
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GuardConfig.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new HandlerEvents());
        MinecraftForge.EVENT_BUS.register(new GuardEntityType());
		MinecraftForge.EVENT_BUS.register(new VillagerToGuard());
		//GuardEntityType.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
		//GuardSpawner.inject();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	
    }

    private void doClientStuff(final FMLClientSetupEvent event) 
    {
      RenderingRegistry.registerEntityRenderingHandler(GuardEntityType.GUARD.get(), GuardRenderer::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
     
    }

    private void processIMC(final InterModProcessEvent event)
    {

    }
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        
    }
}




