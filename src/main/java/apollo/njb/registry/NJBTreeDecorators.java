package apollo.njb.registry;

import apollo.njb.worldgen.treedecorator.BranchAndBeehive;
import apollo.njb.worldgen.treedecorator.TrunkVines;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import static apollo.njb.registry.RegistryUtils.register;

public class NJBTreeDecorators {

    public static final Registry<TreeDecoratorType<?>> registry = Registries.TREE_DECORATOR_TYPE;
    public static final TreeDecoratorType<BranchAndBeehive> BRANCH_AND_BEEHIVE = register(
        registry, "branch_and_beehive", new TreeDecoratorType<>(BranchAndBeehive.CODEC)
    );
    public static final TreeDecoratorType<TrunkVines> TRUNK_VINES = register(
            registry, "trunk_vines", new TreeDecoratorType<>(TrunkVines.CODEC)
    );

    public static void init() {}
}
