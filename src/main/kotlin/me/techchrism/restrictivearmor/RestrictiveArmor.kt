package me.techchrism.restrictivearmor

import me.techchrism.restrictivearmor.command.GetItemsCommand
import me.techchrism.restrictivearmor.command.PlsHelpMeCommand
import me.techchrism.restrictivearmor.items.ArmorRemoverHandler
import me.techchrism.restrictivearmor.items.BindingBootsHandler
import me.techchrism.restrictivearmor.items.BlindfoldHelmetHandler
import me.techchrism.restrictivearmor.listeners.PlayerArmorEquip
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class RestrictiveArmor : JavaPlugin() {
    private val addedRecipes: HashSet<NamespacedKey> = HashSet()
    
    override fun onEnable() {
        getCommand("getitems")?.setExecutor(GetItemsCommand())
        getCommand("plshelpme")?.setExecutor(PlsHelpMeCommand())
        
        BindingBootsHandler(this)
        BlindfoldHelmetHandler(this)
        ArmorRemoverHandler(this)
        
        Bukkit.getPluginManager().registerEvents(PlayerArmorEquip(), this)
        
        val blindfoldRecipeKey = NamespacedKey.fromString("blindfold", this)!!
        val blindfoldRecipe = ShapedRecipe(blindfoldRecipeKey, BlindfoldHelmetHandler.generate())
        blindfoldRecipe.shape("www")
        blindfoldRecipe.setIngredient('w', RecipeChoice.MaterialChoice(Material.BLACK_WOOL))
        Bukkit.addRecipe(blindfoldRecipe)
        addedRecipes.add(blindfoldRecipeKey)

        val bindingBootsRecipeKey = NamespacedKey.fromString("bindingboots", this)!!
        val bindingBootsRecipe = ShapedRecipe(bindingBootsRecipeKey, BindingBootsHandler.generate())
        bindingBootsRecipe.shape("sss", "s s", "sss")
        bindingBootsRecipe.setIngredient('s', RecipeChoice.MaterialChoice(Material.STRING))
        Bukkit.addRecipe(bindingBootsRecipe)
        addedRecipes.add(bindingBootsRecipeKey)

        val armorRemoverRecipeKey = NamespacedKey.fromString("armorremover", this)!!
        val armorRemoverRecipe = ShapedRecipe(armorRemoverRecipeKey, ArmorRemoverHandler.generate())
        armorRemoverRecipe.shape("s", "f")
        armorRemoverRecipe.setIngredient('s', RecipeChoice.MaterialChoice(Material.STRING))
        armorRemoverRecipe.setIngredient('f', RecipeChoice.MaterialChoice(Material.FEATHER))
        Bukkit.addRecipe(armorRemoverRecipe)
        addedRecipes.add(armorRemoverRecipeKey)
    }

    override fun onDisable() {
        for(recipeKey in addedRecipes) {
            Bukkit.removeRecipe(recipeKey)
        }
    }
}