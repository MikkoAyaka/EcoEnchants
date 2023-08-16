package com.willfp.ecoenchants.mechanics

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoenchants.EcoEnchantsPlugin
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.stream.Stream
fun Player.illegalEnchantScan() {
    EcoEnchantsPlugin.instance.scheduler.runAsync {
        val inv = this.inventory
        Stream.of(
            inv.itemInMainHand,
            inv.itemInOffHand,
            inv.helmet,
            inv.chestplate,
            inv.leggings,
            inv.boots
        ).filter{it != null && it.itemMeta != null}.forEach { item ->
            var needUpdate = false
            val meta = item!!.itemMeta
            // 最大词条限制检查
            if(meta.hasEnchants() && meta.enchants.size > 9) {
                needUpdate = true
                while (checkEnchantSize(meta)) {
                    if (meta is EnchantmentStorageMeta) {
                        meta.removeStoredEnchant(meta.storedEnchants.keys.random())
                    } else {
                        meta.removeEnchant(meta.enchants.keys.random())
                    }
                }
            }
            if(protectionConflict(meta)) needUpdate = true
            if(damageConflict(meta)) needUpdate = true
            if(pickaxeConflict(meta)) needUpdate = true
            if(bowConflict1(meta)) needUpdate = true
            if(bowConflict2(meta)) needUpdate = true
            if(tridentConflict1(meta)) needUpdate = true
            if(tridentConflict2(meta)) needUpdate = true
            if(bootsConflict(meta)) needUpdate = true
            if(needUpdate) EcoEnchantsPlugin.instance.scheduler.run {
                item.itemMeta = meta
            }
        }
    }
}
private fun HashSet<Enchantment>.addIfExists(meta: ItemMeta,enchantment: Enchantment) {
    if(meta.hasEnchant(enchantment)) this.add(enchantment)
}
private fun checkEnchantSize(meta : ItemMeta) = meta.enchants.size > 9
private fun reduceEnchants(meta : ItemMeta,set : Set<Enchantment>) : Boolean {
    if(set.size >= 2) {
        val keepEnchant = set.random()
        val keepLevel = meta.getEnchantLevel(keepEnchant)
        set.forEach { meta.removeEnchant(it) }
        meta.addEnchant(keepEnchant,keepLevel,true)
        return true
    }
    return false
}
// 保护附魔冲突
private fun protectionConflict(meta: ItemMeta) : Boolean {
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta,Enchantment.PROTECTION_ENVIRONMENTAL)
    enchants.addIfExists(meta,Enchantment.PROTECTION_EXPLOSIONS)
    enchants.addIfExists(meta,Enchantment.PROTECTION_FIRE)
    enchants.addIfExists(meta,Enchantment.PROTECTION_PROJECTILE)
    return reduceEnchants(meta,enchants)
}
// 伤害附魔冲突
private fun damageConflict(meta: ItemMeta) : Boolean{
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta,Enchantment.DAMAGE_ALL)
    enchants.addIfExists(meta,Enchantment.DAMAGE_ARTHROPODS)
    enchants.addIfExists(meta,Enchantment.DAMAGE_UNDEAD)
    return reduceEnchants(meta,enchants)
}
// 镐子附魔冲突
private fun pickaxeConflict(meta: ItemMeta) : Boolean{
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.SILK_TOUCH)
    enchants.addIfExists(meta, Enchantment.LOOT_BONUS_BLOCKS)
    return reduceEnchants(meta,enchants)
}
// 鞋子附魔冲突
private fun bootsConflict(meta: ItemMeta) : Boolean {
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.FROST_WALKER)
    enchants.addIfExists(meta, Enchantment.WATER_WORKER)
    return reduceEnchants(meta,enchants)
}
private fun bowConflict1(meta: ItemMeta) : Boolean {
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.ARROW_INFINITE)
    enchants.addIfExists(meta, Enchantment.MENDING)
    return reduceEnchants(meta,enchants)
}
private fun bowConflict2(meta: ItemMeta) : Boolean{
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.MULTISHOT)
    enchants.addIfExists(meta, Enchantment.PIERCING)
    return reduceEnchants(meta,enchants)
}
private fun tridentConflict1(meta: ItemMeta) : Boolean{
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.THORNS)
    enchants.addIfExists(meta, Enchantment.RIPTIDE)
    return reduceEnchants(meta,enchants)
}
private fun tridentConflict2(meta: ItemMeta) : Boolean{
    val enchants = HashSet<Enchantment>()
    enchants.addIfExists(meta, Enchantment.LOYALTY)
    enchants.addIfExists(meta, Enchantment.RIPTIDE)
    return reduceEnchants(meta,enchants)
}