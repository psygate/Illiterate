/*
 * The MIT License
 *
 * Copyright 2015  psygate (https://github.com/psygate).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.psygate.minecraft.illiterate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class AnvilListener implements Listener {

    private static final Material remove = Material.ENCHANTED_BOOK;
    private final Map<Material, List<EnchantmentLevel>> applicable;

    public AnvilListener(Map<Material, List<EnchantmentLevel>> applicable) {
        this.applicable = applicable;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void removeEnchantmentBooks(final InventoryClickEvent ev) {
        Inventory inv = ev.getInventory();
        if (inv == null) {
            return;
        }

        if (inv instanceof AnvilInventory) {
            AnvilInventory ainv = (AnvilInventory) inv;
            if (ainv.getItem(0) != null && ainv.getItem(1) != null && ainv.getItem(1).getType().equals(Material.ENCHANTED_BOOK)) {
                ItemStack toenchant = ainv.getItem(0);
                ItemStack book = ainv.getItem(1);
                try {
                    if (!applicable.containsKey(toenchant.getType())) {
                        ainv.setItem(3, null);
                    } else {
                        List<EnchantmentLevel> allowed = applicable.get(toenchant.getType());
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

                        for (Entry<Enchantment, Integer> en : meta.getStoredEnchants().entrySet()) {
                            EnchantmentLevel type = new EnchantmentLevel(en.getKey(), en.getValue());
                            System.out.println(type);
                            if (!allowed.contains(type)) {
                                ainv.setItem(3, null);
                            }
                        }
                    }
                } catch (Exception e) {
                    ainv.setItem(2, null);
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
