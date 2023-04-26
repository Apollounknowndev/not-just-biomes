package dev.worldgen.njb.structure;

import dev.worldgen.njb.NotJustBiomes;
import dev.worldgen.njb.registry.NJBStructurePieces;
import dev.worldgen.njb.worldgen.structure.MansionTemplates;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlternateMansionGenerator {
    public AlternateMansionGenerator() {
    }

    public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<AlternateMansionGenerator.Piece> pieces, Random random, MansionTemplates roomTemplates) {
        AlternateMansionGenerator.MansionParameters mansionParameters = new AlternateMansionGenerator.MansionParameters(random);
        AlternateMansionGenerator.LayoutGenerator layoutGenerator = new AlternateMansionGenerator.LayoutGenerator(manager, random, roomTemplates);
        layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
    }

    private static class MansionParameters {
        private final Random random;
        final AlternateMansionGenerator.FlagMatrix baseLayout;
        final AlternateMansionGenerator.FlagMatrix thirdFloorLayout;
        final AlternateMansionGenerator.FlagMatrix[] roomFlagsByFloor;
        final int entranceI;
        final int entranceJ;

        public MansionParameters(Random random) {
            this.random = random;
            this.entranceI = 7;
            this.entranceJ = 4;
            this.baseLayout = new AlternateMansionGenerator.FlagMatrix(11, 11, 5);
            this.baseLayout.fill(this.entranceI, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 3);
            this.baseLayout.fill(this.entranceI - 1, this.entranceJ, this.entranceI - 1, this.entranceJ + 1, 2);
            this.baseLayout.fill(this.entranceI + 2, this.entranceJ - 2, this.entranceI + 3, this.entranceJ + 3, 5);
            this.baseLayout.fill(this.entranceI + 1, this.entranceJ - 2, this.entranceI + 1, this.entranceJ - 1, 1);
            this.baseLayout.fill(this.entranceI + 1, this.entranceJ + 2, this.entranceI + 1, this.entranceJ + 3, 1);
            this.baseLayout.set(this.entranceI - 1, this.entranceJ - 1, 1);
            this.baseLayout.set(this.entranceI - 1, this.entranceJ + 2, 1);
            this.baseLayout.fill(0, 0, 11, 1, 5);
            this.baseLayout.fill(0, 9, 11, 11, 5);
            this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ - 2, Direction.WEST, 6);
            this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ + 3, Direction.WEST, 6);
            this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ - 1, Direction.WEST, 3);
            this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ + 2, Direction.WEST, 3);

            while(this.adjustLayoutWithRooms(this.baseLayout)) {
            }

            this.roomFlagsByFloor = new AlternateMansionGenerator.FlagMatrix[3];
            this.roomFlagsByFloor[0] = new AlternateMansionGenerator.FlagMatrix(11, 11, 5);
            this.roomFlagsByFloor[1] = new AlternateMansionGenerator.FlagMatrix(11, 11, 5);
            this.roomFlagsByFloor[2] = new AlternateMansionGenerator.FlagMatrix(11, 11, 5);
            this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[0]);
            this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[1]);
            this.roomFlagsByFloor[0].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
            this.roomFlagsByFloor[1].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
            this.thirdFloorLayout = new AlternateMansionGenerator.FlagMatrix(this.baseLayout.n, this.baseLayout.m, 5);
            this.layoutThirdFloor();
            this.updateRoomFlags(this.thirdFloorLayout, this.roomFlagsByFloor[2]);
        }

        public static boolean isInsideMansion(AlternateMansionGenerator.FlagMatrix layout, int i, int j) {
            int k = layout.get(i, j);
            return k == 1 || k == 2 || k == 3 || k == 4;
        }

        public boolean isRoomId(AlternateMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
            return (this.roomFlagsByFloor[floor].get(i, j) & '\uffff') == roomId;
        }

        @Nullable
        public Direction findConnectedRoomDirection(AlternateMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
            Iterator var6 = Direction.Type.HORIZONTAL.iterator();

            Direction direction;
            do {
                if (!var6.hasNext()) {
                    return null;
                }

                direction = (Direction)var6.next();
            } while(!this.isRoomId(layout, i + direction.getOffsetX(), j + direction.getOffsetZ(), floor, roomId));

            return direction;
        }

        private void layoutCorridor(AlternateMansionGenerator.FlagMatrix layout, int i, int j, Direction direction, int length) {
            if (length > 0) {
                layout.set(i, j, 1);
                layout.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

                Direction direction2;
                for(int k = 0; k < 8; ++k) {
                    direction2 = Direction.fromHorizontal(this.random.nextInt(4));
                    if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.random.nextBoolean())) {
                        int l = i + direction.getOffsetX();
                        int m = j + direction.getOffsetZ();
                        if (layout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0 && layout.get(l + direction2.getOffsetX() * 2, m + direction2.getOffsetZ() * 2) == 0) {
                            this.layoutCorridor(layout, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, length - 1);
                            break;
                        }
                    }
                }

                Direction direction3 = direction.rotateYClockwise();
                direction2 = direction.rotateYCounterclockwise();
                layout.update(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
                layout.update(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), 0, 2);
                layout.update(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
                layout.update(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
                layout.update(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
                layout.update(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
                layout.update(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, 0, 2);
            }
        }

        private boolean adjustLayoutWithRooms(AlternateMansionGenerator.FlagMatrix layout) {
            boolean bl = false;

            for(int i = 0; i < layout.m; ++i) {
                for(int j = 0; j < layout.n; ++j) {
                    if (layout.get(j, i) == 0) {
                        int k = 0;
                        k += isInsideMansion(layout, j + 1, i) ? 1 : 0;
                        k += isInsideMansion(layout, j - 1, i) ? 1 : 0;
                        k += isInsideMansion(layout, j, i + 1) ? 1 : 0;
                        k += isInsideMansion(layout, j, i - 1) ? 1 : 0;
                        if (k >= 3) {
                            layout.set(j, i, 2);
                            bl = true;
                        } else if (k == 2) {
                            int l = 0;
                            l += isInsideMansion(layout, j + 1, i + 1) ? 1 : 0;
                            l += isInsideMansion(layout, j - 1, i + 1) ? 1 : 0;
                            l += isInsideMansion(layout, j + 1, i - 1) ? 1 : 0;
                            l += isInsideMansion(layout, j - 1, i - 1) ? 1 : 0;
                            if (l <= 1) {
                                layout.set(j, i, 2);
                                bl = true;
                            }
                        }
                    }
                }
            }

            return bl;
        }

        private void layoutThirdFloor() {
            List<Pair<Integer, Integer>> list = Lists.newArrayList();
            AlternateMansionGenerator.FlagMatrix flagMatrix = this.roomFlagsByFloor[1];

            int j;
            int l;
            for(int i = 0; i < this.thirdFloorLayout.m; ++i) {
                for(j = 0; j < this.thirdFloorLayout.n; ++j) {
                    int k = flagMatrix.get(j, i);
                    l = k & 983040;
                    if (l == 131072 && (k & 2097152) == 2097152) {
                        list.add(new Pair(j, i));
                    }
                }
            }

            if (list.isEmpty()) {
                this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
            } else {
                Pair<Integer, Integer> pair = list.get(this.random.nextInt(list.size()));
                j = flagMatrix.get(pair.getLeft(), pair.getRight());
                flagMatrix.set(pair.getLeft(), pair.getRight(), j | 4194304);
                Direction direction = this.findConnectedRoomDirection(this.baseLayout, pair.getLeft(), pair.getRight(), 1, j & '\uffff');
                l = pair.getLeft() + direction.getOffsetX();
                int m = pair.getRight() + direction.getOffsetZ();

                for(int n = 0; n < this.thirdFloorLayout.m; ++n) {
                    for(int o = 0; o < this.thirdFloorLayout.n; ++o) {
                        if (!isInsideMansion(this.baseLayout, o, n)) {
                            this.thirdFloorLayout.set(o, n, 5);
                        } else if (o == pair.getLeft() && n == pair.getRight()) {
                            this.thirdFloorLayout.set(o, n, 3);
                        } else if (o == l && n == m) {
                            this.thirdFloorLayout.set(o, n, 3);
                            this.roomFlagsByFloor[2].set(o, n, 8388608);
                        }
                    }
                }

                List<Direction> list2 = Lists.newArrayList();
                Iterator var14 = Direction.Type.HORIZONTAL.iterator();

                while(var14.hasNext()) {
                    Direction direction2 = (Direction)var14.next();
                    if (this.thirdFloorLayout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0) {
                        list2.add(direction2);
                    }
                }

                if (list2.isEmpty()) {
                    this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
                    flagMatrix.set(pair.getLeft(), pair.getRight(), j);
                } else {
                    Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
                    this.layoutCorridor(this.thirdFloorLayout, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);

                    while(this.adjustLayoutWithRooms(this.thirdFloorLayout)) {
                    }

                }
            }
        }

        private void updateRoomFlags(AlternateMansionGenerator.FlagMatrix layout, AlternateMansionGenerator.FlagMatrix roomFlags) {
            ObjectArrayList<Pair<Integer, Integer>> objectArrayList = new ObjectArrayList<>();

            int i;
            for(i = 0; i < layout.m; ++i) {
                for(int j = 0; j < layout.n; ++j) {
                    if (layout.get(j, i) == 2) {
                        objectArrayList.add(new Pair(j, i));
                    }
                }
            }

            Collections.shuffle(objectArrayList, this.random);
            i = 10;
            ObjectListIterator var19 = objectArrayList.iterator();

            while(true) {
                int k;
                int l;
                do {
                    if (!var19.hasNext()) {
                        return;
                    }

                    Pair<Integer, Integer> pair = (Pair)var19.next();
                    k = pair.getLeft();
                    l = pair.getRight();
                } while(roomFlags.get(k, l) != 0);

                int m = k;
                int n = k;
                int o = l;
                int p = l;
                int q = 65536;
                if (roomFlags.get(k + 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k + 1, l + 1) == 0 && layout.get(k + 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k + 1, l + 1) == 2) {
                    n = k + 1;
                    p = l + 1;
                    q = 262144;
                } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k - 1, l + 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k - 1, l + 1) == 2) {
                    m = k - 1;
                    p = l + 1;
                    q = 262144;
                } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l - 1) == 0 && roomFlags.get(k - 1, l - 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l - 1) == 2 && layout.get(k - 1, l - 1) == 2) {
                    m = k - 1;
                    o = l - 1;
                    q = 262144;
                } else if (roomFlags.get(k + 1, l) == 0 && layout.get(k + 1, l) == 2) {
                    n = k + 1;
                    q = 131072;
                } else if (roomFlags.get(k, l + 1) == 0 && layout.get(k, l + 1) == 2) {
                    p = l + 1;
                    q = 131072;
                } else if (roomFlags.get(k - 1, l) == 0 && layout.get(k - 1, l) == 2) {
                    m = k - 1;
                    q = 131072;
                } else if (roomFlags.get(k, l - 1) == 0 && layout.get(k, l - 1) == 2) {
                    o = l - 1;
                    q = 131072;
                }

                int r = this.random.nextBoolean() ? m : n;
                int s = this.random.nextBoolean() ? o : p;
                int t = 2097152;
                if (!layout.anyMatchAround(r, s, 1)) {
                    r = r == m ? n : m;
                    s = s == o ? p : o;
                    if (!layout.anyMatchAround(r, s, 1)) {
                        s = s == o ? p : o;
                        if (!layout.anyMatchAround(r, s, 1)) {
                            r = r == m ? n : m;
                            s = s == o ? p : o;
                            if (!layout.anyMatchAround(r, s, 1)) {
                                t = 0;
                                r = m;
                                s = o;
                            }
                        }
                    }
                }

                for(int u = o; u <= p; ++u) {
                    for(int v = m; v <= n; ++v) {
                        if (v == r && u == s) {
                            roomFlags.set(v, u, 1048576 | t | q | i);
                        } else {
                            roomFlags.set(v, u, q | i);
                        }
                    }
                }

                ++i;
            }
        }
    }

    private static class LayoutGenerator {
        private final StructureManager manager;
        private final Random random;
        private final MansionTemplates roomTemplates;
        private int entranceI;
        private int entranceJ;

        public LayoutGenerator(StructureManager manager, Random random, MansionTemplates roomTemplates) {
            this.manager = manager;
            this.random = random;
            this.roomTemplates = roomTemplates;
        }

        public void generate(BlockPos pos, BlockRotation rotation, List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.MansionParameters parameters) {
            AlternateMansionGenerator.GenerationPiece generationPiece = new AlternateMansionGenerator.GenerationPiece();
            generationPiece.position = pos;
            generationPiece.rotation = rotation;
            generationPiece.template = roomTemplates.walls.lowerWall;
            AlternateMansionGenerator.GenerationPiece generationPiece2 = new AlternateMansionGenerator.GenerationPiece();
            this.addEntrance(pieces, generationPiece);
            generationPiece2.position = generationPiece.position.up(8);
            generationPiece2.rotation = generationPiece.rotation;
            generationPiece2.template = roomTemplates.walls.upperWall;
            if (!pieces.isEmpty()) {
            }

            AlternateMansionGenerator.FlagMatrix flagMatrix = parameters.baseLayout;
            AlternateMansionGenerator.FlagMatrix flagMatrix2 = parameters.thirdFloorLayout;
            this.entranceI = parameters.entranceI + 1;
            this.entranceJ = parameters.entranceJ + 1;
            int i = parameters.entranceI + 1;
            int j = parameters.entranceJ;
            this.addOuterWall(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
            this.addOuterWall(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
            AlternateMansionGenerator.GenerationPiece generationPiece3 = new AlternateMansionGenerator.GenerationPiece();
            generationPiece3.position = generationPiece.position.up(19);
            generationPiece3.rotation = generationPiece.rotation;
            generationPiece3.template = roomTemplates.walls.upperWall;
            boolean bl = false;

            int l;
            for(int k = 0; k < flagMatrix2.m && !bl; ++k) {
                for(l = flagMatrix2.n - 1; l >= 0 && !bl; --l) {
                    if (AlternateMansionGenerator.MansionParameters.isInsideMansion(flagMatrix2, l, k)) {
                        generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.entranceJ) * 8);
                        generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.entranceI) * 8);
                        this.addWallPiece(pieces, generationPiece3);
                        this.addOuterWall(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
                        bl = true;
                    }
                }
            }

            this.addRoof(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
            this.addRoof(pieces, pos.up(27), rotation, flagMatrix2, (AlternateMansionGenerator.FlagMatrix)null);
            if (!pieces.isEmpty()) {
            }

            for(l = 0; l < 3; ++l) {
                MansionTemplates.FloorTemplates floor = switch (l) {
                    case 0 -> roomTemplates.firstFloor;
                    case 1 -> roomTemplates.secondFloor;
                    case 2 -> roomTemplates.thirdFloor;
                    default -> throw new IllegalStateException("Unexpected value: " + l);
                };
                BlockPos blockPos = pos.up(8 * l + (l == 2 ? 3 : 0));
                AlternateMansionGenerator.FlagMatrix flagMatrix3 = parameters.roomFlagsByFloor[l];
                AlternateMansionGenerator.FlagMatrix flagMatrix4 = l == 2 ? flagMatrix2 : flagMatrix;
                String string = l == 0 ? "carpet_south_1" : "carpet_south_2";
                String string2 = l == 0 ? "carpet_west_1" : "carpet_west_2";

                for(int m = 0; m < flagMatrix4.m; ++m) {
                    for(int n = 0; n < flagMatrix4.n; ++n) {
                        if (flagMatrix4.get(n, m) == 1) {
                            BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.entranceJ) * 8);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.entranceI) * 8);
                            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, "corridor_floor", blockPos2, rotation));
                            if (flagMatrix4.get(n, m - 1) == 1 || (flagMatrix3.get(n, m - 1) & 8388608) == 8388608) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
                            }

                            if (flagMatrix4.get(n + 1, m) == 1 || (flagMatrix3.get(n + 1, m) & 8388608) == 8388608) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation));
                            }

                            if (flagMatrix4.get(n, m + 1) == 1 || (flagMatrix3.get(n, m + 1) & 8388608) == 8388608) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation));
                            }

                            if (flagMatrix4.get(n - 1, m) == 1 || (flagMatrix3.get(n - 1, m) & 8388608) == 8388608) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, string2, blockPos2.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation));
                            }
                        }
                    }
                }

                String string3 = l == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String string4 = l == 0 ? "indoors_door_1" : "indoors_door_2";
                List<Direction> list = Lists.newArrayList();

                for(int o = 0; o < flagMatrix4.m; ++o) {
                    for(int p = 0; p < flagMatrix4.n; ++p) {
                        boolean bl2 = l == 2 && flagMatrix4.get(p, o) == 3;
                        if (flagMatrix4.get(p, o) == 2 || bl2) {
                            int q = flagMatrix3.get(p, o);
                            int r = q & 983040;
                            int s = q & '\uffff';
                            bl2 = bl2 && (q & 8388608) == 8388608;
                            list.clear();
                            if ((q & 2097152) == 2097152) {
                                Iterator var29 = Direction.Type.HORIZONTAL.iterator();

                                while(var29.hasNext()) {
                                    Direction direction = (Direction)var29.next();
                                    if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) == 1) {
                                        list.add(direction);
                                    }
                                }
                            }

                            Direction direction2 = null;
                            if (!list.isEmpty()) {
                                direction2 = (Direction)list.get(this.random.nextInt(list.size()));
                            } else if ((q & 1048576) == 1048576) {
                                direction2 = Direction.UP;
                            }

                            BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.entranceJ) * 8);
                            blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.entranceI) * 8);
                            if (AlternateMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p - 1, o) && !parameters.isRoomId(flagMatrix4, p - 1, o, l, s)) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
                            }

                            BlockPos blockPos4;
                            if (flagMatrix4.get(p + 1, o) == 1 && !bl2) {
                                blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
                            }

                            if (AlternateMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p, o + 1) && !parameters.isRoomId(flagMatrix4, p, o + 1, l, s)) {
                                blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
                                blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                            }

                            if (flagMatrix4.get(p, o - 1) == 1 && !bl2) {
                                blockPos4 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
                                blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                            }

                            if (r == 65536) {
                                this.addSmallRoom(pieces, blockPos3, rotation, direction2, floor);
                                
                            
                            } else {
                                Direction direction3;
                                if (r == 131072 && direction2 != null) {
                                    direction3 = parameters.findConnectedRoomDirection(flagMatrix4, p, o, l, s);
                                    boolean bl3 = (q & 4194304) == 4194304;
                                    this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, floor, bl3);
                                } else if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
                                    direction3 = direction2.rotateYClockwise();
                                    if (!parameters.isRoomId(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), l, s)) {
                                        direction3 = direction3.getOpposite();
                                    }

                                    this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, floor);
                                } else if (r == 262144 && direction2 == Direction.UP) {
                                    this.addBigSecretRoom(pieces, blockPos3, rotation, floor);
                                }
                            }
                        }
                    }
                }
            }

        }

        private void addOuterWall(List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.GenerationPiece wallPiece, AlternateMansionGenerator.FlagMatrix layout, Direction direction, int startI, int startJ, int endI, int endJ) {
            int i = startI;
            int j = startJ;
            Direction direction2 = direction;

            do {
                if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())) {
                    this.turnLeft(pieces, wallPiece);
                    direction = direction.rotateYClockwise();
                    if (i != endI || j != endJ || direction2 != direction) {
                        this.addWallPiece(pieces, wallPiece);
                    }
                } else if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ()) && AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), j + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
                    this.turnRight(pieces, wallPiece);
                    i += direction.getOffsetX();
                    j += direction.getOffsetZ();
                    direction = direction.rotateYCounterclockwise();
                } else {
                    i += direction.getOffsetX();
                    j += direction.getOffsetZ();
                    if (i != endI || j != endJ || direction2 != direction) {
                        this.addWallPiece(pieces, wallPiece);
                    }
                }
            } while(i != endI || j != endJ || direction2 != direction);

        }

        private void addRoof(List<AlternateMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, AlternateMansionGenerator.FlagMatrix layout, @Nullable AlternateMansionGenerator.FlagMatrix nextFloorLayout) {
            int i;
            int j;
            BlockPos blockPos;
            boolean bl;
            BlockPos blockPos2;
            for(i = 0; i < layout.m; ++i) {
                for(j = 0; j < layout.n; ++j) {
                    blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                    blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                    bl = nextFloorLayout != null && AlternateMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                    if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && !bl) {
                        pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roof, blockPos.up(3), rotation, BlockMirror.NONE));
                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofSide, blockPos2, rotation, BlockMirror.NONE));
                        }

                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofSide, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180), BlockMirror.NONE));
                        }

                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofSide, blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90), BlockMirror.NONE));
                        }

                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofSide, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.NONE));
                        }
                    }
                }
            }

            if (nextFloorLayout != null) {
                for(i = 0; i < layout.m; ++i) {
                    for(j = 0; j < layout.n; ++j) {
                        blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                        blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                        bl = AlternateMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                        if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && bl) {
                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallWall, blockPos2, rotation));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                                blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallWall, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 0);
                                blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallWall, blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                                blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallWall, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                                if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 2);
                                    pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallCornerWall, blockPos2, rotation));
                                }

                                if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 8);
                                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                                    pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallCornerWall, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                                }
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                                if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                    blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 2);
                                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                                    pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallCornerWall, blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                                }

                                if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                    blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                                    pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.smallCornerWall, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                                }
                            }
                        }
                    }
                }
            }

            for(i = 0; i < layout.m; ++i) {
                for(j = 0; j < layout.n; ++j) {
                    blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                    blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                    bl = nextFloorLayout != null && AlternateMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                    if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && !bl) {
                        BlockPos blockPos3;
                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofCorner, blockPos3, rotation, BlockMirror.NONE));
                            } else if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i + 1)) {
                                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 5);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofInnerCorner, blockPos3, rotation, BlockMirror.NONE));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofCorner, blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90), BlockMirror.NONE));
                            } else if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i - 1)) {
                                blockPos3 = blockPos.offset(rotation.rotate(Direction.EAST), 9);
                                blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 2);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofInnerCorner, blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.NONE));
                            }
                        }

                        if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 0);
                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofCorner, blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.NONE));
                            } else if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i + 1)) {
                                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                                blockPos3 = blockPos3.offset(rotation.rotate(Direction.WEST), 3);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofInnerCorner, blockPos3, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90), BlockMirror.NONE));
                            }

                            if (!AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofCorner, blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180), BlockMirror.NONE));
                            } else if (AlternateMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i - 1)) {
                                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 1);
                                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.roofInnerCorner, blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_180), BlockMirror.NONE));
                            }
                        }
                    }
                }
            }

        }

        private void addEntrance(List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.GenerationPiece wallPiece) {
            Direction direction = wallPiece.rotation.rotate(Direction.WEST);
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.entrance, wallPiece.position.offset(direction, 9), wallPiece.rotation, BlockMirror.NONE));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 16);
        }

        private void addWallPiece(List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.GenerationPiece wallPiece) {
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, wallPiece.template, wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 7), wallPiece.rotation, BlockMirror.NONE));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 8);
        }

        private void turnLeft(List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.GenerationPiece wallPiece) {
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -1);
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomTemplates.walls.cornerWall, wallPiece.position, wallPiece.rotation, BlockMirror.NONE));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -7);
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.WEST), -6);
            wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
        }

        private void turnRight(List<AlternateMansionGenerator.Piece> pieces, AlternateMansionGenerator.GenerationPiece wallPiece) {
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 6);
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 8);
            wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
        }

        private void addSmallRoom(List<AlternateMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction direction, MansionTemplates.FloorTemplates floorTemplates) {
            Identifier smallRoom = getRoom(floorTemplates.small, this.random);
            if (direction == Direction.UP) {
                smallRoom = getRoom(floorTemplates.smallSecret, this.random);
            }
            BlockRotation blockRotation = BlockRotation.NONE.rotate(rotateFromDirection(direction)).rotate(BlockRotation.COUNTERCLOCKWISE_90);
            BlockPos blockPos = Structure.applyTransformedOffset(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation, 7, 7);
            blockRotation = blockRotation.rotate(rotation);
            blockPos = blockPos.rotate(rotation);
            BlockPos blockPos2 = pos.add(blockPos.getX(), 0, blockPos.getZ());
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, smallRoom, blockPos2, blockRotation, BlockMirror.NONE));
        }

        private void addMediumRoom(List<AlternateMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, MansionTemplates.FloorTemplates floorTemplates, boolean staircase) {
            BlockPos blockPos;
            BlockMirror blockMirror = BlockMirror.NONE;
            Direction rotationDirection;
            Direction rotationDirection2;
            Identifier roomType;
            int i = 0;
            int j = 0;
            if (entranceDirection == Direction.UP) {
                rotationDirection = Direction.EAST;
                if (connectedRoomDirection == Direction.EAST) {
                    i = 14;
                }
                blockPos = pos.offset(rotation.rotate(rotationDirection), i+1);
                rotation = rotation.rotate(rotateFromDirection(connectedRoomDirection));
                if (connectedRoomDirection == Direction.SOUTH) {
                    rotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
                }
                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, getRoom(floorTemplates.mediumSecret, this.random), blockPos, rotation, BlockMirror.NONE));
            } else if (entranceDirection.getAxis() == connectedRoomDirection.getAxis()) {
                rotationDirection = entranceDirection == Direction.EAST ? Direction.WEST : Direction.EAST;
                rotationDirection2 = entranceDirection.rotateCounterclockwise(Axis.Y).getDirection() == Direction.AxisDirection.NEGATIVE ? Direction.SOUTH : Direction.NORTH;
                if (entranceDirection == Direction.SOUTH) {
                    i = 1;
                    j = 8;
                } else if (entranceDirection == Direction.NORTH) {
                    i = 7;
                    j = 14;
                } else if (entranceDirection == Direction.WEST) {
                    i = 15;
                } else if (entranceDirection == Direction.EAST) {
                    i = 7;
                    j = 6;
                }
                blockPos = pos.offset(rotation.rotate(rotationDirection), i).offset(rotation.rotate(rotationDirection2), j);
                rotation = rotation.rotate(rotateFromDirection(connectedRoomDirection));
                roomType = staircase ? roomTemplates.genericStaircase : getRoom(floorTemplates.mediumGeneric, this.random);
                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomType, blockPos, rotation, BlockMirror.NONE));
            } else {
                if (entranceDirection.rotateCounterclockwise(Axis.Y) == connectedRoomDirection) {
                    blockMirror = entranceDirection.getDirection() == Direction.AxisDirection.NEGATIVE ? BlockMirror.FRONT_BACK : BlockMirror.LEFT_RIGHT;
                }
                if (entranceDirection == Direction.WEST || connectedRoomDirection == Direction.WEST) {
                    i = 7;
                } else {
                    i = 1;
                }
                blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
                if (entranceDirection == Direction.NORTH || connectedRoomDirection == Direction.NORTH) {
                    blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                }
                if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
                    rotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
                } else if (entranceDirection == Direction.SOUTH || connectedRoomDirection == Direction.WEST) {
                    rotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
                } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
                    rotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                }
                roomType = staircase ? roomTemplates.functionalStaircase : getRoom(floorTemplates.mediumFunctional, this.random);
                pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, roomType, blockPos, rotation, blockMirror));
            }
        }

        private void addBigRoom(List<AlternateMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, MansionTemplates.FloorTemplates floorTemplates) {
            int i = 0;
            int j = 0;
            BlockRotation blockRotation = rotation;
            BlockMirror blockMirror = BlockMirror.NONE;
            if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
                i = -7;
            } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
                i = -7;
                j = 6;
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
                i = 1;
                j = 14;
                blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
                i = 7;
                j = 14;
                blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
                i = 7;
                j = -8;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
                i = 1;
                j = -8;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
                i = 15;
                j = 6;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
                i = 15;
                blockMirror = BlockMirror.FRONT_BACK;
            }

            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), j);
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, getRoom(floorTemplates.large, this.random), blockPos, blockRotation, blockMirror));
        }

        private void addBigSecretRoom(List<AlternateMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, MansionTemplates.FloorTemplates floorTemplates) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            pieces.add(new AlternateMansionGenerator.Piece(roomTemplates, this.manager, getRoom(floorTemplates.largeSecret, this.random), blockPos, rotation, BlockMirror.NONE));
        }

        private BlockRotation rotateFromDirection(Direction direction) {
            return switch (direction) {
                case UP, DOWN, NORTH -> BlockRotation.NONE;
                case EAST -> BlockRotation.CLOCKWISE_90;
                case SOUTH -> BlockRotation.CLOCKWISE_180;
                case WEST -> BlockRotation.COUNTERCLOCKWISE_90;
            };
        }

        private Identifier getRoom(List<Identifier> roomType, Random random) {
            return roomType.get(random.nextInt(roomType.size()));
        }
    }

    private static class FlagMatrix {
        private final int[][] array;
        final int n;
        final int m;
        private final int fallback;

        public FlagMatrix(int n, int m, int fallback) {
            this.n = n;
            this.m = m;
            this.fallback = fallback;
            this.array = new int[n][m];
        }

        public void set(int i, int j, int value) {
            if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
                this.array[i][j] = value;
            }

        }

        public void fill(int i0, int j0, int i1, int j1, int value) {
            for(int i = j0; i <= j1; ++i) {
                for(int j = i0; j <= i1; ++j) {
                    this.set(j, i, value);
                }
            }

        }

        public int get(int i, int j) {
            return i >= 0 && i < this.n && j >= 0 && j < this.m ? this.array[i][j] : this.fallback;
        }

        public void update(int i, int j, int expected, int newValue) {
            if (this.get(i, j) == expected) {
                this.set(i, j, newValue);
            }

        }

        public boolean anyMatchAround(int i, int j, int value) {
            return this.get(i - 1, j) == value || this.get(i + 1, j) == value || this.get(i, j + 1) == value || this.get(i, j - 1) == value;
        }
    }

    private static class GenerationPiece {
        public BlockRotation rotation;
        public BlockPos position;
        public Identifier template;

        GenerationPiece() {
        }
    }

    public static class Piece extends SimpleStructurePiece {
        public Piece(MansionTemplates mansionTemplates, StructureManager manager, String template, BlockPos pos, BlockRotation rotation) {
            this(mansionTemplates, manager, new Identifier("woodland_mansion/"+template), pos, rotation, BlockMirror.NONE);
        }

        public Piece(MansionTemplates mansionTemplates, StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
            this(mansionTemplates, manager, template, pos, rotation, BlockMirror.NONE);
        }

        public Piece(MansionTemplates mansionTemplates, StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
            super(NJBStructurePieces.ALTERNATE_MANSION_PIECE, 0, manager, template, String.valueOf(template), createPlacementData(mansionTemplates, mirror, rotation), pos);
        }

        public Piece(StructureManager manager, DynamicRegistryManager dynamicRegistryManager, NbtCompound nbt) {
            super(NJBStructurePieces.ALTERNATE_MANSION_PIECE, nbt, manager, (id) -> {
                return createPlacementData(dynamicRegistryManager, BlockMirror.valueOf(nbt.getString("Mi")), BlockRotation.valueOf(nbt.getString("Rot")));
            });
        }

        public Piece(StructureContext structureContext, NbtCompound nbtCompound) {
            this(structureContext.structureManager(), structureContext.registryManager(), nbtCompound);
        }

        private static StructurePlacementData createPlacementData(MansionTemplates mansionTemplates,  BlockMirror mirror, BlockRotation rotation) {
            StructurePlacementData structurePlacementData = new StructurePlacementData().setIgnoreEntities(false).setRotation(rotation).setMirror(mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            List<StructureProcessor> processors = mansionTemplates.mansionProcessor.value().getList();
            processors.forEach(structurePlacementData::addProcessor);
            return structurePlacementData;
        }

        private static StructurePlacementData createPlacementData(DynamicRegistryManager dynamicRegistryManager, BlockMirror mirror, BlockRotation rotation) {
            StructurePlacementData structurePlacementData = new StructurePlacementData().setIgnoreEntities(false).setRotation(rotation).setMirror(mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            RegistryKey<StructureProcessorList> processorList = RegistryKey.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, new Identifier(NotJustBiomes.MOD_ID, "mansion/generic"));
            List<StructureProcessor> processors = dynamicRegistryManager.get(Registry.STRUCTURE_PROCESSOR_LIST_KEY).get(processorList).getList();
            processors.forEach(structurePlacementData::addProcessor);
            return structurePlacementData;
        }

        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putString("Rot", this.placementData.getRotation().name());
            nbt.putString("Mi", this.placementData.getMirror().name());
        }

        protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockRotation blockRotation = this.placementData.getRotation();
                BlockState blockState = Blocks.CHEST.getDefaultState();
                if ("ChestWest".equals(metadata)) {
                    blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
                } else if ("ChestEast".equals(metadata)) {
                    blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
                } else if ("ChestSouth".equals(metadata)) {
                    blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
                } else if ("ChestNorth".equals(metadata)) {
                    blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
                }

                this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
            } else {
                List<MobEntity> list = new ArrayList();
                label60:
                switch (metadata) {
                    case "Mage":
                        list.add(EntityType.EVOKER.create(world.toServerWorld()));
                        break;
                    case "Warrior":
                        list.add(EntityType.VINDICATOR.create(world.toServerWorld()));
                        break;
                    case "Prisoner":
                        list.add(EntityType.VILLAGER.create(world.toServerWorld()));
                        break;
                    default:
                        return;
                }

                Iterator var7 = list.iterator();

                while(var7.hasNext()) {
                    MobEntity mobEntity = (MobEntity)var7.next();
                    if (mobEntity != null) {
                        mobEntity.setPersistent();
                        mobEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                        mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
                        world.spawnEntityAndPassengers(mobEntity);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                    }
                }
            }

        }
    }
}