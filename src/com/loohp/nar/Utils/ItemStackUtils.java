package com.loohp.nar.Utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtils {
	
	public static boolean isSimilarIgnoreDurabiblty(ItemStack stack, ItemStack compareTo) {
		if (stack == null && compareTo == null) {
            return true;
        }
        if (stack == null) {
            return false;
        }
        if (compareTo == null) {
            return false;
        }
        if (stack == compareTo) {
            return true;
        }
        if (stack.hasItemMeta() && compareTo.hasItemMeta() && stack.getItemMeta() instanceof Damageable && compareTo.getItemMeta() instanceof Damageable) {
        	ItemStack fullStack = stack.clone();
        	ItemStack fullCompareTo = compareTo.clone();        	
        	
        	ItemMeta stackMeta = fullStack.getItemMeta();
        	((Damageable) stackMeta).setDamage(0);
        	fullStack.setItemMeta(stackMeta);
        	
        	ItemMeta compareToMeta = fullCompareTo.getItemMeta();
        	((Damageable) compareToMeta).setDamage(0);
        	fullCompareTo.setItemMeta(compareToMeta);
        	
        	return fullStack.isSimilar(fullCompareTo);
        }
        return stack.isSimilar(compareTo);
    }
	
	public static Inventory deepClone(Inventory inv) {
		Inventory clone = Bukkit.createInventory(null, (int) (Math.ceil((double) inv.getSize() / 9) * 9));
		for (int i = 0; i < clone.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (item != null) {
				clone.setItem(i, item.clone());
			} else {
				clone.setItem(i, null);
			}
		}
		return clone;
	}

}
