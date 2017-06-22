package org.generousg.kaidencraft.blocks.tileentities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import org.generousg.fruitylib.client.gui.IHasGui
import org.generousg.fruitylib.inventory.GenericInventory
import org.generousg.fruitylib.inventory.IInventoryProvider
import org.generousg.fruitylib.tileentity.SyncedTileEntity
import org.generousg.kaidencraft.client.gui.ContainerBoiler
import org.generousg.kaidencraft.client.gui.GuiBoiler


class TileEntityBoiler : SyncedTileEntity(), IHasGui, IInventoryProvider {


    override fun createSyncedFields() {

    }

    override val inventory: IInventory = GenericInventory("boiler", false, 1)

    override fun getServerGui(player: EntityPlayer): Any {
        return ContainerBoiler(player.inventory, this)
    }

    override fun getClientGui(player: EntityPlayer): Any {
        return GuiBoiler(ContainerBoiler(player.inventory, this))
    }

    override fun canOpenGui(player: EntityPlayer): Boolean = true
}