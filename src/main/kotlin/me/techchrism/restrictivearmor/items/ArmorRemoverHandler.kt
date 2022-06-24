package me.techchrism.restrictivearmor.items

import me.techchrism.restrictivearmor.ItemGenerator
import org.bukkit.*
import org.bukkit.block.data.Directional
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

class ArmorRemoverHandler (plugin: Plugin) : Listener {
    companion object : ItemGenerator {
        lateinit var PERSISTENT_KEY: NamespacedKey
        lateinit var NOPICKUP_KEY: NamespacedKey

        fun isArmorRemover(item: ItemStack): Boolean {
            return item.itemMeta?.persistentDataContainer?.has(PERSISTENT_KEY, PersistentDataType.BYTE) ?: false
        }

        override fun generate(): ItemStack {
            val remover = ItemStack(Material.BARRIER, 1)

            val meta = remover.itemMeta!!
            meta.persistentDataContainer.set(PERSISTENT_KEY, PersistentDataType.BYTE, 1)
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, false)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.setDisplayName("${ChatColor.WHITE}Armor Remover")
            remover.itemMeta = meta

            return remover
        }
    }
    
    init {
        PERSISTENT_KEY = NamespacedKey.fromString("armorremover", plugin)!!
        NOPICKUP_KEY = NamespacedKey.fromString("nopickup", plugin)!!
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }
    
    @EventHandler(ignoreCancelled = true)
    private fun onBlockPlace(event: BlockPlaceEvent) {
        if(isArmorRemover(event.itemInHand)) {
            event.isCancelled = true
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val interacted = event.rightClicked
        val item = event.player.inventory.getItem(event.hand) ?: return
        if(!(interacted is Player && isArmorRemover(item))) return

        removeArmor(interacted, null, event.player.eyeLocation)
    }

    @EventHandler(ignoreCancelled = true)
    private fun onPlayerPickupItem(event: EntityPickupItemEvent) {
        if(event.entity.uniqueId != event.item.thrower) return

        event.item.persistentDataContainer.get(NOPICKUP_KEY, PersistentDataType.LONG)?.let {
            if(it < System.currentTimeMillis()) return
            event.isCancelled = true
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private fun onItemDispense(event: BlockDispenseEvent) {
        if(!isArmorRemover(event.item)) return;

        val facing = (event.block.blockData as Directional).facing
        val players = event.block.world.getNearbyEntities(BoundingBox.of(event.block.getRelative(facing)))
        for(player in players) {
            if(!(player is Player && player.gameMode == GameMode.SURVIVAL)) continue
            
            removeArmor(player, null, null)
        }
        
        event.isCancelled = true
    }
    
    private fun removeArmor(player: Player, slot: EquipmentSlot?, sendTowards: Location?) {
        val slots = slot?.let { arrayOf(slot) } ?: EquipmentSlot.values()
        for(currentSlot in slots) {
            player.inventory.getItem(currentSlot)?.let { 
                if(it.amount == 0 || it.type == Material.AIR) return@let
                
                val item = player.world.dropItem(player.eyeLocation, it)
                item.thrower = player.uniqueId
                item.persistentDataContainer.set(NOPICKUP_KEY, PersistentDataType.LONG, System.currentTimeMillis() + (10 * 1000))
                if(sendTowards != null) {
                    item.velocity = sendTowards.toVector().subtract(player.eyeLocation.toVector()).normalize().multiply(0.3)
                } else {
                    item.velocity = Vector(0, 0, 0)
                }
                player.world.playSound(player.location, Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.2f)
                it.amount = 0
            }
        }
    }
}