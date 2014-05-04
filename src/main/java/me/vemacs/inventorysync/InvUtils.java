package me.vemacs.inventorysync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InvUtils {
    private InvUtils() {
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .create();

    public static String serializeInventory(Player player) {
        return gson.toJson(SerializedInventory.create(player));
    }

    public static void deserializeInventory(String serialized, Player player) {
        SerializedInventory inventory = gson.fromJson(serialized, SerializedInventory.class);
        player.getInventory().setArmorContents(inventory.armor);
        player.getInventory().setContents(inventory.inventory);
        player.setLevel(inventory.level);
        player.setExp(inventory.xp);
    }

    private static class SerializedInventory {
        private ItemStack[] armor;
        private ItemStack[] inventory;
        private int level;
        private float xp;

        public static SerializedInventory create(Player player) {
            SerializedInventory serializedInventory = new SerializedInventory();
            serializedInventory.armor = player.getInventory().getArmorContents();
            serializedInventory.inventory = player.getInventory().getContents();
            serializedInventory.level = player.getLevel();
            serializedInventory.xp = player.getExp();
            return serializedInventory;
        }
    }
}
