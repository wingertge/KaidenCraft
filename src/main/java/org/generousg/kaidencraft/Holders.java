package org.generousg.kaidencraft;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.generousg.fruitylib.config.*;
import org.generousg.kaidencraft.blocks.*;
import org.generousg.kaidencraft.blocks.BlockBoiler.TileEntityBoiler;
import org.generousg.kaidencraft.blocks.BlockBoilerTank.TileEntityBoilerTank;
import org.generousg.kaidencraft.items.ItemWrench;

public final class Holders {
   @SuppressWarnings("WeakerAccess")
   public static final class Blocks implements BlockInstances {

      @RegisterBlock(name = "boiler", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = TileEntityBoiler.class)
      public static BlockBoiler blockBoiler;

      @RegisterBlock(name = "boilertank", creativeTab = "tabKaidenCraft", hasInfo = true, tileEntity = TileEntityBoilerTank.class)
      public static BlockBoilerTank blockBoilerTank;

      @RegisterBlock(name = "smallturbine", creativeTab = "tabKaidenCraft", hasInfo = true)
      public static BlockSmallTurbine blockSmallTurbine;

      @RegisterBlock(name = "smalldynamo", creativeTab = "tabKaidenCraft", hasInfo = true)
      public static BlockSmallDynamo blockSmallDynamo;

      @RegisterBlock(name = "inductionheater", creativeTab = "tabKaidenCraft", hasInfo = true)
      public static BlockInductionHeater blockInductionHeater;
   }

   @SuppressWarnings("WeakerAccess")
   public static final class Items implements ItemInstances {

      @RegisterItem(name = "wrench", creativeTab = "tabKaidenCraft", hasInfo = true)
      public static ItemWrench itemWrench;
   }

   public static final class Fluids implements FluidInstances {

      @RegisterFluid(name = "steam", gaseous = true, temperature = 100)
      public static Fluid steam = new Fluid("steam", new ResourceLocation(KaidenCraft.MOD_ID, "block/steam_still"), new ResourceLocation(KaidenCraft.MOD_ID, "block/steam_flowing"));
   }

   public static final class EntityIds {
      public static final int MULTIBLOCK_BOILER = 0;
   }

   public static final class Config {
   }
}