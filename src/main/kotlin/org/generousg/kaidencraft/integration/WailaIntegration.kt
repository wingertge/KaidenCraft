package org.generousg.kaidencraft.integration

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import mcp.mobius.waila.api.IWailaRegistrar
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.generousg.fruitylib.blocks.FruityBlock
import org.generousg.fruitylib.emptyItemStack
import org.generousg.fruitylib.multiblock.TileEntityMultiblockPart
import org.generousg.kaidencraft.blocks.BlockBoiler
import org.generousg.kaidencraft.blocks.BlockBoilerTank
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock


class WailaIntegration private constructor() : IWailaDataProvider {
    companion object {
        val instance by lazy { WailaIntegration() }

        @JvmStatic
        fun callbackRegister(registrar: IWailaRegistrar) {
            registrar.registerHeadProvider(WailaIntegration.instance, FruityBlock::class.java)
        }
    }

    override fun getWailaTail(itemStack: ItemStack?, currenttip: MutableList<String>, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String> {
        return currenttip
    }

    override fun getNBTData(player: EntityPlayerMP?, te: TileEntity?, tag: NBTTagCompound, world: World?, pos: BlockPos?): NBTTagCompound {
        return tag
    }

    override fun getWailaHead(itemStack: ItemStack, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?): MutableList<String> {
        if(accessor.tileEntity is BlockBoiler.TileEntityBoiler || accessor.tileEntity is BlockBoilerTank.TileEntityBoilerTank) {
            val entity = (accessor.tileEntity as TileEntityMultiblockPart).multiblockEntity as? EntityBoilerMultiblock
            if(entity != null) {
                if (entity.isFurnaceBurning()) currenttip.add("Burn time: ${entity.boilerBurnTime / entity.boilerBlocks.size / 20}s")
                else currenttip.add("Not burning")
            }
        }
        return currenttip
    }

    override fun getWailaBody(itemStack: ItemStack?, currenttip: MutableList<String>, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String> {
        return currenttip
    }

    override fun getWailaStack(accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): ItemStack {
        return emptyItemStack
    }
}
