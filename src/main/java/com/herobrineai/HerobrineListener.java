package com.herobrineai;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HerobrineListener implements Listener {

    private final HerobrineAI plugin;
    private final PlayerMemory memory;
    private final HerobrineBehavior behavior;

    public HerobrineListener(HerobrineAI plugin, PlayerMemory memory, HerobrineBehavior behavior) {
        this.plugin = plugin;
        this.memory = memory;
        this.behavior = behavior;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        memory.loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        memory.savePlayerData(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) event.getEntity();
            if (stand.getCustomName() != null && stand.getCustomName().contains("Herobrine")) {
                event.setCancelled(true);
                Player player = (Player) event.getDamager();
                behavior.handlePlayerReaction(player, "FOUGHT");
                stand.remove();
                player.sendMessage("§cO Herobrine se desfez em fumaça antes que seu golpe atingisse!");
            }
        }
    }
}