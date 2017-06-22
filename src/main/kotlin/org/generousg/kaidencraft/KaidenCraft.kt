package org.generousg.kaidencraft

import Holders
import codechicken.lib.gui.SimpleCreativeTab
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.generousg.fruitylib.FruityLib
import org.generousg.fruitylib.config.ConfigProcessing
import org.generousg.fruitylib.config.FeatureHelper

@Mod(useMetadata = true, modid = "kaidencraft", acceptedMinecraftVersions = "[1.9,1.12)")
class KaidenCraft {
    companion object {
        val tabKaidenCraft = SimpleCreativeTab("kaidencraft", "minecraft:diamond_axe")
        @Mod.Instance("kaidencraft")
        var instance: KaidenCraft? = null
    }

    val featureHelper = object: FeatureHelper("kaidencraft", KaidenCraft.Companion::class) {
        override fun populateConfig(config: Configuration) = ConfigProcessing.processAnnotations("kaidencraft", config, Config::class.java)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        featureHelper.registerBlocksHolder(Holders.Blocks::class.java)
        featureHelper.registerItemsHolder(Holders.Items::class.java)
        featureHelper.preInit(event.suggestedConfigurationFile)

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, FruityLib.proxy?.wrapHandler(KaidenCraftGuiHandler()))
    }
}