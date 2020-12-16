/*
 *  Copyright (c) 2018 Cerus
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Cerus
 *
 */

package de.cerus.curseofbinding.listener;

import de.cerus.ceruslib.listenerframework.CerusListener;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PlayerDeathListener extends CerusListener {

    private Map<UUID, Set<ArmorContent>> map = new HashMap<>();

    public PlayerDeathListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        List<ItemStack> drops = event.getDrops();
        Set<ArmorContent> contents = new HashSet<>();

        for(int i = 0; i < entity.getInventory().getArmorContents().length; i++) {
            ItemStack armorContent = entity.getInventory().getArmorContents()[i];
            if(armorContent == null) continue;
            if(!armorContent.hasItemMeta()) return;
            if(!armorContent.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE)) return;
            drops.remove(armorContent);
            contents.add(new ArmorContent(i, armorContent));
        }

        if(contents.isEmpty()) return;
        map.put(entity.getUniqueId(), contents);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(!map.containsKey(player.getUniqueId())) return;

        ItemStack[] itemStacks = player.getInventory().getArmorContents();

        for (ArmorContent armorContent : map.get(player.getUniqueId())) {
            itemStacks[armorContent.index] = armorContent.itemStack;
        }
        player.getInventory().setArmorContents(itemStacks);

        map.remove(player.getUniqueId());
    }

    private static class ArmorContent {

        private int index;
        private ItemStack itemStack;

        public ArmorContent(int index, ItemStack itemStack) {
            this.index = index;
            this.itemStack = itemStack;
        }

        public int getIndex() {
            return index;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}
