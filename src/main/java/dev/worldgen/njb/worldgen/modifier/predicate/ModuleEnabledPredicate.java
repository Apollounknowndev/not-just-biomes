package dev.worldgen.njb.worldgen.modifier.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.njb.config.ConfigHandler;

public record ModuleEnabledPredicate(String module) implements ModifierPredicate {
    public static final Codec<ModuleEnabledPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("module").forGetter(ModuleEnabledPredicate::module)
    ).apply(instance, ModuleEnabledPredicate::new));

    public boolean test() {
        return ConfigHandler.isModuleEnabled(this.module);
    }

    public Codec<? extends ModifierPredicate> codec() {
        return CODEC;
    }
}
