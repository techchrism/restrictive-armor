package me.techchrism.restrictivearmor.command

import me.techchrism.restrictivearmor.items.ArmorRemoverHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlsHelpMeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            ArmorRemoverHandler.removeArmor(sender, null, null)
        }
        return true
    }
}