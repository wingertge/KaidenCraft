package org.generousg.kaidencraft.blocks.tileentities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.items.IItemHandler
import org.generousg.fruitylib.client.gui.IHasGui
import org.generousg.fruitylib.emptyItemStack
import org.generousg.fruitylib.inventory.IInventoryProvider
import org.generousg.fruitylib.inventory.InventorySerializable
import org.generousg.fruitylib.isNullOrEmpty
import org.generousg.fruitylib.join
import org.generousg.fruitylib.multiblock.EntityMultiblock
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.fruitylib.subtract
import org.generousg.fruitylib.sync.SyncableCoordList
import org.generousg.fruitylib.sync.SyncableInt
import org.generousg.fruitylib.sync.SyncableTank
import org.generousg.fruitylib.util.Log
import org.generousg.kaidencraft.KaidenCraft
import org.generousg.kaidencraft.blocks.BlockBoiler
import org.generousg.kaidencraft.blocks.BlockBoilerTank
import org.generousg.kaidencraft.client.gui.ContainerBoiler
import org.generousg.kaidencraft.client.gui.GuiBoiler


class EntityBoilerMultiblock(world: World) : EntityMultiblock(world), IHasGui, IInventoryProvider, IItemHandler {
    companion object {
        init {
            KaidenCraft.preInitEvent += { EntityRegistry.registerModEntity(ResourceLocation(KaidenCraft.MOD_ID, "multiblock_boiler"),
                    EntityBoilerMultiblock::class.java, "multiblock_boiler", 0, KaidenCraft.instance, 32, 20, false) }
        }

        fun rebuild(world: World, pos: BlockPos): EntityBoilerMultiblock? {
            Log.debug("Starting org.generousg.fruitylib.multiblock startRebuild")

            var currentPos = pos
            while(world.getBlockState(currentPos).block is BlockBoilerTank) currentPos = currentPos.subtract(0, 1, 0)

            while (world.getBlockState(currentPos.subtract(1, 0, 0)).block is BlockBoiler) currentPos = currentPos.subtract(1, 0, 0)
            while(world.getBlockState(currentPos.subtract(0, 0, 1)).block is BlockBoiler) currentPos = currentPos.subtract(0, 0, 1)

            val boilerBlocks = arrayListOf<BlockPos>()
            val tankBlocks = arrayListOf<BlockPos>()

            if(world.getBlockState(currentPos.add(0, 1,0)).block is BlockBoilerTank) {
                boilerBlocks.add(currentPos)
                tankBlocks.add(currentPos.add(0, 1, 0))
            } else return null

            while(world.getBlockState(currentPos.add(1, 0, 0)).block is BlockBoiler) {
                if(boilerBlocks.size >= 32) break
                if(world.getBlockState(currentPos.add(1, 1, 0)).block is BlockBoilerTank) {
                    currentPos = currentPos.add(1, 0, 0)
                    boilerBlocks.add(currentPos)
                    tankBlocks.add(currentPos.add(0, 1, 0))
                    var tempPos = currentPos.add(0, 2, 0)
                    while(world.getBlockState(tempPos).block is BlockBoilerTank) {
                        if(tempPos.subtract(currentPos).y >= 32) break
                        tempPos = tempPos.add(0, 1, 0)
                        tankBlocks.add(tempPos)
                    }
                } else break
            }

            var z = 31
            var minYSize = 30
            for(it in boilerBlocks) {
                var currentZ = 0
                var currentY = 0
                while (world.getBlockState(it.add(0, 0, currentZ + 1)).block is BlockBoiler) {
                    val teBoiler = world.getTileEntity(it.add(0, 0, currentZ + 1))
                    if(teBoiler is TileEntityMultiblockPart && teBoiler.multiblockId.value != 0) (world.getEntityByID(teBoiler.multiblockId.value) as? EntityMultiblock)?.destroy()
                    if(world.getBlockState(it.add(0, 1, currentZ + 1)).block is BlockBoilerTank) {
                        val teTank = world.getTileEntity(it.add(0, 1, currentZ + 1))
                        if(teTank is TileEntityMultiblockPart && teTank.multiblockId.value != 0) (world.getEntityByID(teTank.multiblockId.value) as? EntityMultiblock)?.destroy()
                        while (world.getBlockState(it.add(0, currentY + 2, currentZ + 1)).block is BlockBoilerTank) currentY++
                        currentZ++
                    } else break
                }
                z = Math.min(z, currentZ)
                minYSize = Math.min(minYSize, currentY)
            }

            val boilerBlocksTemp = arrayListOf<BlockPos>()
            boilerBlocksTemp.addAll(boilerBlocks)
            if(z > 0) for (a in 1..z) {
                for (it in boilerBlocksTemp) {
                    boilerBlocks.add(it.add(0, 0, a))
                    tankBlocks.add(it.add(0, 1, a))
                    if(minYSize > 0) (1..minYSize).mapTo(tankBlocks) { y -> it.add(0, y + 1, z) }
                }
            }

            val entity = EntityBoilerMultiblock(world)
            entity.setPosition(currentPos.x.toDouble(), currentPos.y.toDouble(), currentPos.z.toDouble())
            boilerBlocks.join(tankBlocks).forEach {
                val te = world.getTileEntity(it)
                if(te is TileEntityMultiblockPart) {
                    if(te.multiblockId.value != 0) getMultiblockEnt(world, te)?.destroy()
                    te.multiblockId.value = entity.entityId
                }
            }

            entity.boilerBlocks.addAll(boilerBlocks)
            entity.tankBlocks.addAll(tankBlocks)
            Log.debug("Found multiblock, contains ${boilerBlocks.size} boiler/tank units")
            return entity
        }

        private fun getMultiblockEnt(world: World, te: TileEntityMultiblockPart): EntityMultiblock? {
            return (world.getEntityByID(te.multiblockId.value)) as? EntityMultiblock
        }
    }

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

    override fun onEntityUpdate() {
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
        syncMap.sentSyncEvent += {
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
        tag.setTag("inventory", inventory.serializeNBT())
        return tag
    }

    override fun readFromNBT(tag: NBTTagCompound) {
        super.readFromNBT(tag)
        inventory.deserializeNBT(tag.getCompoundTag("inventory"))
    }

    override val inventory: InventorySerializable = object: InventorySerializable("boiler", false, 1) {
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
}