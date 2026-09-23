package com.teamtea.eclipticseasons_serene_compatibility;


import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons_serene_compatibility.config.LayerCommonConfig;
import com.teamtea.eclipticseasons_serene_compatibility.handler.CompatibilityListener;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.SSItems;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SereneCompatibility.MODID)
public class SereneCompatibility {
    public static final String MODID = "sereneseasons";
    public static final Logger LOGGER = LogManager.getLogger(SereneCompatibility.MODID);

    public SereneCompatibility(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::FMLCommonSetup);

        if (FMLLoader.getCurrentOrNull().getDist() == Dist.CLIENT)
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ESModConfigScreen::new);

    }

    public static Identifier rl(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }

    public void FMLCommonSetup(final FMLCommonSetupEvent event) {
        sereneseasons.init.ModConfig.init();
        LayerCommonConfig.init();
        SSItems.CALENDAR= ItemRegistry.calendar_item.get();
    }

    @EventBusSubscriber(modid = MODID)
    public static class CompatibilityEventInvoker {
        @SubscribeEvent
        public static void onLevelLoad(LevelEvent.Load event) {
            if (event.getLevel() instanceof Level level) {
                CompatibilityListener.onLevelLoad(level);
            }
        }

        @SubscribeEvent
        public static void onLevelUnLoad(LevelEvent.Unload event) {
            if (event.getLevel() instanceof Level level) {
                CompatibilityListener.onLevelUnLoad(level);
            }
        }

        public static void onSolarTermChangeEvent(SolarTermChangeEvent event) {
            CompatibilityListener.onSolarTermChangeEvent(event);
        }

        @SubscribeEvent
        public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
            CompatibilityListener.onTagsUpdatedEvent();
        }

        @SubscribeEvent
        public static void UpdateConfig(ModConfigEvent modConfigEvent) {
            LayerCommonConfig.init();
        }

    }
}
