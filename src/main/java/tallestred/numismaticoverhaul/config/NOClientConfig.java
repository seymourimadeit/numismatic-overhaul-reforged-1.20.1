package tallestred.numismaticoverhaul.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NOClientConfig {

    public static final ModConfigSpec CLIENT_SPEC;
    public static final NOClientConfig CLIENT;

    static {
        Pair<NOClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(NOClientConfig::new);
        CLIENT_SPEC = pair.getRight();
        CLIENT = pair.getLeft();
    }

    public ModConfigSpec.BooleanValue example;
    public ModConfigSpec.IntValue creativePursePositionX;
    public ModConfigSpec.IntValue creativePursePositionY;
    public ModConfigSpec.IntValue survivalPursePositionX;
    public ModConfigSpec.IntValue survivalPursePositionY;
    public ModConfigSpec.IntValue merchantPursePositionX;
    public ModConfigSpec.IntValue merchantPursePositionY;

    public NOClientConfig(ModConfigSpec.Builder builder) {
        builder.push("client");
        example = builder.define("purse", true);
        creativePursePositionX = builder.defineInRange("creativePurseX", 0, -1000, 1000);
        creativePursePositionY = builder.defineInRange("creativePurseY", 0, -2000, 2000);
        survivalPursePositionX = builder.defineInRange("survivalPurseX", 0, -1000, 1000);
        survivalPursePositionY = builder.defineInRange("survivalPurseY", 0, -2000, 2000);
        merchantPursePositionX = builder.defineInRange("merchantPurseX", 0, -1000, 1000);
        merchantPursePositionY = builder.defineInRange("merchantPurseY", 0, -2000, 2000);
        builder.pop();
        builder.build();
    }
}

