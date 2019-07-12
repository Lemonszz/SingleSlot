package party.lemons.singleinv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@Mod(modid = SingleInv.MODID, name = "Single Slot", version = "1.0.0")
@Mod.EventBusSubscriber(modid = SingleInv.MODID)
public class SingleInv implements IGuiHandler
{
	public static final String MODID = "singleinv";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.Instance(MODID)
	public static SingleInv INSTANCE;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		NETWORK.registerMessage(MessageOpenInventory.Handler.class, MessageOpenInventory.class, 0, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, this);
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.phase != TickEvent.Phase.START)
			return;

		event.player.inventory.currentItem = 0;
		for(int i = 1; i < event.player.inventory.getSizeInventory() - 5; i++)
		{
			event.player.inventory.setInventorySlotContents(i, new ItemStack(ModItems.FILLER_ITEM));
		}
		event.player.inventory.setInventorySlotContents(event.player.inventory.getSizeInventory() -1, new ItemStack(Items.AIR));
	}

	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new ContainerSingleInv(player.inventory, player, world.isRemote);
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new GuiSingleInv(player);
	}


	@Mod.EventBusSubscriber(modid = SingleInv.MODID)
	@GameRegistry.ObjectHolder(MODID)
	public static class ModItems
	{
		public static final Item FILLER_ITEM = Items.AIR;

		@SubscribeEvent
		public static void onRegisterItems(RegistryEvent.Register<Item> event)
		{
			event.getRegistry().register(new ItemFiller().setRegistryName(MODID, "filler_item"));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onOpenGui(GuiOpenEvent event)
	{
		if(event.getGui() != null && event.getGui().getClass() == GuiInventory.class)
		{
			final EntityPlayer player = Minecraft.getMinecraft().player;

			if(!player.isCreative()) {
				event.setCanceled(true);
				NETWORK.sendToServer(new MessageOpenInventory());
			}
		}
	}
	protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onIngameGui(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			GuiIngameForge.renderHotbar = false;
			Minecraft mc = Minecraft.getMinecraft();
			System.out.println("Render");

			if (mc.getRenderViewEntity() instanceof EntityPlayer)
			{
				ScaledResolution sr = new ScaledResolution(mc);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
				EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
				ItemStack itemstack = entityplayer.getHeldItemOffhand();
				EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
				int i = sr.getScaledWidth() / 2;
				GuiUtils.drawTexturedModalRect(i - 11, sr.getScaledHeight() - 22, 0, 0, 22, 22, -90);
				GlStateManager.enableRescaleNormal();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderHelper.enableGUIStandardItemLighting();

				int i1 = i - 90 + 4 * 20 + 2;
				int j1 = sr.getScaledHeight() - 16 - 3;
				renderHotbarItem(i1, j1, event.getPartialTicks(), entityplayer, entityplayer.inventory.mainInventory.get(0));


				if (mc.gameSettings.attackIndicator == 2)
				{
					float f1 = mc.player.getCooledAttackStrength(0.0F);

					if (f1 < 1.0F)
					{
						int i2 = sr.getScaledHeight() - 20;
						int j2 = i + 91 + 6;

						if (enumhandside == EnumHandSide.RIGHT)
						{
							j2 = i - 91 - 22;
						}

						mc.getTextureManager().bindTexture(Gui.ICONS);
						int k1 = (int)(f1 * 19.0F);
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						GuiUtils.drawTexturedModalRect(j2, i2, 0, 94, 18, 18, -90);
						GuiUtils.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1, -90);
					}
				}

				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				GlStateManager.disableBlend();
			}
		}
		GuiIngameForge.renderHotbar = false;

	}

	@SideOnly(Side.CLIENT)
	protected static void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack)
	{
		if (!stack.isEmpty())
		{
			float f = (float)stack.getAnimationsToGo() - partialTicks;

			if (f > 0.0F)
			{
				GlStateManager.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				GlStateManager.translate((float)(x + 8), (float)(y + 12), 0.0F);
				GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
			}

			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(player, stack, x, y);

			if (f > 0.0F)
			{
				GlStateManager.popMatrix();
			}

			Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, x, y);
		}
	}


	private static class ItemFiller extends Item
	{
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
		{
			return false;
		}
	}
}
