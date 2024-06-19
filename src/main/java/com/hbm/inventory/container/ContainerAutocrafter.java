package com.hbm.inventory.container;

import com.hbm.inventory.SlotMachineOutput;
import com.hbm.inventory.SlotPattern;
import com.hbm.tileentity.machine.TileEntityMachineAutocrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAutocrafter extends Container {

	private final TileEntityMachineAutocrafter autocrafter;

	public ContainerAutocrafter(InventoryPlayer invPlayer, TileEntityMachineAutocrafter tema) {
		autocrafter = tema;

		/* TEMPLATE */
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotPattern(this.autocrafter.inventory, j + i * 3, 44 + j * 18, 22 + i * 18));
			}
		}
		this.addSlotToContainer(new SlotPattern(this.autocrafter.inventory, 9, 116, 40));

		/* RECIPE */
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotItemHandler(this.autocrafter.inventory, j + i * 3 + 10, 44 + j * 18, 86 + i * 18));
			}
		}
		this.addSlotToContainer(new SlotItemHandler(this.autocrafter.inventory, 19, 116, 104));
		
		//Battery
		this.addSlotToContainer(new SlotItemHandler(this.autocrafter.inventory, 20, 17, 99));

        // player inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 158 + i * 18));
			}
		}

		// player hotbar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 216));
		}
	}

	@Override
	public ItemStack slotClick(int index, int button, ClickType mode, EntityPlayer player) {
		
		//L/R: 0
		//M3: 3
		//SHIFT: 1
		//DRAG: 5

		if(index < 0 || index > 9) {
			return super.slotClick(index, button, mode, player);
		}

		Slot slot = this.getSlot(index);
		
		ItemStack ret = null;
		ItemStack held = player.inventory.getItemStack();
		
		if(slot.getHasStack())
			ret = slot.getStack().copy();
		
		//Don't allow any interaction for the template's output
		if(index == 9) {
			
			if((mode.equals(ClickType.PICKUP) || mode.equals(ClickType.PICKUP_ALL)) && slot.getHasStack()) {
				autocrafter.nextTemplate();
				this.detectAndSendChanges();
			}
			
			return ret;
		}
		
		if((mode.equals(ClickType.PICKUP) || mode.equals(ClickType.PICKUP_ALL)) && slot.getHasStack()) {
			autocrafter.nextMode(index);
			return ret;
			
		} else {
	
			slot.putStack(held != null ? held.copy() : null);
			
			if(slot.getHasStack()) {
				slot.getStack().setCount(1);
			}
			
			slot.onSlotChanged();
			autocrafter.initPattern(slot.getStack(), index);
			autocrafter.updateTemplateGrid();
			
			return ret;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return autocrafter.isUseableByPlayer(player);
	}
}
