/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:tracks|electric", dependencyClasses = {ModuleLocomotives.class, ModuleCharge.class}, description = "electric tracks")
public class ModuleTracksElectric extends RailcraftModulePayload {

    public ModuleTracksElectric() {
        add(
                RailcraftBlocks.TRACK_FLEX_ELECTRIC
        );
    }
}
