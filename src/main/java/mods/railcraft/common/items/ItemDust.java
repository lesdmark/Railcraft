/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemDust extends ItemRailcraftSubtyped {

    public ItemDust() {
        super(EnumDust.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumDust d : EnumDust.VALUES) {
            ItemStack stack = new ItemStack(this, 1, d.ordinal());
            RailcraftRegistry.register(this, d, stack);
            ForestryPlugin.addBackpackItem("forestry.miner", stack);
            for (String tag : d.oreTags)
                OreDictionary.registerOre(tag, stack.copy());
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(getStack(3, EnumDust.VOID),
                RailcraftItems.DUST, EnumDust.COAL,
                RailcraftItems.DUST, EnumDust.ENDER,
                RailcraftItems.DUST, EnumDust.OBSIDIAN);
    }

    @Override
    public void finalizeDefinition() {
        if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC)) {
            if (RailcraftConfig.getRecipeConfig("ic2.macerator.charcoal")) {
                IC2Plugin.addMaceratorRecipe(new ItemStack(Items.COAL, 1, 1), getStack(EnumDust.CHARCOAL));
            }
            if (!Mod.IC2_CLASSIC.isLoaded()) {
                if (RailcraftConfig.getRecipeConfig("ic2.macerator.slag")) {
                    IC2Plugin.addMaceratorRecipe(ModItems.SLAG.getStack(), getStack(EnumDust.SLAG));
                }
            }
        }
    }

    public enum EnumDust implements IVariantEnum {

        OBSIDIAN("dustObsidian"),
        SULFUR("dustSulfur"),
        SALTPETER("dustSaltpeter"),
        CHARCOAL("dustCharcoal"),
        SLAG("dustSlag"),
        COAL("dustCoal"),
        ENDER("dustEnderPearl", "dustEnder"),
        VOID("dustVoid");
        public static final EnumDust[] VALUES = values();
        private final String[] oreTags;

        EnumDust(String... oreTags) {
            this.oreTags = oreTags;
        }

        @Override
        public @Nullable String getOreTag() {
            return oreTags[0];
        }

        public String[] getOreTags() {
            return oreTags;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
