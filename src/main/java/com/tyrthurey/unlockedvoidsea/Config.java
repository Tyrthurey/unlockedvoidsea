package com.tyrthurey.unlockedvoidsea;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> DIMENSION = BUILDER
            .comment("The dimension to enable the void sea in (e.g., minecraft:overworld)")
            .translation("unlockedvoidsea.config.dimension")
            .define("dimension", "minecraft:overworld");

    public static final ModConfigSpec.DoubleValue START_Y = BUILDER
            .comment("The Y-level where the purple fog and buoyancy begin")
            .translation("unlockedvoidsea.config.startY")
            .defineInRange("startY", -40.0, -256.0, 320.0);

    public static final ModConfigSpec.DoubleValue DEPTH_GRADIENT = BUILDER
            .comment("How strong the buoyancy is. 1.0 is standard.")
            .translation("unlockedvoidsea.config.depthGradient")
            .defineInRange("depthGradient", 1.0, 0.0, 10.0);

    public static final ModConfigSpec.DoubleValue DRAG = BUILDER
            .comment("Multiplier for water-like resistance. 1.0 is standard.")
            .translation("unlockedvoidsea.config.drag")
            .defineInRange("drag", 1.0, 0.0, 10.0);

    public static final ModConfigSpec.IntValue PRIORITY = BUILDER
            .comment("Higher priority overrides other void sea definitions for the same dimension.")
            .translation("unlockedvoidsea.config.priority")
            .defineInRange("priority", 1000, 0, 10000);

    static final ModConfigSpec SPEC = BUILDER.build();
}
