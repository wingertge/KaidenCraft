package org.generousg.kaidencraft.client.gui

import net.minecraft.util.ResourceLocation
import org.generousg.fruitylib.client.gui.SyncedGuiContainer
import org.generousg.fruitylib.container.ContainerBase
import org.generousg.fruitylib.sync.ISyncMapProvider


abstract class GuiMachineBase<out T : ContainerBase<ISyncMapProvider>>(container: T, width: Int, height: Int, name: String) : SyncedGuiContainer<T>(container, width, height, name) {
    companion object {
        val COMPONENTS = ResourceLocation("kaidencraft", "textures/gui/components.png")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
    }
}