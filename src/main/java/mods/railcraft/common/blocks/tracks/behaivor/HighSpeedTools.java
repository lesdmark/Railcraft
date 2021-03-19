/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.modules.ModuleTracksHighSpeed;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class HighSpeedTools {
    public static final float SPEED_EXPLODE = 0.5f;
    public static final float SPEED_CUTOFF = 0.499f;
    public static final int LOOK_AHEAD_DIST = 2;
    public static final float SPEED_SLOPE = 0.45f;

    public static void checkSafetyAndExplode(World world, BlockPos pos, EntityMinecart cart) {
        if (!isTrackSafeForHighSpeed(world, pos, cart)) {
            CartTools.explodeCart(cart);
        }
    }

    public static boolean isTrackSafeForHighSpeed(World world, BlockPos pos, EntityMinecart cart) {
        if (!isHighSpeedTrackAt(world, pos))
            return false;
        BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
        if (!TrackShapeHelper.isStraight(dir)) {
            return false;
        }
        if (TrackShapeHelper.isNorthSouth(dir)) {
            BlockPos north = pos.north();
            BlockPos south = pos.south();
            return (isTrackHighSpeedCapable(world, north) || isTrackHighSpeedCapable(world, north.up()) || isTrackHighSpeedCapable(world, north.down()))
                    && (isTrackHighSpeedCapable(world, south) || isTrackHighSpeedCapable(world, south.up()) || isTrackHighSpeedCapable(world, south.down()));
        } else if (TrackShapeHelper.isEastWest(dir)) {
            BlockPos east = pos.east();
            BlockPos west = pos.west();
            return (isTrackHighSpeedCapable(world, east) || isTrackHighSpeedCapable(world, east.up()) || isTrackHighSpeedCapable(world, east.down()))
                    && (isTrackHighSpeedCapable(world, west) || isTrackHighSpeedCapable(world, west.up()) || isTrackHighSpeedCapable(world, west.down()));
        }
        return false;
    }

    private static boolean isTrackHighSpeedCapable(World world, BlockPos pos) {
        return !world.isBlockLoaded(pos) || isHighSpeedTrackAt(world, pos);
    }

    private static void limitSpeed(EntityMinecart cart) {
        cart.motionX = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionX)), cart.motionX);
        cart.motionZ = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionZ)), cart.motionZ);
    }

    public static void performHighSpeedChecks(World world, BlockPos pos, EntityMinecart cart, @Nullable TrackKit trackKit) {
        boolean highSpeed = isTravellingHighSpeed(cart);
        if (highSpeed) {
            checkSafetyAndExplode(world, pos, cart);
        } else if (trackKit == TrackKits.BOOSTER.getTrackKit() || trackKit == TrackKits.HIGH_SPEED_TRANSITION.getTrackKit()) {
            if (isTrackSafeForHighSpeed(world, pos, cart)) {
                if (Math.abs(cart.motionX) > SPEED_CUTOFF) {
                    cart.motionX = Math.copySign(SPEED_CUTOFF, cart.motionX);
                    setTravellingHighSpeed(cart, true);
                }
                if (Math.abs(cart.motionZ) > SPEED_CUTOFF) {
                    cart.motionZ = Math.copySign(SPEED_CUTOFF, cart.motionZ);
                    setTravellingHighSpeed(cart, true);
                }
            }
        } else {
            limitSpeed(cart);
        }
    }

    public static boolean isHighSpeedTrackAt(IBlockAccess world, BlockPos pos) {
        return TrackTools.getTrackTypeAt(world, pos).isHighSpeed();
    }

    public static float speedForNextTrack(World world, BlockPos pos, int dist, @Nullable EntityMinecart cart) {
        float maxSpeed = ModuleTracksHighSpeed.config.maxSpeed;
        if (dist < LOOK_AHEAD_DIST)
            for (EnumFacing side : EnumFacing.HORIZONTALS) {
                BlockPos nextPos = pos.offset(side);
                boolean foundTrack = TrackTools.isRailBlockAt(world, nextPos);
                if (!foundTrack) {
                    if (TrackTools.isRailBlockAt(world, nextPos.up())) {
                        foundTrack = true;
                        nextPos = nextPos.up();
                    } else if (TrackTools.isRailBlockAt(world, nextPos.down())) {
                        foundTrack = true;
                        nextPos = nextPos.down();
                    }
                }
                if (foundTrack) {
                    BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, nextPos, cart);
                    if (dir.isAscending())
                        return SPEED_SLOPE;
                    maxSpeed = speedForNextTrack(world, nextPos, dist + 1, cart);
                    if (maxSpeed == SPEED_SLOPE)
                        return SPEED_SLOPE;
                }
            }

        return maxSpeed;
    }

    public static void setTravellingHighSpeed(EntityMinecart cart, boolean flag) {
        cart.getEntityData().setBoolean(CartConstants.TAG_HIGH_SPEED, flag);
    }

    public static boolean isTravellingHighSpeed(EntityMinecart cart) {
        return cart.getEntityData().getBoolean(CartConstants.TAG_HIGH_SPEED);
    }
}
