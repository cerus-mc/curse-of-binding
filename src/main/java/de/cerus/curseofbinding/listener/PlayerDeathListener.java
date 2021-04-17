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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    private final Map<UUID, Set<ArmorContent>> map = new HashMap<>();

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player entity = event.getEntity();
        final List<ItemStack> drops = event.getDrops();
        final Set<ArmorContent> contents = new HashSet<>();

        for (int i = 0; i < entity.getInventory().getArmorContents().length; i++) {
            final ItemStack armorContent = entity.getInventory().getArmorContents()[i];
            if (armorContent == null) {
                continue;
            }
            if (!armorContent.hasItemMeta()) {
                return;
            }
            if (!armorContent.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE)) {
                return;
            }
            drops.remove(armorContent);
            contents.add(new ArmorContent(i, armorContent));
        }

        if (contents.isEmpty()) {
            return;
        }
        this.map.put(entity.getUniqueId(), contents);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (!this.map.containsKey(player.getUniqueId())) {
            return;
        }

        final ItemStack[] itemStacks = player.getInventory().getArmorContents();

        for (final ArmorContent armorContent : this.map.get(player.getUniqueId())) {
            itemStacks[armorContent.index] = armorContent.itemStack;
        }
        player.getInventory().setArmorContents(itemStacks);

        this.map.remove(player.getUniqueId());
    }

    private static class ArmorContent {

        private final int index;
        private final ItemStack itemStack;

        public ArmorContent(final int index, final ItemStack itemStack) {
            this.index = index;
            this.itemStack = itemStack;
        }

        public int getIndex() {
            return this.index;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }
}
