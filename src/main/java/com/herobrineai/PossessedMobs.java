package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PossessedMobs {
    private final HerobrineAI plugin;
    private final Random random = new Random();

    public PossessedMobs(HerobrineAI plugin) {
        this.plugin = plugin;
        startPossessionTask();
    }

    private void startPossessionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.isAiEnabled()) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (random.nextDouble() < 0.15) { // 15% de chance a cada ciclo por jogador
                        possessNearbyMobs(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L * 30L, 20L * 45L); // A cada 45 segundos
    }

    public void possessNearbyMobs(Player player) {
        Location loc = player.getLocation();
        double radius = 25.0;

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getLocation().distanceSquared(loc) <= radius * radius) {
                if (entity instanceof Monster || entity instanceof Animals) {
                    applyPossessionEffects(entity, player);
                }
            }
        }
    }

    private void applyPossessionEffects(Entity entity, Player player) {
        // Efeitos visuais e sonoros de possessão pelo Herobrine
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(Particle.SOUL, loc.add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.05);
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 5, 0.2, 0.2, 0.2, 0.01);

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            // Efeito de força, velocidade e glow / olhos brancos (simulado com poções)
            living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1, false, false));
            living.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, false, false));
            living.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));

            if (living instanceof Mob) {
                Mob mob = (Mob) living;
                mob.setTarget(player); // Mob possuído agora persegue o jogador implacavelmente!
            }

            // Som assustador ambiente leve
            player.playSound(loc, Sound.ENTITY_ENDERMAN_STARE, 0.5f, 0.3f);
        }
    }

    public void triggerMassPossession(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "[Herobrine] Os olhos dos animais e monstros ao seu redor brilham em branco...");
        player.sendTitle(ChatColor.DARK_RED + "ELES SERVEM A ELE", ChatColor.GRAY + "Fuja enquanto pode...", 10, 70, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3f, 0.5f);

        for (Entity entity : player.getNearbyEntities(30, 15, 30)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                applyPossessionEffects(entity, player);
            }
        }
    }
}
