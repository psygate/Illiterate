/*
 * The MIT License
 *
 * Copyright 2015 psygate (https://github.com/psygate).
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

/**
 *
 * @author psygate (https://github.com/psygate)
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class Illiterate extends JavaPlugin {

    private final static Map<String, List<String>> matmap = new HashMap<>();

    static {
        matmap.put("hoes", Arrays.asList(new String[]{
            Material.WOOD_HOE.name(),
            Material.IRON_HOE.name(),
            Material.GOLD_HOE.name(),
            Material.DIAMOND_HOE.name()
        }));
        matmap.put("swords", Arrays.asList(new String[]{
            Material.WOOD_SWORD.name(),
            Material.IRON_SWORD.name(),
            Material.GOLD_SWORD.name(),
            Material.DIAMOND_SWORD.name()
        }));
        matmap.put("axes", Arrays.asList(new String[]{
            Material.WOOD_AXE.name(),
            Material.IRON_AXE.name(),
            Material.GOLD_AXE.name(),
            Material.DIAMOND_AXE.name()
        }));
        matmap.put("axes", Arrays.asList(new String[]{
            Material.WOOD_PICKAXE.name(),
            Material.IRON_PICKAXE.name(),
            Material.GOLD_PICKAXE.name(),
            Material.DIAMOND_PICKAXE.name()
        }));
        matmap.put("leggings", Arrays.asList(new String[]{
            Material.LEATHER_LEGGINGS.name(),
            Material.IRON_LEGGINGS.name(),
            Material.GOLD_LEGGINGS.name(),
            Material.CHAINMAIL_LEGGINGS.name(),
            Material.DIAMOND_LEGGINGS.name()
        }));
        matmap.put("trousers", matmap.get("leggings"));
        matmap.put("pants", matmap.get("leggings"));
        matmap.put("chestplate", Arrays.asList(new String[]{
            Material.LEATHER_CHESTPLATE.name(),
            Material.IRON_CHESTPLATE.name(),
            Material.GOLD_CHESTPLATE.name(),
            Material.CHAINMAIL_CHESTPLATE.name(),
            Material.DIAMOND_CHESTPLATE.name()
        }));
        matmap.put("boots", Arrays.asList(new String[]{
            Material.LEATHER_BOOTS.name(),
            Material.IRON_BOOTS.name(),
            Material.GOLD_BOOTS.name(),
            Material.CHAINMAIL_BOOTS.name(),
            Material.DIAMOND_BOOTS.name()
        }));
        matmap.put("shoes", matmap.get("boots"));
        matmap.put("helmets", Arrays.asList(new String[]{
            Material.LEATHER_HELMET.name(),
            Material.IRON_HELMET.name(),
            Material.GOLD_HELMET.name(),
            Material.CHAINMAIL_HELMET.name(),
            Material.DIAMOND_HELMET.name()
        }));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Map<Material, List<EnchantmentLevel>> applicable = loadEnchantments();

        getServer().getPluginManager().registerEvents(new AnvilListener(applicable), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private Map<Material, List<EnchantmentLevel>> loadEnchantments() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Map<Material, List<EnchantmentLevel>> enmap = new HashMap<>();

        for (Object o : getConfig().getMapList("Items")) {
            Map<String, ?> map = (Map<String, ?>) o;
            for (String key : map.keySet()) {
                List<String> keys = coalesceMaterial(key);

                LinkedList<Material> materials = new LinkedList<>();
                for (String ikey : keys) {
                    materials.add(Material.valueOf(ikey));
                }

                for (Map<String, ?> enchantmentmap : (Collection<Map<String, ?>>) map.get(key)) {
                    for (String ekey : enchantmentmap.keySet()) {
                        Enchantment enchantment = Enchantment.getByName(ekey.toUpperCase());

                        if (enchantment == null) {
                            throw new NullPointerException("Enchantment not found: " + ekey);
                        }

                        String level = (String) enchantmentmap.get(ekey);
                        if (level.toLowerCase().equals("anylevel")) {
                            for (int i = enchantment.getStartLevel(); i <= enchantment.getMaxLevel(); i++) {
                                add(enmap, materials, new EnchantmentLevel(enchantment, i));
                            }
                        } else {
                            int ilevel = Integer.parseInt(level);
                            add(enmap, materials, new EnchantmentLevel(enchantment, ilevel));
                        }
                    }
                }
            }
        }

        return enmap;
    }

    private List<String> coalesceMaterial(String key) {
        LinkedList<String> out = new LinkedList<>();
        if (matmap.containsKey(key.toLowerCase())) {
            out.addAll(matmap.get(key.toLowerCase()));
        } else {
            out.add(key.toUpperCase());
        }

        return out;
    }

    private void add(Map<Material, List<EnchantmentLevel>> enmap, LinkedList<Material> materials, EnchantmentLevel enchantmentLevel) {
        for (Material m : materials) {
            if (!enmap.containsKey(m)) {
                enmap.put(m, new ArrayList<EnchantmentLevel>());
            }

            enmap.get(m).add(enchantmentLevel);
        }
    }

}
