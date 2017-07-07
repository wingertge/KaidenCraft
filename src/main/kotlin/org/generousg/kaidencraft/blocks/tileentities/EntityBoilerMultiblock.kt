package org.generousg.kaidencraft.blocks.tileentities

import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import org.generousg.fruitylib.client.gui.IHasGui
import org.generousg.fruitylib.inventory.NamedItemHandler
import org.generousg.fruitylib.isNullOrEmpty
import org.generousg.fruitylib.join
import org.generousg.fruitylib.liquids.ProxyTank
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.fruitylib.subtract
import org.generousg.fruitylib.sync.SyncableCoordList
import org.generousg.fruitylib.sync.SyncableInt
import org.generousg.fruitylib.sync.SyncableUUID
import org.generousg.fruitylib.util.Log
import org.generousg.kaidencraft.blocks.BlockBoiler
import org.generousg.kaidencraft.blocks.BlockBoilerTank
import org.generousg.kaidencraft.client.gui.ContainerBoiler
import org.generousg.kaidencraft.client.gui.GuiBoiler


class EntityBoilerMultiblock(world: World) : EntityMultiblock(world), IHasGui {
    companion object {
        fun rebuild(world: World, pos: BlockPos): EntityBoilerMultiblock? {
            Log.debug("Starting org.generousg.fruitylib.multiblock startRebuild")

            var currentPos = pos
            while(world.getBlockState(currentPos).block is BlockBoilerTank) currentPos = currentPos.subtract(0, 1, 0)

            while (world.getBlockState(currentPos.subtract(1, 0, 0)).block is BlockBoiler) currentPos = currentPos.subtract(1, 0, 0)
            while(world.getBlockState(currentPos.subtract(0, 0, 1)).block is BlockBoiler) currentPos = currentPos.subtract(0, 0, 1)

            val boilerBlocks = arrayListOf<BlockPos>()
            val tankBlocks = arrayListOf<BlockPos>()

            var minXSize = 32
            var minYSize = 31
            var minZSize = 32
            var currentX = 0
            while (world.getBlockState(currentPos.add(currentX, 0, 0)).block is BlockBoiler) {
                var currentZ = 0
                while(world.getBlockState(currentPos.add(currentX, 0, currentZ)).block is BlockBoiler) {
                    var currentY = 0
                    while(world.getBlockState(currentPos.add(currentX, currentY + 1, currentZ)).block is BlockBoilerTank)
                        currentY++
                    currentZ++
                    minYSize = Math.min(minYSize, currentY)
                }
                minZSize = Math.min(minZSize, currentZ)
                currentX++
            }
            minXSize = Math.min(minXSize, currentX)

            if(minXSize > 0 && minYSize > 0 && minZSize > 0) {
                for(x in 0..minXSize-1) {
                    for(z in 0..minZSize-1) {
                        boilerBlocks.add(currentPos.add(x, 0, z))
                        (1..minYSize).mapTo(tankBlocks) { currentPos.add(x, it, z) }
                    }
                }
            } else return null

            val entity = EntityBoilerMultiblock(world)
            entity.setPosition(currentPos.x.toDouble(), currentPos.y.toDouble(), currentPos.z.toDouble())
            boilerBlocks.join(tankBlocks).forEach {
                val te = world.getTileEntity(it)
                if(te is TileEntityMultiblockPart) {
                    te.multiblockEntity?.destroy(it)
                    te.multiblockId.value = entity.persistentID
                }
            }

            entity.boilerBlocks.addAll(boilerBlocks)
            entity.tankBlocks.addAll(tankBlocks)
            Log.debug("Found multiblock, contains ${boilerBlocks.size} boiler/tank units")
            return entity
        }
    }

    lateinit var boilerBurnTime: SyncableInt
    lateinit var currentItemBurnTime: SyncableInt
    lateinit var boilerBlocks: SyncableCoordList
    lateinit var tankBlocks: SyncableCoordList
    val waterTank = ProxyTank()
    val steamTank = ProxyTank()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if(capability == ITEM_HANDLER_CAPABILITY || capability == FLUID_HANDLER_CAPABILITY) true else super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        when(capability) {
            ITEM_HANDLER_CAPABILITY -> return ITEM_HANDLER_CAPABILITY.cast(itemHandler)
            FLUID_HANDLER_CAPABILITY -> if(facing == EnumFacing.UP) return FLUID_HANDLER_CAPABILITY.cast(steamTank) else return FLUID_HANDLER_CAPABILITY.cast(waterTank)
            else -> return super.getCapability(capability, facing)
        }
    }

    override fun destroy(pos: BlockPos) {
        if(world.isRemote) return
        boilerBlocks.join(tankBlocks).forEach {
            val te = world.getTileEntity(it)
            if(te is TileEntityMultiblockPart) {
                te.multiblockId.value = SyncableUUID.IDENTITY
                te.sync()
            }
        }
        boilerBlocks.clear()
        tankBlocks.clear()
        if(!itemHandler.isEmpty()) {
            itemHandler.stacks.filterNot { it.isNullOrEmpty() }
                    .map { EntityItem(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, it) }
                    .forEach { world.spawnEntity(it) }
            itemHandler.clear()
        }
        setDead()
    }

    override fun onEntityUpdate() {
        if(!world.isRemote) {
            var boilersLeft = boilerBlocks.size
            while(boilersLeft > 0 && isFurnaceBurning()) {
                if(boilerBurnTime.value >= boilersLeft){
                    while (boilersLeft > 0) {
                        if(waterTank.drain(10, false)?.amount ?: 0 == 10 && steamTank.fill(FluidRegistry.getFluidStack("steam", 20), false) == 20) {
                            boilerBurnTime.value--
                            waterTank.drain(10, true)
                            steamTank.fill(FluidRegistry.getFluidStack("steam", 20), true)
                            boilersLeft--
                        } else return
                    }
                } else {
                    for(i in 0..boilersLeft - 1) {
                        if(waterTank.drain(10, false)?.amount ?: 0 == 10 && steamTank.fill(FluidRegistry.getFluidStack("steam", 20), false) == 20) {
                            boilerBurnTime.value--
                            waterTank.drain(10, true)
                            steamTank.fill(FluidRegistry.getFluidStack("steam", 20), true)
                            boilersLeft--
                        } else return
                    }
                }
                tryRefuel()
            }
            if(!waterTank.isEmpty() && !steamTank.isFull()) tryRefuel()
        }
    }

    private fun tryRefuel() {
        if (!isFurnaceBurning() && !itemHandler.isEmpty()) {
            val item = itemHandler.getStackInSlot(0)
            val singleItem = item.copy()
            singleItem.stackSize = 1
            val fuelValue = GameRegistry.getFuelValue(singleItem)
            boilerBurnTime.value = fuelValue
            currentItemBurnTime.value = fuelValue

            item.stackSize--
            if (item.isNullOrEmpty()) itemHandler.setStackInSlot(0, item.item.getContainerItem(item))
            sync()
        }
    }

    override fun createSyncedFields() {
        syncMap.sentSyncEvent += {
            waterTank.setMembers(tankBlocks.map { (world.getTileEntity(it) as? BlockBoilerTank.TileEntityBoilerTank)?.waterTank }.filterNotNull())
            steamTank.setMembers(tankBlocks.map { (world.getTileEntity(it) as? BlockBoilerTank.TileEntityBoilerTank)?.steamTank }.filterNotNull())
        }

        syncMap.receivedSyncEvent += {
            if(it.changes.contains(tankBlocks)) {
                waterTank.setMembers(tankBlocks.map { (world.getTileEntity(it) as? BlockBoilerTank.TileEntityBoilerTank)?.waterTank }.filterNotNull())
                steamTank.setMembers(tankBlocks.map { (world.getTileEntity(it) as? BlockBoilerTank.TileEntityBoilerTank)?.steamTank }.filterNotNull())
            }
        }

        boilerBurnTime = SyncableInt()
        currentItemBurnTime = SyncableInt()
        boilerBlocks = SyncableCoordList()
        tankBlocks = SyncableCoordList()
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
        compound.setTag("itemHandler", itemHandler.serializeNBT())
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
        itemHandler.deserializeNBT(compound.getCompoundTag("itemHandler"))
    }

    val itemHandler: NamedItemHandler = object: NamedItemHandler("boiler", 1) {
        override fun isItemValidForSlot(slot: Int, item: ItemStack): Boolean = GameRegistry.getFuelValue(item) > 0
    }

    override fun getServerGui(player: EntityPlayer): Any {
        return ContainerBoiler(player.inventory, itemHandler, this)
    }

    override fun getClientGui(player: EntityPlayer): Any {
        return GuiBoiler(ContainerBoiler(player.inventory, itemHandler, this))
    }

    override fun canOpenGui(player: EntityPlayer): Boolean = true
    fun isFurnaceBurning(): Boolean = boilerBurnTime.value > 0
}