package org.generousg.kaidencraft

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler


class KaidenCraftGuiHandler : IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        return null
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        return null
    }
}