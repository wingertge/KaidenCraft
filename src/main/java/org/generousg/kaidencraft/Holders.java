package org.generousg.kaidencraft;

import org.generousg.fruitylib.config.BlockInstances;
import org.generousg.fruitylib.config.ItemInstances;
import org.generousg.fruitylib.config.RegisterBlock;
import org.generousg.fruitylib.config.RegisterItem;
import org.generousg.kaidencraft.blocks.*;
import org.generousg.kaidencraft.items.ItemWrench;

public class Holders {
    public static class Blocks implements BlockInstances {
        @RegisterBlock(name = "boiler", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = BlockBoiler.TileEntityBoiler.class)
        public static BlockBoiler blockBoiler;

        @RegisterBlock(name = "boilertank", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = BlockBoilerTank.TileEntityBoilerTank.class)
        public static BlockBoilerTank blockBoilerTank;

        @RegisterBlock(name = "smallturbine", creativeTab = "tabKaidenCraft", hasInfo = true)
        public static BlockSmallTurbine blockSmallTurbine;

        @RegisterBlock(name = "smalldynamo", creativeTab = "tabKaidenCraft", hasInfo = true)
        public static BlockSmallDynamo blockSmallDynamo;

        @RegisterBlock(name = "inductionheater", creativeTab = "tabKaidenCraft", hasInfo = true)
        public static BlockInductionHeater blockInductionHeater;
    }

    public static class Items implements ItemInstances {
        @RegisterItem(name = "wrench", creativeTab = "tabKaidenCraft", hasInfo = true)
        public static ItemWrench itemWrench;
    }

    public static class Config {

    }
}
