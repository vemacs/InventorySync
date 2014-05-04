package me.vemacs.inventorysync;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ItemStack.deserialize(jsonDeserializationContext.<Map<String, Object>>deserialize(jsonElement, new TypeToken<Map<String, Object>>(){}.getType()));
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(itemStack.serialize());
    }
}
