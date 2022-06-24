package me.techchrism.restrictivearmor

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

interface ItemGenerator {
    fun generate(): ItemStack
}