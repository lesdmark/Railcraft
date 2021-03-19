/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by CovertJaguar on 8/8/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GlassReplacer extends BlockStrengthGlass {

    protected GlassReplacer() {
        setTickRandomly(true);
    }

    @Override
    public void initializeDefinition() {
    }

    @Override
    public void defineRecipes() {
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public boolean requiresUpdates() {
        return true;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        replace(world, pos, state);
        Arrays.stream(EnumFacing.VALUES).forEach(side -> {
            try {
                IBlockState neighbor = WorldPlugin.getBlockState(world, pos.offset(side));
                if (neighbor.getBlock() == this)
                    world.scheduleUpdate(pos.offset(side), neighbor.getBlock(), 1);
            } catch (Exception ignored) {}
        });
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(world, pos, neighbor);
        if (world instanceof World)
            replace((World) world, pos, WorldPlugin.getBlockState(world, pos));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        replace(worldIn, pos, state);
    }

    private void replace(World worldIn, BlockPos pos, IBlockState state) {
        WorldPlugin.setBlockState(worldIn, pos, RailcraftBlocks.GLASS.getDefaultState().withProperty(EnumColor.PROPERTY, state.getValue(EnumColor.PROPERTY)));
        WorldPlugin.notifyBlocksOfNeighborChange(worldIn, pos, this);
    }
}
