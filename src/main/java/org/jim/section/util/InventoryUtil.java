package org.jim.section.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {
	private InventoryUtil() {}

	/**
	 * 判断一个玩家的背包与末影箱是否全部为空
	 * 
	 * @param player
	 * @return
	 */
	public static boolean inventoryEmpty(final Player player) {
		final ItemStack[][] stacks = new ItemStack[][] { player.getInventory().getContents(), player.getEnderChest().getContents() };

		for (final ItemStack[] stacks2 : stacks) {
			for (final ItemStack itemStack : stacks2) {
				if (itemStack != null) {
					return false;
				}
			}
		}

		return true;
	}
}
