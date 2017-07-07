package org.generousg.kaidencraft.client.gui

import net.minecraft.inventory.IInventory
import net.minecraftforge.items.IItemHandler
import org.generousg.fruitylib.container.ContainerBase


open class ContainerMachineBase<out T>(playerInventory: IInventory, itemHandler: IItemHandler, owner: T) : ContainerBase<T>(playerInventory, itemHandler, owner)