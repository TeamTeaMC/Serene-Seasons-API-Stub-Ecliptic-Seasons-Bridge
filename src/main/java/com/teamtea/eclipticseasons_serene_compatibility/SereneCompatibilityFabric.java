package com.teamtea.eclipticseasons_serene_compatibility;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import com.teamtea.eclipticseasons_serene_compatibility.config.LayerCommonConfig;
import com.teamtea.eclipticseasons_serene_compatibility.handler.CompatibilityListener;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.SSItems;

import java.lang.reflect.Proxy;
import java.util.List;

public class SereneCompatibilityFabric implements ModInitializer {

    public static final String MODID = "sereneseasons";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        sereneseasons.init.ModConfig.init();
        LayerCommonConfig.init();

        SSItems.CALENDAR = BuiltInRegistries.ITEM.getValue(EclipticSeasons.rl("calendar"));

        CompatibilityEventInvoker.init();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientCompatibilityEventInvoker.init();
        }
    }

    public static Identifier rl(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }


    public static class CompatibilityEventInvoker {

        public static void init() {
            ServerLevelEvents.LOAD.register((server, level) ->
                    CompatibilityListener.onLevelLoad(level)
            );

            ServerLevelEvents.UNLOAD.register((server, level) ->
                    CompatibilityListener.onLevelUnLoad(level)
            );

            ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
                if (success) {
                    CompatibilityListener.onTagsUpdatedEvent();
                }
            });
            ServerLifecycleEvents.SERVER_STARTED.register((server -> CompatibilityListener.onTagsUpdatedEvent()));

//            ESEventHook.SOLAR_TERM_CHANGE.register(CompatibilityListener::onSolarTermChangeEvent);
            registerSolarTermChangeEvent();
        }

        private static void registerSolarTermChangeEvent() {
            try {
                Class<?> hookClass = Class.forName(
                        "com.teamtea.eclipticseasons.common.hook.ESEventHook"
                );
                Class<?> trickerClass = Class.forName(
                        "com.teamtea.eclipticseasons.common.hook.ESEventHook$Tricker"
                );

                Object event = hookClass.getField("SOLAR_TERM_CHANGE").get(null);

                Object listener = Proxy.newProxyInstance(
                        trickerClass.getClassLoader(),
                        new Class[]{trickerClass},
                        (proxy, method, args) -> {
                            if (method.getName().equals("onEvent")) {
                                CompatibilityListener.onSolarTermChangeEvent(
                                        (SolarTermChangeEvent) args[0]
                                );
                            }
                            return null;
                        }
                );

                Event.class
                        .getMethod("register", Object.class)
                        .invoke(event, listener);

            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ClientCompatibilityEventInvoker {

        public static void init() {
            ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) ->
                    CompatibilityListener.onLevelLoad(level)
            );

            ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
                CompatibilityListener.onTagsUpdatedEvent();
            });
        }
    }

    public static class ModMenuCompact implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return ESModConfigScreen::new;
        }
    }
}