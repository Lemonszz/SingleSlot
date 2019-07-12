package party.lemons.singleinv;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ContainerSingleInv extends Container
{
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	/** The crafting matrix inventory. */
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	/** Determines if inventory manipulation should be handled. */
	public boolean isLocalWorld;
	private final EntityPlayer player;

	public ContainerSingleInv(InventoryPlayer inventory, EntityPlayer player, boolean isLocalWorld)
	{
		this.player = player;
		this.isLocalWorld = isLocalWorld;

		this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 154, 28));

		for (int i = 0; i < 2; ++i)
		{
			for (int j = 0; j < 2; ++j)
			{
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}
		for (int k = 0; k < 4; ++k)
		{
			final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
			this.addSlotToContainer(new Slot(inventory, 36 + (3 - k), 8, 8 + k * 18)
			{
				/**
				 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
				 * in the case of armor slots)
				 */
				public int getSlotStackLimit()
				{
					return 1;
				}
				/**
				 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
				 * fuel.
				 */
				public boolean isItemValid(ItemStack stack)
				{
					return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
				}
				/**
				 * Return whether this slot's stack can be taken from this slot.
				 */
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					ItemStack itemstack = this.getStack();
					return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
				}
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture()
				{
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});


			this.addSlotToContainer(new Slot(inventory, 0, 80, 102));

		}


	}

	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		this.craftResult.clear();

		if (!playerIn.world.isRemote)
		{
			this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		return ItemStack.EMPTY;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
	 * is null for the initial slot that was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}
}
