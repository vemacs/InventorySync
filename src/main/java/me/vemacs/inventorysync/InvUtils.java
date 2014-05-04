package me.vemacs.inventorysync;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.Set;

public class InvUtils {
    private InvUtils() {
    }

    private static final JSONParser parser = new JSONParser();

    public static String serializeInventory(Player player) {
        JSONObject json = new JSONObject();
        ItemStack[] inventory = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        json.put("inventory", serializeItemStackArray(inventory));
        json.put("armor", serializeItemStackArray(armor));
        json.put("level", player.getLevel());
        json.put("exp", player.getExp());
        return json.toJSONString();
    }

    public static void deserializeInventory(String serialized, Player player) {
        try {
            JSONObject json = (JSONObject) parser.parse(serialized);
            player.getInventory().setContents(deserializeItemStackArray((String) json.get("inventory")));
            player.getInventory().setArmorContents(deserializeItemStackArray((String) json.get("armor")));
            player.setLevel((int) (long) json.get("level"));
            player.setExp((float) (double) json.get("exp"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String serializeItemStackArray(ItemStack[] toSerialize) {
        JSONObject json = new JSONObject();
        json.put("size", toSerialize.length);
        for (int i = 0; i < toSerialize.length; i++) {
            if (toSerialize[i] == null) continue;
            json.put(i, toSerialize[i].serialize());
        }
        return json.toJSONString();
    }

    public static ItemStack[] deserializeItemStackArray(String serialized) {
        try {
            JSONObject parsed = (JSONObject) parser.parse(serialized);
            ItemStack[] itemArray = new ItemStack[(int) (long) parsed.get("size")];
            for (Map.Entry<?, ?> entry : (Set<Map.Entry<Object, Object>>)parsed.entrySet()) {
                int i = (int) entry.getKey();
                Map<String, Object> s = (Map<String, Object>) entry.getValue();
                if (s == null) continue;
                System.out.println(s.toString());
                itemArray[i] = ItemStack.deserialize(s);
            }
            return itemArray;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
