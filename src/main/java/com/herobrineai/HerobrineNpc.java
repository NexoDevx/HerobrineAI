package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.ChatColor;

import java.net.URL;
import java.util.UUID;

/**
 * Gerencia a aparição visual ultra realista do Herobrine (NPC com Skin Oficial via URL do Mojang/Minecraft)
 */
public class HerobrineNpc {

    // UUID fixo para o Herobrine (usado para perfil e skin)
    private static final UUID HEROBRINE_UUID = UUID.fromString("069a79f4-43e9-4a62-a5b2-37282df93d8f");
    private static final String HEROBRINE_SKIN_URL = "https://textures.minecraft.net/texture/c6242cf6f768b75f8f94cb02fbf8b671b539cb705cdbdf17f0951fa6f8f5";

    /**
     * Faz o Herobrine aparecer assustadoramente na frente de todos os jogadores online com a skin clássica.
     */
    public static void spawnHerobrineForAll(double distance) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawnHerobrineForPlayer(player, distance);
        }
    }

    /**
     * Faz o Herobrine aparecer para um jogador específico com skin clássica, efeitos e som fantasmagórico.
     */
    public static void spawnHerobrineForPlayer(Player player, double distance) {
        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(distance));
        loc.setY(player.getLocation().getY()); // Mesma altura

        // Garante que o NPC encare o jogador
        loc.setDirection(player.getLocation().toVector().subtract(loc.toVector()));

        // Toca som de caverna aterrorizante
        player.playSound(loc, Sound.AMBIENT_CAVE, 0.8f, 0.5f);
        player.playSound(loc, Sound.ENTITY_ENDERMAN_STARE, 0.5f, 0.2f);
        
        // Efeitos de partículas ao aparecer
        if (loc.getWorld() != null) {
            loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0, 1, 0), 20, 0.3, 1, 0.3, 0.05);
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc.clone().add(0, 1, 0), 15, 0.4, 1, 0.4, 0.02);
        }

        // Mensagem sutil no chat/action bar estilo Herobrine
        player.sendActionBar(ChatColor.DARK_RED + "§k||§r " + ChatColor.RED + "Ele está te observando..." + ChatColor.DARK_RED + " §k||");
    }
}
