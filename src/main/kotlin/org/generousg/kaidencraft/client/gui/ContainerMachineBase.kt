package org.generousg.kaidencraft.client.gui

import net.minecraft.inventory.IInventory
import org.generousg.fruitylib.client.gui.ContainerInventoryProvider
import org.generousg.fruitylib.inventory.IInventoryProvider


open class ContainerMachineBase<out T: IInventoryProvider>(playerInventory: IInventory, owner: T) : ContainerInventoryProvider<T>(playerInventory, owner) {
    init {
        addPlayerInventorySlots(93)
    }
}