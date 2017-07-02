package org.generousg.kaidencraft.blocks.tileentities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.items.IItemHandler
import org.generousg.fruitylib.client.gui.IHasGui
import org.generousg.fruitylib.emptyItemStack
import org.generousg.fruitylib.inventory.GenericInventory
import org.generousg.fruitylib.inventory.IInventoryProvider
import org.generousg.fruitylib.isNullOrEmpty
import org.generousg.fruitylib.join
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.fruitylib.subtract
import org.generousg.fruitylib.sync.SyncableCoordList
import org.generousg.fruitylib.sync.SyncableInt
import org.generousg.fruitylib.sync.SyncableTank
import org.generousg.fruitylib.util.Log
import org.generousg.kaidencraft.blocks.BlockBoiler
import org.generousg.kaidencraft.blocks.BlockBoilerTank
import org.generousg.kaidencraft.client.gui.ContainerBoiler
import org.generousg.kaidencraft.client.gui.GuiBoiler


class EntityBoilerMultiblock(world: World) : EntityMultiblock(world), IHasGui, IInventoryProvider, IItemHandler, ITickable {
    override fun destroy() {
        boilerBlocks.join(tankBlocks).forEach {
            val te = world.getTileEntity(it)
            if(te is TileEntityMultiblockPart) {
                te.multiblockId.value = 0
                te.sync()
            }
        }
        boilerBlocks.clear()
        tankBlocks.clear()
        world.removeEntity(this)
    }

    lateinit var boilerBurnTime: SyncableInt
    lateinit var currentItemBurnTime: SyncableInt
    lateinit var waterTank: SyncableTank
    lateinit var steamTank: SyncableTank
    lateinit var boilerBlocks: SyncableCoordList
    lateinit var tankBlocks: SyncableCoordList

    override fun update() {
        if(isFurnaceBurning())
            boilerBurnTime.value--

        if(!world.isRemote) {
            if(!isFurnaceBurning() && !inventory.isEmpty) {
                val item = inventory.getStackInSlot(0)
                val singleItem = item.copy()
                singleItem.stackSize = 1
                val fuelValue = GameRegistry.getFuelValue(singleItem)
                boilerBurnTime.value = fuelValue
                currentItemBurnTime.value = fuelValue

                item.stackSize--
                if(item.isNullOrEmpty()) inventory.setInventorySlotContents(0, item.item.getContainerItem(item))
                markDirty()
                sync()
            }
        }
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if(stack.isNullOrEmpty() || !inventory.isItemValidForSlot(slot, stack)) return stack
        val currentStack = inventory.getStackInSlot(slot)
        if(currentStack.stackSize >= currentStack.maxStackSize) return stack
        val newStack = stack.copy()
        newStack.stackSize -= currentStack.maxStackSize - currentStack.stackSize

        if(!simulate) {
            if(newStack.stackSize == 0) {
                currentStack.stackSize += stack.stackSize
                inventory.setInventorySlotContents(slot, currentStack)
                return emptyItemStack
            } else {
                currentStack.stackSize = currentStack.maxStackSize
                inventory.setInventorySlotContents(slot, currentStack)
                return newStack
            }
        }
        return newStack
    }

    override fun getStackInSlot(slot: Int): ItemStack = inventory.getStackInSlot(slot)
    override fun getSlotLimit(slot: Int): Int = inventory.sizeInventory
    override fun getSlots(): Int = 1
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return emptyItemStack
    }

    override fun createSyncedFields() {
        syncMap.syncEvent += {
            if(it.changes.contains(waterTank)) waterTank.capacity = 4000 * tankBlocks.size
            if(it.changes.contains(steamTank)) steamTank.capacity = 4000 * tankBlocks.size
        }

        boilerBurnTime = SyncableInt()
        currentItemBurnTime = SyncableInt()
        boilerBlocks = SyncableCoordList()
        tankBlocks = SyncableCoordList()
        waterTank = SyncableTank(4000)
        steamTank = SyncableTank(4000)
    }

    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(tag)
        inventory.writeToNBT(tag)
        return tag
    }

    override fun readFromNBT(tag: NBTTagCompound) {
        super.readFromNBT(tag)
        inventory.readFromNBT(tag)
    }

    override val inventory: GenericInventory = object: GenericInventory("boiler", false, 1) {
        override fun isItemValidForSlot(i: Int, stack: ItemStack): Boolean = GameRegistry.getFuelValue(stack) > 0
    }

    override fun getServerGui(player: EntityPlayer): Any {
        return ContainerBoiler(player.inventory, this)
    }

    override fun getClientGui(player: EntityPlayer): Any {
        return GuiBoiler(ContainerBoiler(player.inventory, this))
    }

    override fun canOpenGui(player: EntityPlayer): Boolean = true
    fun isFurnaceBurning(): Boolean = boilerBurnTime.value > 0

    private fun getMultiblockEnt(te: TileEntityMultiblockPart): EntityMultiblock? {
        return (world.getEntityByID(te.multiblockId.value)) as? EntityMultiblock
    }

    override fun rebuild(pos: BlockPos): Boolean {
        Log.debug("Starting org.generousg.fruitylib.multiblock rebuild")
        setPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        this.boilerBlocks.clear()
        this.tankBlocks.clear()

        var currentPos = pos
        if(world.getBlockState(currentPos).block is BlockBoilerTank) currentPos = currentPos.subtract(0, 1, 0)

        while (world.getBlockState(currentPos.subtract(1, 0, 0)).block is BlockBoiler) currentPos = currentPos.subtract(1, 0, 0)
        while(world.getBlockState(currentPos.subtract(0, 0, 1)).block is BlockBoiler) currentPos = currentPos.subtract(0, 0, 1)

        val boilerBlocks = arrayListOf<BlockPos>()
        val tankBlocks = arrayListOf<BlockPos>()

        if(world.getBlockState(currentPos.add(0, 1,0)).block is BlockBoilerTank) {
            boilerBlocks.add(currentPos)
            tankBlocks.add(currentPos.add(0, 1, 0))
        } else return false

        while(world.getBlockState(currentPos.add(1, 0, 0)).block is BlockBoiler) {
            if(world.getBlockState(currentPos.add(1, 1, 0)).block is BlockBoilerTank) {
                currentPos = currentPos.add(1, 0, 0)
                boilerBlocks.add(currentPos)
                tankBlocks.add(currentPos.add(0, 1, 0))
            } else break
        }

        var minYSize = Int.MAX_VALUE
        boilerBlocks.forEach {
            var current = 0
            while (world.getBlockState(it.add(0, 0, 1)).block is BlockBoiler) {
                if(world.getBlockState(it.add(0, 1, 1)).block is BlockBoilerTank) {
                    current++
                } else break
            }
            minYSize = Math.min(minYSize, current)
        }

        val boilerBlocksTemp = arrayListOf<BlockPos>()
        boilerBlocksTemp.addAll(boilerBlocks)
        if(minYSize > 0) (1..minYSize).forEach { a ->
            boilerBlocksTemp.forEach {
                boilerBlocks.add(it.add(0, 0, a))
                tankBlocks.add(it.add(0, 1, a))
            }
        }

        boilerBlocks.join(tankBlocks).forEach {
            val te = world.getTileEntity(it)
            if(te is TileEntityMultiblockPart) {
                if(te.multiblockId.value != 0) getMultiblockEnt(te)?.destroy()
                te.multiblockId.value = entityId
            }
        }

        this.boilerBlocks.addAll(boilerBlocks)
        this.tankBlocks.addAll(tankBlocks)
        Log.debug("Found multiblock, contains ${boilerBlocks.size} boiler/tank units")
        return true
    }
}