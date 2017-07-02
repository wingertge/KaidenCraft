package org.generousg.kaidencraft.client.gui

import net.minecraft.inventory.IInventory
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock


class ContainerBoiler(playerInventory: IInventory, tile: EntityBoilerMultiblock) : ContainerMachineBase<EntityBoilerMultiblock>(playerInventory, tile) {
    init {
        addSlotToContainer(RestrictedSlot(inventory, 0, 176/2 - 8, 50))
        addPlayerInventorySlots(93)
    }
}