package org.generousg.kaidencraft.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.kaidencraft.KaidenCraft
import org.generousg.kaidencraft.blocks.tileentities.TileEntityBoiler


class BlockBoiler : FruityBlock(Material.ROCK), ITileEntityProvider {
    override val mod: Any
        get() = KaidenCraft.instance!!

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityBoiler()
    }
}