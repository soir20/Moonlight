package net.mehvahdjukaar.selene.resourcepack.recipe;

import com.google.gson.JsonObject;
import net.mehvahdjukaar.selene.block_set.BlockType;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public interface IRecipeTemplate<R extends FinishedRecipe> {

    <T extends BlockType> R createSimilar(T originalMat, T destinationMat, Item unlockItem, @Nullable String id);

    default <T extends BlockType> R createSimilar(T originalMat, T destinationMat, Item unlockItem) {
        return createSimilar(originalMat, destinationMat, unlockItem, null);
    }

    static IRecipeTemplate<?> read(JsonObject recipe) throws UnsupportedOperationException {
        String type = GsonHelper.getAsString(recipe, "type");
        RecipeSerializer<?> s = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(type));
        if (s == RecipeSerializer.SHAPED_RECIPE) {
            return ShapedRecipeTemplate.fromJson(recipe);
        } else if (s == RecipeSerializer.SHAPELESS_RECIPE) {
            return ShapelessRecipeTemplate.fromJson(recipe);
        } else if (s == RecipeSerializer.STONECUTTER) {
            return StoneCutterRecipeTemplate.fromJson(recipe);
        }
        throw new UnsupportedOperationException(String.format("Invalid recipe serializer: %s. Must be either shaped, shapeless or stonecutting", s));
    }

}
