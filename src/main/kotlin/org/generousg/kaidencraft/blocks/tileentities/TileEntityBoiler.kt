package org.generousg.kaidencraft.blocks.tileentities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.items.IItemHandler
import org.generousg.fruitylib.client.gui.IHasGui
import org.generousg.fruitylib.emptyItemStack
import org.generousg.fruitylib.inventory.GenericInventory
import org.generousg.fruitylib.inventory.IInventoryProvider
import org.generousg.fruitylib.isNullOrEmpty
import org.generousg.fruitylib.sync.SyncableInt
import org.generousg.fruitylib.tileentity.SyncedTileEntity
import org.generousg.kaidencraft.client.gui.ContainerBoiler
import org.generousg.kaidencraft.client.gui.GuiBoiler


class TileEntityBoiler : SyncedTileEntity(), IHasGui, IInventoryProvider, IItemHandler, ITickable {
    lateinit var boilerBurnTime: SyncableInt
    lateinit var currentItemBurnTime: SyncableInt

    override fun update() {
        if(isBurning())
            boilerBurnTime.value--

        if(!world.isRemote) {
            if(!isBurning() && !inventory.isEmpty) {
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
        boilerBurnTime = SyncableInt()
        currentItemBurnTime = SyncableInt()
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
    fun  isBurning(): Boolean = boilerBurnTime.value > 0
}