package dev.worldgen.njb.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.njb.worldgen.modifier.predicate.ModuleEnabledPredicate;
import net.minecraft.registry.MutableRegistry;

import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBModifierPredicates {
    public static final MutableRegistry<Codec<? extends ModifierPredicate>> registry = LithostitchedBuiltInRegistries.MODIFIER_PREDICATE_TYPE;
    public static final Codec<ModuleEnabledPredicate> MODULE_ENABLED = register(
        registry, "module_enabled", ModuleEnabledPredicate.CODEC
    );

    public static void init() {}

}
