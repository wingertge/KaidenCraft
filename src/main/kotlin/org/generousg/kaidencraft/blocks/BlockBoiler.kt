package org.generousg.kaidencraft.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.generousg.fruitylib.blocks.BlockMultiblockPart
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.fruitylib.getBlockInDirection
import org.generousg.fruitylib.inDirection
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.MultiblockPart
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.fruitylib.sync.SyncableUUID
import org.generousg.fruitylib.util.bitmap.EnumBitMap
import org.generousg.kaidencraft.KaidenCraft
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock
import kotlin.reflect.KClass


class BlockBoiler : BlockMultiblockPart(Material.ROCK), ITileEntityProvider {
    override fun getValidComponents(): List<KClass<out FruityBlock>> = arrayListOf(BlockBoiler::class, BlockBoilerTank::class)

    override fun getMultiblockPart(world: IBlockAccess, pos: BlockPos, state: IBlockState): IBlockState {
        val neighbors = hashMapOf<EnumFacing, Block>()
        val thisTE = world.getTileEntity(pos) as TileEntityMultiblockPart
        if(thisTE.multiblockId == SyncableUUID.IDENTITY) return state.withProperty(MB_PART, MultiblockPart.SINGLE).withProperty(FACING, NORTH)
        EnumFacing.values().filter { it != EnumFacing.UP && it != EnumFacing.DOWN }.forEach { neighbors.put(it, world.getBlockInDirection(it, pos).block) }
        val validSidesEnums = neighbors.filter { (key, value) -> value is BlockBoiler &&
                (world.getTileEntity(pos.inDirection(key)) as TileEntityMultiblockPart).multiblockId == thisTE.multiblockId }.keys
        val validSides = EnumBitMap(validSidesEnums)

        when(validSidesEnums.size) {
            0 -> return state.withProperty(MB_PART, MultiblockPart.SINGLE)
            1 -> return state.withProperty(MB_PART, MultiblockPart.END)
                    .withProperty(FACING, validSidesEnums.elementAt(0))
            2 -> {
                if(validSides.contains(NORTH)) {
                    if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, MultiblockPart.LINE_EDGE)
                            .withProperty(FACING, NORTH)
                    else if(validSides.contains(WEST)) return state.withProperty(MB_PART, MultiblockPart.CORNER)
                            .withProperty(FACING, NORTH)
                    else return state.withProperty(MB_PART, MultiblockPart.CORNER)
                            .withProperty(FACING, EAST)
                } else if(validSides.contains(EAST)) {
                    if(validSides.contains(WEST)) return state.withProperty(MB_PART, MultiblockPart.LINE_EDGE)
                            .withProperty(FACING, EAST)
                    else if(validSides.contains(EnumFacing.SOUTH)) return state.withProperty(MB_PART, MultiblockPart.CORNER)
                            .withProperty(FACING, SOUTH)
                } else if(validSides.contains(EnumFacing.SOUTH)) return state.withProperty(MB_PART, MultiblockPart.CORNER)
                        .withProperty(FACING, WEST)
            }
            3 -> {
                when {
                    !validSides.contains(NORTH) -> return state.withProperty(FACING, NORTH).withProperty(MB_PART, MultiblockPart.EDGE)
                    !validSides.contains(EAST) -> return state.withProperty(FACING, EAST).withProperty(MB_PART, MultiblockPart.EDGE)
                    !validSides.contains(SOUTH) -> return state.withProperty(FACING, SOUTH).withProperty(MB_PART, MultiblockPart.EDGE)
                    !validSides.contains(WEST) -> return state.withProperty(FACING, WEST).withProperty(MB_PART, MultiblockPart.EDGE)
                }
            }
            4 -> return state.withProperty(MB_PART, MultiblockPart.CENTER)
        }

        return state
    }

    override val mod: Any = KaidenCraft.instance

    class TileEntityBoiler : TileEntityMultiblockPart() {
        override fun rebuild(pos: BlockPos): EntityMultiblock? = EntityBoilerMultiblock.rebuild(world, pos)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityBoiler()
    }
}
