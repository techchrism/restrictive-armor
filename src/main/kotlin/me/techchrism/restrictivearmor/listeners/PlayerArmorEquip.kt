package me.techchrism.restrictivearmor.listeners

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerArmorEquip : Listener {
    @EventHandler(ignoreCancelled = true)
    private fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val interacted = event.rightClicked
        val item = event.player.inventory.getItem(event.hand) ?: return

        if(!(interacted is Player && item.type.equipmentSlot != EquipmentSlot.HAND)) return
        val armorItem = interacted.inventory.getItem(item.type.equipmentSlot)
        if(armorItem != null && armorItem.type != Material.AIR) return
        
        interacted.inventory.setItem(item.type.equipmentSlot, item.clone())
        item.amount = 0
        item.type = Material.AIR
        event.isCancelled = true
    }
}