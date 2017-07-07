package org.generousg.kaidencraft.client.gui

import net.minecraft.client.renderer.GlStateManager
import org.generousg.fruitylib.client.gui.components.GuiComponentTankLevel
import org.lwjgl.opengl.GL11


class GuiBoiler(container: ContainerBoiler) : GuiMachineBase<ContainerBoiler>(container, 176, 175, "kaidencraft.gui.boiler") {
    private val waterTank = GuiComponentTankLevel(20, 20, 22, 57, container.owner.waterTank.maxAmount, "water")
    private val steamTank = GuiComponentTankLevel(134, 20, 22, 57, container.owner.waterTank.maxAmount, "steam")

    init {
        waterTank.fluidStack = container.owner.waterTank.contents
        steamTank.fluidStack = container.owner.steamTank.contents
        container.owner.waterTank.capacityChangedEvent += waterTank.capacityReceiver
        container.owner.waterTank.fluidChangedEvent += waterTank.fluidReceiver
        container.owner.steamTank.capacityChangedEvent += steamTank.capacityReceiver
        container.owner.steamTank.fluidChangedEvent += steamTank.fluidReceiver
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        container.owner.waterTank.capacityChangedEvent -= waterTank.capacityReceiver
        container.owner.waterTank.fluidChangedEvent -= waterTank.fluidReceiver
        container.owner.steamTank.capacityChangedEvent -= steamTank.capacityReceiver
        container.owner.steamTank.fluidChangedEvent -= steamTank.fluidReceiver
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(COMPONENTS)

        root.addComponent(waterTank)
        root.addComponent(steamTank)

        drawFlames()
    }

    private fun drawFlames() {
        GL11.glPushMatrix()
        GL11.glTranslatef(xPos + 82f, yPos + 33f, 0f)
        this.drawTexturedModalRect(0, 0, 0, 0, 13, 13)

        if (container.owner.isFurnaceBurning()) {
            GL11.glTranslatef(-1f, -1f, 0f)
            val k = getBurnLeftScaled(13)
            this.drawTexturedModalRect(0, 13-k, 13, 13-k, 14, k + 1)
        }
        GL11.glPopMatrix()
    }

    private fun getBurnLeftScaled(pixels: Int): Int {
        var i = container.owner.currentItemBurnTime.value

        if (i == 0) {
            i = 200
        }

        return container.owner.boilerBurnTime.value * pixels / i
    }
}