package org.generousg.kaidencraft.client.gui

import net.minecraft.inventory.IInventory
import org.generousg.kaidencraft.blocks.tileentities.TileEntityBoiler


class ContainerBoiler(playerInventory: IInventory, tile: TileEntityBoiler) : ContainerMachineBase<TileEntityBoiler>(playerInventory, tile) {
}