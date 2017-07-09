package org.generousg.kaidencraft

import codechicken.lib.gui.SimpleCreativeTab
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.EntityRegistry
import org.generousg.fruitylib.FruityLib
import org.generousg.fruitylib.config.ConfigProcessing
import org.generousg.fruitylib.config.FeatureHelper
import org.generousg.fruitylib.integration.Integration
import org.generousg.fruitylib.integration.IntegrationUtil
import org.generousg.fruitylib.util.events.Event
import org.generousg.kaidencraft.KaidenCraft.Companion.DEPENDENCIES
import org.generousg.kaidencraft.KaidenCraft.Companion.MOD_ID
import org.generousg.kaidencraft.KaidenCraft.Companion.VERSION
import org.generousg.kaidencraft.blocks.tileentities.EntityBoilerMultiblock
import org.generousg.kaidencraft.util.Log

@Mod(useMetadata = true, modid = MOD_ID, acceptedMinecraftVersions = "[1.9,1.12)", version = VERSION, dependencies = DEPENDENCIES)
class KaidenCraft {
    companion object {
        const val MOD_ID = "kaidencraft"
        const val VERSION = "\$VERSION$"
        const val PROXY_CLIENT = "org.generousg.kaidencraft.ClientProxy"
        const val PROXY_SERVER = "org.generousg.kaidencraft.ServerProxy"
        const val DEPENDENCIES = "required-after:FruityLib"

        @Suppress("unused")
        @JvmStatic
        val tabKaidenCraft = SimpleCreativeTab("kaidencraft", "minecraft:diamond_axe")
        @Mod.Instance(MOD_ID)
        lateinit var instance: KaidenCraft
        @SidedProxy(clientSide = PROXY_CLIENT, serverSide = PROXY_SERVER)
        lateinit var proxy: CommonProxy

        val preInitEvent = Event<FMLPreInitializationEvent>()
    }

    val featureHelper = object: FeatureHelper(MOD_ID, KaidenCraft::class) {
        override fun populateConfig(config: Configuration) = ConfigProcessing.processAnnotations(MOD_ID, config, Holders.Config::class.java)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        FruityLib.DEBUG_MODE = true
        Log.setLogger(event.modLog)
        preInitEvent.fire(event)
        registerEntities()
        featureHelper.registerFluidsHolder(Holders.Fluids::class.java)
        featureHelper.registerBlocksHolder(Holders.Blocks::class.java)
        featureHelper.registerItemsHolder(Holders.Items::class.java)
        featureHelper.preInit(event.suggestedConfigurationFile)

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, FruityLib.proxy.wrapHandler(KaidenCraftGuiHandler()))

        proxy.preInit(event)
        Integration.addModule(IntegrationUtil.createSimpleModule("kc_waila", {
            FMLInterModComms.sendMessage("waila", "register", "org.generousg.kaidencraft.integration.WailaIntegration.callbackRegister")
        }))
    }

    private fun registerEntities() {
        EntityRegistry.registerModEntity(ResourceLocation(MOD_ID, "multiblock_boiler"),
                EntityBoilerMultiblock::class.java, "multiblock_boiler", Holders.EntityIds.MULTIBLOCK_BOILER, KaidenCraft.instance, 32, 20, false)
    }

    @Mod.EventHandler
    fun serverStart(event: FMLServerStartingEvent) {
        event.registerServerCommand(object: CommandBase() {
            override fun getName(): String = "kaidencraft"

            override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
                if(args.isEmpty()) return
                when(args[0]) {
                    "version" -> sender.sendMessage(TextComponentString(VERSION))
                }
            }

            override fun getUsage(sender: ICommandSender?): String = "/kaidencraft <command> <args>"
            override fun getRequiredPermissionLevel(): Int = 0
        })
    }
}
