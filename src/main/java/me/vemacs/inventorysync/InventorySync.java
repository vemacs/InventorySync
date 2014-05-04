package me.vemacs.inventorysync;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

public class InventorySync extends JavaPlugin implements Listener {
    private JedisPool pool;
    private static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        String ip = getConfig().getString("ip");
        int port = getConfig().getInt("port");
        String password = getConfig().getString("password");
        if (password == null || password.equals(""))
            pool = new JedisPool(new JedisPoolConfig(), ip, port, 0);
        else
            pool = new JedisPool(new JedisPoolConfig(), ip, port, 0, password);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final String name = event.getPlayer().getName();
        getServer().getScheduler().runTaskAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = pool.getResource();
                try {
                    if (jedis.exists(uuid.toString())) {
                        final String serialized = jedis.get(uuid.toString());
                        Bukkit.getScheduler().runTask(InventorySync.instance, new BukkitRunnable() {
                            @Override
                            public void run() {
                                InvUtils.deserializeInventory(serialized, Bukkit.getPlayerExact(name));
                            }
                        });
                    }
                } catch (Exception e) {
                    pool.returnBrokenResource(jedis);
                    return;
                }
                pool.returnResource(jedis);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveToRedis(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        saveToRedis(event.getPlayer());
    }

    private void saveToRedis(Player player) {
        final UUID uuid = player.getUniqueId();
        final String serialized = InvUtils.serializeInventory(player);
        getServer().getScheduler().runTaskAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = pool.getResource();
                try {
                    jedis.set(uuid.toString(), serialized);
                } catch (Exception e) {
                    pool.returnBrokenResource(jedis);
                    return;
                }
                pool.returnResource(jedis);
            }
        });
    }
}

