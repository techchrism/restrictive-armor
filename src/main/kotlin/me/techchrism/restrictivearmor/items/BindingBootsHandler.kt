package me.techchrism.restrictivearmor.items

import me.techchrism.restrictivearmor.ItemGenerator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*

class BindingBootsHandler(plugin: Plugin) : Listener {
    companion object : ItemGenerator {
        lateinit var PERSISTENT_KEY: NamespacedKey
        
        fun areBoots(item: ItemStack): Boolean {
            return item.itemMeta?.persistentDataContainer?.has(PERSISTENT_KEY, PersistentDataType.BYTE) ?: false
        }
        
        fun wearingBoots(player: Player): Boolean {
            return player.inventory.boots?.let { areBoots(it) } == true
        }

        override fun generate(): ItemStack {
            val boots = ItemStack(Material.LEATHER_BOOTS, 1)

            val meta = boots.itemMeta!!
            meta.persistentDataContainer.set(PERSISTENT_KEY, PersistentDataType.BYTE, 1)
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, false)
            meta.setDisplayName("${ChatColor.WHITE}Binding Boots")
            meta.isUnbreakable = true
            boots.itemMeta = meta

            return boots
        }
    }
    
    private val hadBoots: HashSet<UUID> = HashSet()

    init {
        PERSISTENT_KEY = NamespacedKey.fromString("bindingboots", plugin)!!
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::checkPlayers, 1L, 1L)
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }
    
    private fun Player.wearingBindingBoots(): Boolean {
        return wearingBoots(this)
    }
    
    private fun checkPlayers() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (hadBoots.contains(player.uniqueId)) {
                if (!player.wearingBindingBoots()) {
                    // Player took off boots
                    hadBoots.remove(player.uniqueId)
                    player.walkSpeed = 0.2F
                    player.removePotionEffect(PotionEffectType.JUMP)
                }
            } else {
                if (player.wearingBindingBoots()) {
                    // Player put on boots
                    hadBoots.add(player.uniqueId)
                    player.walkSpeed = 0.0F
                    player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Int.MAX_VALUE, 251, false, false, false))
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private fun onDamage(event: EntityDamageEvent) {
        val player = event.entity
        if(!(player is Player && player.wearingBindingBoots())) return
        
        event.damage = 0.0
        if(event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }
        
        // Prevent client<->server desync
        player.velocity = Vector(0, 0, 0)
    }
    
    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        hadBoots.remove(event.player.uniqueId)
    }

    @EventHandler
    private fun onWorldChange(event: PlayerChangedWorldEvent) {
        hadBoots.remove(event.player.uniqueId)
    }

    @EventHandler
    private fun onRespawn(event: PlayerRespawnEvent) {
        hadBoots.remove(event.player.uniqueId)
    }
}