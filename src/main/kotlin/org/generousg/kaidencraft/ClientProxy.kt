package org.generousg.kaidencraft

import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent


class ClientProxy : CommonProxy() {
    override fun preInit(event: FMLPreInitializationEvent) {
        OBJLoader.INSTANCE.addDomain(KaidenCraft.MOD_ID)
    }
}