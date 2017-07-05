package org.generousg.kaidencraft

import org.generousg.fruitylib.config.BlockInstances
import org.generousg.fruitylib.config.ItemInstances
import org.generousg.fruitylib.config.RegisterBlock
import org.generousg.fruitylib.config.RegisterItem
import org.generousg.kaidencraft.blocks.*
import org.generousg.kaidencraft.items.ItemWrench

class Holders {
    class Blocks : BlockInstances {
        companion object {
            @RegisterBlock(name = "boiler", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = BlockBoiler.TileEntityBoiler::class)
            lateinit var blockBoiler: BlockBoiler

            @RegisterBlock(name = "boilertank", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = BlockBoilerTank.TileEntityBoilerTank::class)
            lateinit var blockBoilerTank: BlockBoilerTank

            @RegisterBlock(name = "smallturbine", creativeTab = "tabKaidenCraft", hasInfo = true)
            lateinit var blockSmallTurbine: BlockSmallTurbine

            @RegisterBlock(name = "smalldynamo", creativeTab = "tabKaidenCraft", hasInfo = true)
            lateinit var blockSmallDynamo: BlockSmallDynamo

            @RegisterBlock(name = "inductionheater", creativeTab = "tabKaidenCraft", hasInfo = true)
            lateinit var blockInductionHeater: BlockInductionHeater
        }
    }

    class Items : ItemInstances {
        companion object {
            @RegisterItem(name = "wrench", creativeTab = "tabKaidenCraft", hasInfo = true)
            lateinit var itemWrench: ItemWrench
        }
    }

    class EntityIds {
        companion object {
            val MULTIBLOCK_BOILER = 0
        }
    }
    class Config
}
