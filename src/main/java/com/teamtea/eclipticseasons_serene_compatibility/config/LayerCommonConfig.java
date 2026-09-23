package com.teamtea.eclipticseasons_serene_compatibility.config;

import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import sereneseasons.init.ModConfig;

public class LayerCommonConfig {

    public static void init() {
        // seasons
        if (CommonConfig.COMMON_CONFIG.isLoaded()) {
            ModConfig.seasons.whitelistedDimensions.clear();
            ModConfig.seasons.whitelistedDimensions.addAll(CommonConfig.Season.validDimensions.get());
            ModConfig.seasons.generateSnowAndIce = CommonConfig.Temperature.iceMelt.get()
                    && CommonConfig.Temperature.snowDown.get();
            ModConfig.seasons.subSeasonDuration = CommonConfig.Season.lastingDaysOfEachTerm.get() * 2;
            ModConfig.seasons.changeWeatherFrequency = CommonConfig.Weather.useSolarWeather.get();
            ModConfig.seasons.whitelistedDimensions.addAll(CommonConfig.Season.validDimensions.get());
        }

        if (ClientConfig.CLIENT_CONFIG.isLoaded()) {
            ModConfig.seasons.changeGrassColor = ClientConfig.Renderer.seasonalGrassColorChange.get();
            ModConfig.seasons.changeFoliageColor = ClientConfig.Renderer.seasonalGrassColorChange.get();
            ModConfig.seasons.changeBirchColor = ClientConfig.Renderer.seasonalColorChangeExtend.get();
        }

        // fertility
        if (CommonConfig.COMMON_CONFIG.isLoaded()) {
            ModConfig.fertility.seasonalCrops = CommonConfig.Crop.enableCrop.get();
        }
        if (ClientConfig.CLIENT_CONFIG.isLoaded()) {
            ModConfig.fertility.cropTooltips = ClientConfig.GUI.agriculturalInformation.get();
        }
    }
}

