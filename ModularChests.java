package vovapolu.modularchests;

import vovapolu.modularchests.block.ModularChestBaseBlock;
import vovapolu.modularchests.block.ModularChestItemBlock;
import vovapolu.modularchests.items.BreakableUpgradeItem;
import vovapolu.modularchests.items.CoreAddItem;
import vovapolu.modularchests.items.ModularChestItemRender;
import vovapolu.modularchests.items.StackSizeUpgradeItem;
import vovapolu.modularchests.items.StorageAddItemType;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

@Mod(modid = "ModularChests", name = "Modular Chests", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, 
	packetHandler = PacketHandler.class, channels = {"TE", "SB", "UTE"})
public class ModularChests {
	@Instance("ModularChests")
	public static ModularChests instance;

	@SidedProxy(clientSide = "vovapolu.modularchests.client.ClientProxy", serverSide = "vovapolu.modularchests.CommonProxy")
	public static CommonProxy proxy;

	public static Block modularChestBlock;
	public static Item coreUpgradeItem;
	public static StackSizeUpgradeItem stackSizeUpgradeItem;
	public static BreakableUpgradeItem breakableUpgradeItem;
	
	// Cfg
	public static int modularBlockId;
	public static int addItemId;
	public static int coreAddItemId;
	public static int StackSizeUpgradeItemId;
	public static int inventoryFactor;
	public static int BreakableUpgradeItemId;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration cfg = new Configuration(
				event.getSuggestedConfigurationFile());
		cfg.load();
		modularBlockId = cfg.getBlock("ModularChest", 500).getInt();
		addItemId = cfg.getItem("AddItem", 6002).getInt();
		coreAddItemId = cfg.getItem("CoreAddItem", 6001).getInt();
		StackSizeUpgradeItemId = cfg.getItem("StackSizeUpgradeItem", 6000).getInt();
		BreakableUpgradeItemId = cfg.getItem("BreakableUpgradeItem", 5999).getInt();
		Property InventoryFactorProperty = cfg.get(Configuration.CATEGORY_GENERAL, "InventoryFactor", 5);		
		inventoryFactor = InventoryFactorProperty.getInt();
	
		cfg.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		modularChestBlock = new ModularChestBaseBlock(modularBlockId,
				Material.ground);
		coreUpgradeItem = new CoreAddItem(coreAddItemId);
		stackSizeUpgradeItem = new StackSizeUpgradeItem(StackSizeUpgradeItemId, "stackSizeUpgradeItem", 
				"stackSizeItem", "redstoneBound.png");
		breakableUpgradeItem = new BreakableUpgradeItem(BreakableUpgradeItemId, "breakableUpgradeItem", 
				"breakableItem", "lapizBound.png");
		
		GameRegistry.registerItem(stackSizeUpgradeItem, "stackSizeUpgradeItem");
		GameRegistry.registerItem(breakableUpgradeItem, "breakableUpgradeItem");
		GameRegistry.registerBlock(modularChestBlock, ModularChestItemBlock.class, "ModularChest");
		GameRegistry.registerItem(coreUpgradeItem, "CoreUpgradeItem");		
		LanguageRegistry.addName(modularChestBlock, "Modular Chest");
		LanguageRegistry.addName(coreUpgradeItem, "Upgrade Core");
		LanguageRegistry.addName(breakableUpgradeItem, "Breakable Upgrade");
		LanguageRegistry.addName(stackSizeUpgradeItem, "Stack Size Upgrade");
		
		GameRegistry.addRecipe(new ItemStack(coreUpgradeItem), " x ", "xyx", " x ",
		        'y', new ItemStack(Block.glass), 'x', new ItemStack(Block.stone));
		GameRegistry.addRecipe(new ItemStack(modularChestBlock), "xxx", "xyx", "xxx",
		        'y', new ItemStack(Block.chest), 'x', new ItemStack(Block.cobblestone));
		GameRegistry.addRecipe(new ItemStack(stackSizeUpgradeItem), "xxx", "xcx", "xxx", 
				'x', new ItemStack(Item.redstone), 'c', new ItemStack(coreUpgradeItem));
		GameRegistry.addRecipe(new ItemStack(breakableUpgradeItem), "xxx", "xcx", "xxx", 
				'x', new ItemStack(Item.dyePowder, 1, 4), 'c', new ItemStack(coreUpgradeItem));
		
		StorageAddItemType.registreItems();
		
		GameRegistry.registerTileEntity(ModularChestTileEntity.class,
				"ModularChestTileEntity");
		ClientRegistry.bindTileEntitySpecialRenderer(
				ModularChestTileEntity.class, new ModularChestRenderer());
		MinecraftForgeClient.registerItemRenderer(modularBlockId, new ModularChestItemRender());
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
		
		proxy.registerRenderInformation();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
}