package org.generousg.kaidencraft.blocks

import net.minecraft.block.material.Material
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.kaidencraft.KaidenCraft


class BlockSmallTurbine : FruityBlock(Material.IRON) {
    override val mod: Any
        get() = KaidenCraft.instance!!
}