/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.anchor;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileAnchorAdmin extends TileAnchorWorld {

    @Override
    public AnchorVariant getMachineType() {
        return AnchorVariant.ADMIN;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }
}