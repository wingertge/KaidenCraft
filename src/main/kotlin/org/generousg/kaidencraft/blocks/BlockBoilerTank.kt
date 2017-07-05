package org.generousg.kaidencraft.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.generousg.fruitylib.blocks.BlockMultiblockPart
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.fruitylib.getBlockInDirection
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.MultiblockPart
import org.generousg.fruitylib.multiblock.MultiblockPart.*
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.fruitylib.util.bitmap.EnumBitMap
import org.generousg.kaidencraft.KaidenCraft
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock
import kotlin.reflect.KClass


class BlockBoilerTank : BlockMultiblockPart(Material.IRON), ITileEntityProvider {
    companion object {
        private val BOUNDING_BOX = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        private val COLLISION_BOX = AxisAlignedBB(0.025, 0.025, 0.025, 0.975, 0.975, 0.975)
    }

    override fun getValidComponents(): List<KClass<out FruityBlock>> = arrayListOf(BlockBoiler::class, BlockBoilerTank::class)

    override fun getMultiblockPart(world: IBlockAccess, pos: BlockPos, state: IBlockState): IBlockState {
        val neighbors = hashMapOf<EnumFacing, Block>()
        val thisTE = world.getTileEntity(pos) as TileEntityMultiblockPart
        if(thisTE.multiblockId.value == 0) return state.withProperty(MB_PART, MultiblockPart.SINGLE).withProperty(FACING, NORTH)
        EnumFacing.values().forEach { neighbors.put(it, world.getBlockInDirection(it, pos).block) }
        val validSidesEnums = neighbors.filter { (_, value) -> value is BlockBoilerTank /*&& (world.getTileEntity(pos.inDirection(key)) as TileEntityMultiblockPart).multiblockId.value == thisTE.multiblockId.value*/ }.keys
        val validSides = EnumBitMap(validSidesEnums)

        when(validSidesEnums.size) {
            0 -> return state.withProperty(MB_PART, MultiblockPart.SINGLE).withProperty(FACING, NORTH)
            1 -> {
                if(validSides.contains(UP)) return state.withProperty(MB_PART, BOTTOM_PILLAR).withProperty(FACING, NORTH)
                else if(validSides.contains(DOWN)) return state.withProperty(MB_PART, TOP_PILLAR).withProperty(FACING, NORTH)
                else state.withProperty(MB_PART, END).withProperty(FACING, validSidesEnums.elementAt(0))
            }
            2 -> {
                if(validSides.contains(UP)) {
                    if(validSides.contains(DOWN)) return state.withProperty(MB_PART, CENTER_PILLAR)
                    else return state.withProperty(MB_PART, BOTTOM_END).withProperty(FACING, validSidesEnums.first { it != UP })
                } else if(validSides.contains(DOWN)) return state.withProperty(MB_PART, TOP_END).withProperty(FACING, validSidesEnums.first { it != DOWN })
                else if(validSides.contains(NORTH)) {
                    if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, LINE_EDGE).withProperty(FACING, validSidesEnums.elementAt(0))
                    else if(validSides.contains(WEST)) return state.withProperty(MB_PART, CORNER).withProperty(FACING, NORTH)
                    else return state.withProperty(MB_PART, CORNER).withProperty(FACING, EAST)
                } else if(validSides.contains(EAST)) {
                    if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, CORNER).withProperty(FACING, SOUTH)
                    else return state.withProperty(MB_PART, LINE_EDGE).withProperty(FACING, EAST)
                } else return state.withProperty(MB_PART, CORNER).withProperty(FACING, WEST)
            }
            3 -> {
                if(validSides.contains(UP)) {
                    if(validSides.contains(DOWN)) return state.withProperty(MB_PART, CENTER_END).withProperty(FACING, validSidesEnums.first { it != UP && it != DOWN })
                    else if(validSides.contains(NORTH)) {
                        if(validSides.contains(WEST)) return state.withProperty(MB_PART, BOTTOM_CORNER).withProperty(FACING, NORTH)
                        else if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, BOTTOM_LINE_EDGE).withProperty(FACING, NORTH)
                        else return state.withProperty(MB_PART, BOTTOM_CORNER).withProperty(FACING, EAST)
                    } else if(validSides.contains(EAST)) {
                        if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, BOTTOM_CORNER).withProperty(FACING, SOUTH)
                        else return state.withProperty(MB_PART, BOTTOM_LINE_EDGE).withProperty(FACING, EAST)
                    } else return state.withProperty(MB_PART, BOTTOM_CORNER).withProperty(FACING, WEST)
                } else if(validSides.contains(DOWN)) {
                    if(validSides.contains(NORTH)) {
                        if(validSides.contains(WEST)) return state.withProperty(MB_PART, TOP_CORNER).withProperty(FACING, NORTH)
                        else if(validSides.contains(EAST)) return state.withProperty(MB_PART, TOP_CORNER).withProperty(FACING, EAST)
                        else return state.withProperty(MB_PART, TOP_LINE_EDGE).withProperty(FACING, NORTH)
                    } else if(validSides.contains(EAST)) {
                        if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, TOP_CORNER).withProperty(FACING, SOUTH)
                        else return state.withProperty(MB_PART, TOP_LINE_EDGE).withProperty(FACING, EAST)
                    } else return state.withProperty(MB_PART, TOP_CORNER).withProperty(FACING, WEST)
                } else when {
                    !validSides.contains(SOUTH) -> return state.withProperty(MB_PART, EDGE).withProperty(FACING, NORTH)
                    !validSides.contains(NORTH) -> return state.withProperty(MB_PART, EDGE).withProperty(FACING, SOUTH)
                    !validSides.contains(EAST) -> return state.withProperty(MB_PART, EDGE).withProperty(FACING, WEST)
                    !validSides.contains(WEST) -> return state.withProperty(MB_PART, EDGE).withProperty(FACING, EAST)
                }
            }
            4 -> {
                if(validSides.contains(UP)) {
                    if(validSides.contains(DOWN)) {
                        if(validSides.contains(NORTH)) {
                            if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, CENTER_LINE_EDGE).withProperty(FACING, NORTH)
                            else if(validSides.contains(EAST)) return state.withProperty(MB_PART, CENTER_CORNER).withProperty(FACING, EAST)
                            else return state.withProperty(MB_PART, CENTER_CORNER).withProperty(FACING, NORTH)
                        } else if(validSides.contains(EAST)) {
                            if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, CENTER_CORNER).withProperty(FACING, SOUTH)
                            else return state.withProperty(MB_PART, CENTER_LINE_EDGE).withProperty(FACING, EAST)
                        } else return state.withProperty(MB_PART, CENTER_CORNER).withProperty(FACING, WEST)
                    } else if(validSides.contains(NORTH)) {
                        if(validSides.contains(EAST)) {
                            if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, BOTTOM_EDGE).withProperty(FACING, EAST)
                            else return state.withProperty(MB_PART, BOTTOM_EDGE).withProperty(FACING, NORTH)
                        } else return state.withProperty(MB_PART, BOTTOM_EDGE).withProperty(FACING, WEST)
                    } else return state.withProperty(MB_PART, BOTTOM_EDGE).withProperty(FACING, SOUTH)
                } else if(validSides.contains(DOWN)) {
                    if(validSides.contains(NORTH)) {
                        if(validSides.contains(EAST)) {
                            if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, TOP_EDGE).withProperty(FACING, EAST)
                            else return state.withProperty(MB_PART, TOP_EDGE).withProperty(FACING, NORTH)
                        } else return state.withProperty(MB_PART, TOP_EDGE).withProperty(FACING, WEST)
                    } else return state.withProperty(MB_PART, TOP_EDGE).withProperty(FACING, SOUTH)
                } else return state.withProperty(MB_PART, CENTER)
            }
            5 -> {
                if(validSides.contains(UP)) {
                    if(validSides.contains(DOWN)) {
                        if(validSides.contains(NORTH)) {
                            if(validSides.contains(EAST)) {
                                if(validSides.contains(SOUTH)) return state.withProperty(MB_PART, CENTER_EDGE).withProperty(FACING, EAST)
                                else return state.withProperty(MB_PART, CENTER_EDGE).withProperty(FACING, NORTH)
                            } else return state.withProperty(MB_PART, CENTER_EDGE).withProperty(FACING, WEST)
                        } else return state.withProperty(MB_PART, CENTER_EDGE).withProperty(FACING, SOUTH)
                    } else return state.withProperty(MB_PART, BOTTOM_CENTER)
                } else return state.withProperty(MB_PART, TOP_CENTER)
            }
            6 -> return state.withProperty(MB_PART, HIDDEN)
        }

        return state
    }

    class TileEntityBoilerTank : TileEntityMultiblockPart(BlockBoiler::class, BlockBoilerTank::class) {
        override fun rebuild(pos: BlockPos): EntityMultiblock? = EntityBoilerMultiblock.rebuild(world, pos)
    }

    override val mod: Any = KaidenCraft.instance
    override fun createNewTileEntity(worldIn: World?, meta: Int) = TileEntityBoilerTank()
    override fun isOpaqueCube(state: IBlockState?): Boolean = false
    override fun isFullCube(state: IBlockState?): Boolean = false
    override fun getCollisionBoundingBox(blockState: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?): AxisAlignedBB? {
        return super.getCollisionBoundingBox(blockState, worldIn, pos)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB = BOUNDING_BOX
    override fun addCollisionBoxToList(state: IBlockState?, worldIn: World?, pos: BlockPos?, entityBox: AxisAlignedBB?, collidingBoxes: MutableList<AxisAlignedBB>?, entityIn: Entity?, p_185477_7_: Boolean) = addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX)
}