package org.generousg.kaidencraft.blocks.tileentities

import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart


abstract class TileEntityBoilerPart : TileEntityMultiblockPart() {
    init {
        (block.value as FruityBlock).neighborChangedEvent += {

        }
    }
}