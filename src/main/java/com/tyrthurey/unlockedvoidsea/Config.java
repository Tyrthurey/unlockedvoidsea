package com.tyrthurey.unlockedvoidsea;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config extends ConfigBase {

    public final CValue<String, ModConfigSpec.ConfigValue<String>> DIMENSION = s("minecraft:overworld", "dimension", Comments.dimension);
    public final ConfigFloat START_Y = f(-40.0f, -256.0f, 900.0f, "startY", Comments.startY);
    public final ConfigFloat DEPTH_GRADIENT = f(1.0f, 0.0f, 10.0f, "depthGradient", Comments.depthGradient);
    public final ConfigFloat DRAG = f(1.0f, 0.0f, 10.0f, "drag", Comments.drag);
    public final ConfigInt PRIORITY = i(1000, 0, 10000, "priority", Comments.priority);

    @Override
    public String getName() {
        return "server";
    }

    protected <T> CValue<T, ModConfigSpec.ConfigValue<T>> s(T defaultValue, String name, String... comment) {
        CValue<T, ModConfigSpec.ConfigValue<T>> value = new CValue<>(name, builder -> builder.define(name, defaultValue), comment);
        allValues.add(value);
        return value;
    }

    private static class Comments {
        static String dimension = "The dimension to enable the void sea in (e.g., minecraft:overworld)";
        static String startY = "The Y-level where the purple fog and buoyancy begin";
        static String depthGradient = "How strong the buoyancy is. 1.0 is standard.";
        static String drag = "Multiplier for water-like resistance. 1.0 is standard.";
        static String priority = "Higher priority overrides other void sea definitions for the same dimension.";
    }
}
