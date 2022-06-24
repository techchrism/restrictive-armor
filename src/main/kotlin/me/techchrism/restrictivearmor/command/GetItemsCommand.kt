package me.techchrism.restrictivearmor.command

import me.techchrism.restrictivearmor.items.ArmorRemoverHandler
import me.techchrism.restrictivearmor.items.BindingBootsHandler
import me.techchrism.restrictivearmor.items.BlindfoldHelmetHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GetItemsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            sender.inventory.addItem(BindingBootsHandler.generate())
            sender.inventory.addItem(BlindfoldHelmetHandler.generate())
            sender.inventory.addItem(ArmorRemoverHandler.generate())
        } else {
            sender.sendMessage("Must be a player")
        }
        return true
    }
}