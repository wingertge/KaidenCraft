package org.generousg.kaidencraft;

import mcp.mobius.waila.api.IWailaRegistrar;
import org.generousg.fruitylib.blocks.FruityBlock;
import org.generousg.kaidencraft.integration.WailaIntegration;

public class Integration {
    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerHeadProvider(WailaIntegration.Companion.getInstance(), FruityBlock.class);
    }
}
