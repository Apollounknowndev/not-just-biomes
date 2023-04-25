package apollo.njb.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import java.util.List;

public class MansionTemplates {
    public static final Codec<MansionTemplates> ROOM_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            StructureProcessorType.REGISTRY_CODEC.fieldOf("mansion_processor").forGetter((config) -> {
                return config.mansionProcessor;
            }),
            Identifier.CODEC.fieldOf("functional_staircase").forGetter((roomTemplates) -> {
                return roomTemplates.functionalStaircase;
            }),
            Identifier.CODEC.fieldOf("generic_staircase").forGetter((roomTemplates) -> {
                return roomTemplates.genericStaircase;
            }),
            FloorTemplates.CODEC.fieldOf("first_floor").forGetter((rooms) -> {
                return rooms.firstFloor;
            }),
            FloorTemplates.CODEC.fieldOf("second_floor").forGetter((rooms) -> {
                return rooms.secondFloor;
            }),
            FloorTemplates.CODEC.fieldOf("third_floor").forGetter((rooms) -> {
                return rooms.thirdFloor;
            }),
            WallTemplates.CODEC.fieldOf("walls").forGetter((rooms) -> {
                return rooms.walls;
            })
        ).apply(instance, MansionTemplates::new);
    });
    public final RegistryEntry<StructureProcessorList> mansionProcessor;
    public final Identifier functionalStaircase;
    public final Identifier genericStaircase;
    public final FloorTemplates firstFloor;
    public final FloorTemplates secondFloor;
    public final FloorTemplates thirdFloor;
    public final WallTemplates walls;

    public MansionTemplates(RegistryEntry<StructureProcessorList> mansionProcessor, Identifier functionalStaircase, Identifier genericStaircase, FloorTemplates firstFloor, FloorTemplates secondFloor, FloorTemplates thirdFloor, WallTemplates walls) {
        this.mansionProcessor = mansionProcessor;
        this.functionalStaircase = functionalStaircase;
        this.genericStaircase = genericStaircase;
        this.firstFloor = firstFloor;
        this.secondFloor = secondFloor;
        this.thirdFloor = thirdFloor;
        this.walls = walls;
    }

    public static class FloorTemplates {
        public static final Codec<FloorTemplates> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                Identifier.CODEC.listOf().fieldOf("small").forGetter((roomTemplates) -> {
                    return roomTemplates.small;
                }),
                Identifier.CODEC.listOf().fieldOf("small_secret").forGetter((roomTemplates) -> {
                    return roomTemplates.smallSecret;
                }),
                Identifier.CODEC.listOf().fieldOf("medium_functional").forGetter((roomTemplates) -> {
                    return roomTemplates.mediumFunctional;
                }),
                Identifier.CODEC.listOf().fieldOf("medium_generic").forGetter((roomTemplates) -> {
                    return roomTemplates.mediumGeneric;
                }),
                Identifier.CODEC.listOf().fieldOf("medium_secret").forGetter((roomTemplates) -> {
                    return roomTemplates.mediumSecret;
                }),
                Identifier.CODEC.listOf().fieldOf("large").forGetter((roomTemplates) -> {
                    return roomTemplates.large;
                }),
                Identifier.CODEC.listOf().fieldOf("large_secret").forGetter((roomTemplates) -> {
                    return roomTemplates.largeSecret;
                })
            ).apply(instance, FloorTemplates::new);
        });
        public final List<Identifier> small;
        public final List<Identifier> smallSecret;
        public final List<Identifier> mediumFunctional;
        public final List<Identifier> mediumGeneric;
        public final List<Identifier> mediumSecret;
        public final List<Identifier> large;
        public final List<Identifier> largeSecret;

        public FloorTemplates(List<Identifier> small, List<Identifier> smallSecret, List<Identifier> mediumFunctional, List<Identifier> mediumGeneric, List<Identifier> mediumSecret, List<Identifier> large, List<Identifier> largeSecret) {
            if (small.isEmpty() || smallSecret.isEmpty() || mediumFunctional.isEmpty() || mediumGeneric.isEmpty() || mediumSecret.isEmpty() || large.isEmpty() || largeSecret.isEmpty()) {
                throw new IllegalArgumentException("All structure lists need at least one entry");
            } else {
                this.small = small;
                this.smallSecret = smallSecret;
                this.mediumFunctional = mediumFunctional;
                this.mediumGeneric = mediumGeneric;
                this.mediumSecret = mediumSecret;
                this.large = large;
                this.largeSecret = largeSecret;
            }
        }
    }

    public static class WallTemplates {
        public static final Codec<WallTemplates> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                Identifier.CODEC.fieldOf("entrance").forGetter((roomTemplates) -> {
                    return roomTemplates.entrance;
                }),
                Identifier.CODEC.fieldOf("lower_wall").forGetter((roomTemplates) -> {
                    return roomTemplates.lowerWall;
                }),
                Identifier.CODEC.fieldOf("upper_wall").forGetter((roomTemplates) -> {
                    return roomTemplates.upperWall;
                }),
                Identifier.CODEC.fieldOf("corner_wall").forGetter((roomTemplates) -> {
                    return roomTemplates.cornerWall;
                }),
                Identifier.CODEC.fieldOf("small_wall").forGetter((roomTemplates) -> {
                    return roomTemplates.smallWall;
                }),
                Identifier.CODEC.fieldOf("small_corner_wall").forGetter((roomTemplates) -> {
                    return roomTemplates.smallCornerWall;
                }),
                Identifier.CODEC.fieldOf("roof").forGetter((roomTemplates) -> {
                    return roomTemplates.roof;
                }),
                Identifier.CODEC.fieldOf("roof_corner").forGetter((roomTemplates) -> {
                    return roomTemplates.roofCorner;
                }),
                Identifier.CODEC.fieldOf("roof_inner_corner").forGetter((roomTemplates) -> {
                    return roomTemplates.roofInnerCorner;
                }),
                Identifier.CODEC.fieldOf("roof_side").forGetter((roomTemplates) -> {
                    return roomTemplates.roofSide;
                })
            ).apply(instance, WallTemplates::new);
        });
        public final Identifier entrance;
        public final Identifier lowerWall;
        public final Identifier upperWall;
        public final Identifier cornerWall;
        public final Identifier smallWall;
        public final Identifier smallCornerWall;
        public final Identifier roof;
        public final Identifier roofCorner;
        public final Identifier roofInnerCorner;
        public final Identifier roofSide;

        public WallTemplates(Identifier entrance, Identifier lowerWall, Identifier upperWall, Identifier cornerWall, Identifier smallWall, Identifier smallCornerWall, Identifier roof, Identifier roofCorner, Identifier roofInnerCorner, Identifier roofSide) {
            this.entrance = entrance;
            this.lowerWall = lowerWall;
            this.upperWall = upperWall;
            this.cornerWall = cornerWall;
            this.smallWall = smallWall;
            this.smallCornerWall = smallCornerWall;
            this.roof = roof;
            this.roofCorner = roofCorner;
            this.roofInnerCorner = roofInnerCorner;
            this.roofSide = roofSide;
        }
    }
}
