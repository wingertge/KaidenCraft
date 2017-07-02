package org.generousg.kaidencraft.client.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11


class GuiBoiler(container: ContainerBoiler) : GuiMachineBase<ContainerBoiler>(container, 176, 175, "kaidencraft.gui.boiler") {
    private val COMPONENTS = ResourceLocation("kaidencraft", "textures/gui/components.png")

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(COMPONENTS)
        GL11.glPushMatrix()
        GL11.glTranslatef(xPos + 82f, yPos + 33f, 0f)
        this.drawTexturedModalRect(0, 0, 0, 0, 13, 13)

        if (getContainer().owner.isFurnaceBurning()) {
            GL11.glTranslatef(-1f, -1f, 0f)
            val k = getBurnLeftScaled(13)
            this.drawTexturedModalRect(0, 13-k, 13, 13-k, 14, k + 1)
        }
        GL11.glPopMatrix()
    }

    private fun getBurnLeftScaled(pixels: Int): Int {
        var i = getContainer().owner.currentItemBurnTime.value

        if (i == 0) {
            i = 200
        }

        return getContainer().owner.boilerBurnTime.value * pixels / i
    }
}