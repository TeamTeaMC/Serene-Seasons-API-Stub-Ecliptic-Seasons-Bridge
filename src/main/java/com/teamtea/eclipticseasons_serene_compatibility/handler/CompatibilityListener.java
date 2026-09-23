package com.teamtea.eclipticseasons_serene_compatibility.handler;

import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons_serene_compatibility.SereneCompatibility;
import com.teamtea.eclipticseasons_serene_compatibility.api.EclipticSeasonTime;
import com.teamtea.eclipticseasons_serene_compatibility.config.LayerCommonConfig;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class CompatibilityListener {

    public static void onLevelLoad(Level level) {
        SeasonHelper.SEASON_STATE_HASH_MAP.put(level, new EclipticSeasonTime(level));
    }


    public static void onLevelUnLoad(Level level) {
        SeasonHelper.SEASON_STATE_HASH_MAP.remove(level);
    }

    private static Method fireMethod = null;
    private static Constructor<?> eventConstructor = null;
    public static final Logger LOGGER = LogManager.getLogger(SereneCompatibility.MODID);

    public static void onSolarTermChangeEvent(SolarTermChangeEvent event) {
        Season.SubSeason oldSub = Season.SubSeason.VALUES[event.getOldSolarTerm().ordinal() / 2];
        Season.SubSeason newSub = Season.SubSeason.VALUES[event.getNewSolarTerm().ordinal() / 2];

        if (oldSub != newSub) {
            try {
                if (fireMethod == null) {
                    Class<?> eventManagerClass = Class.forName("glitchcore.event.EventManager");
                    Class<?> eventBaseClass = Class.forName("glitchcore.event.Event");
                    fireMethod = eventManagerClass.getMethod("fire", eventBaseClass);
                    Class<?> eventClass = Class.forName("sereneseasons.api.season.SeasonChangedEvent$Standard");
                    eventConstructor = eventClass.getConstructor(Level.class, Season.SubSeason.class, Season.SubSeason.class);
                }
                Object seasonEvent = eventConstructor.newInstance(event.getLevel(), oldSub, newSub);
                fireMethod.invoke(null, seasonEvent);
            } catch (Exception e) {
                LOGGER.error("Try fire glitchcore event" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public static void onTagsUpdatedEvent() {
        LayerCommonConfig.init();
    }
}
