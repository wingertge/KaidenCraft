package org.generousg.kaidencraft.client.gui

import org.generousg.fruitylib.client.gui.SyncedGuiContainer
import org.generousg.fruitylib.container.ContainerBase
import org.generousg.fruitylib.sync.ISyncMapProvider


abstract class GuiMachineBase<out T : ContainerBase<out ISyncMapProvider>>(container: T, width: Int, height: Int, name: String) : SyncedGuiContainer<T>(container, width, height, name) {
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)


    }
}