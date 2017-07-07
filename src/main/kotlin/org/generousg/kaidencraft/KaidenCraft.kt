package org.generousg.kaidencraft

import codechicken.lib.gui.SimpleCreativeTab
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.EntityRegistry
import org.generousg.fruitylib.FruityLib
import org.generousg.fruitylib.config.ConfigProcessing
import org.generousg.fruitylib.config.FeatureHelper
import org.generousg.fruitylib.util.events.Event
import org.generousg.kaidencraft.KaidenCraft.Companion.MOD_ID
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock
import org.generousg.kaidencraft.util.Log

@Mod(useMetadata = true, modid = MOD_ID, acceptedMinecraftVersions = "[1.9,1.12)")
class KaidenCraft {
    companion object {
        const val MOD_ID = "kaidencraft"
        @Suppress("unused")
        val tabKaidenCraft = SimpleCreativeTab("kaidencraft", "minecraft:diamond_axe")
        @Mod.Instance(MOD_ID)
        lateinit var instance: KaidenCraft
        @SidedProxy(clientSide = "org.generousg.kaidencraft.ClientProxy", serverSide = "org.generousg.kaidencraft.ServerProxy")
        lateinit var proxy: CommonProxy

        val preInitEvent = Event<FMLPreInitializationEvent>()
    }

    val featureHelper = object: FeatureHelper("kaidencraft", KaidenCraft.Companion::class) {
        override fun populateConfig(config: Configuration) = ConfigProcessing.processAnnotations("kaidencraft", config, Config::class.java)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        FruityLib.DEBUG_MODE = true
        Log.setLogger(event.modLog)
        preInitEvent.fire(event)
        registerEntities()
        featureHelper.registerBlocksHolder(Holders.Blocks::class.java)
        featureHelper.registerItemsHolder(Holders.Items::class.java)
        featureHelper.registerFluidsHolder(Holders.Fluids::class.java)
        featureHelper.preInit(event.suggestedConfigurationFile)

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, FruityLib.proxy?.wrapHandler(KaidenCraftGuiHandler()))

        proxy.preInit(event)
    }

    private fun registerEntities() {
        EntityRegistry.registerModEntity(ResourceLocation(MOD_ID, "multiblock_boiler"),
                EntityBoilerMultiblock::class.java, "multiblock_boiler", Holders.EntityIds.MULTIBLOCK_BOILER, KaidenCraft.instance, 32, 20, false)
    }
}