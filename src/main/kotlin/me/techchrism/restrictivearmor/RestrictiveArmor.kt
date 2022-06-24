package me.techchrism.restrictivearmor

import me.techchrism.restrictivearmor.command.GetBindingBootsCommand
import me.techchrism.restrictivearmor.items.ArmorRemoverHandler
import me.techchrism.restrictivearmor.items.BindingBootsHandler
import me.techchrism.restrictivearmor.items.BlindfoldHelmetHandler
import me.techchrism.restrictivearmor.listeners.PlayerArmorEquip
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class RestrictiveArmor : JavaPlugin() {
    override fun onEnable() {
        getCommand("getBindingBoots")?.setExecutor(GetBindingBootsCommand())
        
        BindingBootsHandler(this)
        BlindfoldHelmetHandler(this)
        ArmorRemoverHandler(this)
        
        Bukkit.getPluginManager().registerEvents(PlayerArmorEquip(), this)
    }
}