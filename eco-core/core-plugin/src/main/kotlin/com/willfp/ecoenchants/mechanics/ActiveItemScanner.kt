package com.willfp.ecoenchants.mechanics

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoenchants.EcoEnchantsPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.stream.Stream

fun Player.illegalEnchantScan() {
    val inv = this.inventory
    Stream.of(
        inv.itemInMainHand,
        inv.itemInOffHand,
        inv.helmet,
        inv.chestplate,
        inv.leggings,
        inv.boots
    ).filter{it != null && it.itemMeta != null}.forEach { item ->
        val meta = item!!.itemMeta
        if(meta.hasEnchants() && meta.enchants.size > 9) {
            EcoEnchantsPlugin.instance.scheduler.run {
                while (meta.enchants.size > 9) {
                    if (meta is EnchantmentStorageMeta) {
                        meta.removeStoredEnchant(meta.storedEnchants.keys.random())
                    } else {
                        meta.removeEnchant(meta.enchants.keys.random())
                    }
                }
                item.itemMeta = meta
            }
        }
    }
}