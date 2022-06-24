package me.techchrism.restrictivearmor.items

import me.techchrism.restrictivearmor.ItemGenerator
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import java.util.*

class BlindfoldHelmetHandler(plugin: Plugin) : Listener {
    companion object : ItemGenerator {
        lateinit var PERSISTENT_KEY: NamespacedKey

        fun isHelmet(item: ItemStack): Boolean {
            return item.itemMeta?.persistentDataContainer?.has(PERSISTENT_KEY, PersistentDataType.BYTE) ?: false
        }

        fun wearingBlindfoldHelmet(player: Player): Boolean {
            return player.inventory.helmet?.let { isHelmet(it) } == true
        }

        override fun generate(): ItemStack {
            val helmet = ItemStack(Material.LEATHER_HELMET, 1)

            val meta = helmet.itemMeta!!
            meta.persistentDataContainer.set(PERSISTENT_KEY, PersistentDataType.BYTE, 1)
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, false)
            (meta as LeatherArmorMeta).setColor(Color.BLACK)
            meta.setDisplayName("${ChatColor.WHITE}Blindfold")
            meta.isUnbreakable = true
            helmet.itemMeta = meta

            return helmet
        }
    }

    private val hadHelmet: HashSet<UUID> = HashSet()

    init {
        PERSISTENT_KEY = NamespacedKey.fromString("blindfoldhelmet", plugin)!!
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::checkPlayers, 1L, 1L)
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    private fun Player.wearingBlindfoldHelmet(): Boolean {
        return wearingBlindfoldHelmet(this)
    }

    private fun checkPlayers() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (hadHelmet.contains(player.uniqueId)) {
                if (!player.wearingBlindfoldHelmet()) {
                    // Player took off helmet
                    player.scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard
                    hadHelmet.remove(player.uniqueId)
                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                }
            } else {
                if (player.wearingBlindfoldHelmet()) {
                    // Player put on helmet
                    val scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
                    val team = scoreboard.registerNewTeam("nametag-hide")
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                    Bukkit.getOnlinePlayers().forEach { team.addEntry(it.name) }
                    player.scoreboard = scoreboard
                    
                    hadHelmet.add(player.uniqueId)
                    player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Int.MAX_VALUE, 1, false, false, false))
                }
            }
        }
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        hadHelmet.remove(event.player.uniqueId)
    }

    @EventHandler
    private fun onWorldChange(event: PlayerChangedWorldEvent) {
        hadHelmet.remove(event.player.uniqueId)
    }
    
    @EventHandler
    private fun onRespawn(event: PlayerRespawnEvent) {
        hadHelmet.remove(event.player.uniqueId)
    }
}