package org.generousg.kaidencraft

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent


abstract class CommonProxy {
    open fun preInit(event: FMLPreInitializationEvent) = Unit
}