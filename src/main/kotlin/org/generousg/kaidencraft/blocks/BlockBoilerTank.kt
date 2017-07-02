package org.generousg.kaidencraft.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.generousg.fruitylib.blocks.BlockMultiblockPart
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.kaidencraft.KaidenCraft
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock
import kotlin.reflect.KClass


class BlockBoilerTank : BlockMultiblockPart(Material.IRON), ITileEntityProvider {
    override fun getValidComponents(): List<KClass<out FruityBlock>> = arrayListOf(BlockBoiler::class, BlockBoilerTank::class)

    override fun getMultiblockPart(world: IBlockAccess, pos: BlockPos, state: IBlockState): IBlockState {
        return state
    }

    class TileEntityBoilerTank : TileEntityMultiblockPart(BlockBoiler::class, BlockBoilerTank::class) {
        override fun createEntity(world: World): EntityMultiblock = EntityBoilerMultiblock(world)
    }

    override val mod: Any = KaidenCraft.instance
    override fun createNewTileEntity(worldIn: World?, meta: Int) = TileEntityBoilerTank()
}