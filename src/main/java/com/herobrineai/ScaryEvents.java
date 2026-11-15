package com.herobrineai;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class ScaryEvents {
    private final HerobrineAI plugin;
    private final Random random = new Random();

    public ScaryEvents(HerobrineAI plugin) {
        this.plugin = plugin;
    }

    public void triggerRandomEvent(Player player, int phase) {
        if (player == null || !player.isOnline()) return;

        // Quanto maior a fase, maiores as opções de eventos assustadores disponíveis
        int maxChoice = Math.min(phase * 2, 7);
        int choice = random.nextInt(maxChoice + 1);

        switch (choice) {
            case 0:
            case 1:
                replaceTorches(player);
                break;
            case 2:
                toggleDoors(player);
                break;
            case 3:
                playCreepySounds(player);
                break;
            case 4:
                makeMobsStare(player);
                break;
            case 5:
                strikeLightningNear(player);
                break;
            case 6:
                spawnHerobrineApparition(player);
                break;
            case 7:
                triggerCoordinatedMobEvent(player);
                break;
        }
    }

    // 1. Tochas sendo trocadas por Redstone Torch ou apagadas
    public void replaceTorches(Player player) {
        Location pLoc = player.getLocation();
        World world = pLoc.getWorld();
        int radius = 12;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = world.getBlockAt(pLoc.clone().add(x, y, z));
                    if (block.getType() == Material.TORCH || block.getType() == Material.WALL_TORCH) {
                        if (random.nextBoolean()) {
                            block.setType(block.getType() == Material.TORCH ? Material.REDSTONE_TORCH : Material.REDSTONE_WALL_TORCH);
                        } else {
                            block.setType(Material.AIR);
                        }
                        world.playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 0.5f);
                        return;
                    }
                }
            }
        }
    }

    // 2. Portas e Alçapões abrindo/fechando sozinhos
    public void toggleDoors(Player player) {
        Location pLoc = player.getLocation();
        World world = pLoc.getWorld();
        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = world.getBlockAt(pLoc.clone().add(x, y, z));
                    if (block.getBlockData() instanceof Door door) {
                        door.setOpen(!door.isOpen());
                        block.setBlockData(door);
                        world.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1.0f, 0.6f);
                        return;
                    }
                }
            }
        }
    }

    // 3. Sons de passos e mineração próximos ao jogador
    public void playCreepySounds(Player player) {
        Location loc = player.getLocation().add(random.nextInt(6) - 3, 0, random.nextInt(6) - 3);
        World world = loc.getWorld();

        Sound sound;
        int s = random.nextInt(4);
        if (s == 0) sound = Sound.BLOCK_STONE_STEP;
        else if (s == 1) sound = Sound.BLOCK_STONE_BREAK;
        else if (s == 2) sound = Sound.AMBIENT_CAVE;
        else sound = Sound.ENTITY_ENDERMAN_AMBIENT;

        world.playSound(loc, sound, 1.0f, 0.5f + random.nextFloat() * 0.5f);
    }

    // 4. Mobs parando e olhando fixamente para o jogador
    public void makeMobsStare(Player player) {
        Location pLoc = player.getLocation();
        List<Entity> nearby = player.getNearbyEntities(15, 10, 15);
        for (Entity entity : nearby) {
            if (entity instanceof Mob mob) {
                Location mobLoc = mob.getLocation();
                Vector dir = pLoc.toVector().subtract(mobLoc.toVector()).normalize();
                mobLoc.setDirection(dir);
                mob.teleport(mobLoc);
                mob.setTarget(null); // Para o que estava fazendo e foca a atenção
            }
        }
    }

    // 5. Relâmpago sem dano próximo
    public void strikeLightningNear(Player player) {
        Location loc = player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        loc.getWorld().strikeLightningEffect(loc);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
    }

    // 6. Aparição rápida de Herobrine (ArmorStand estilizado)
    public void spawnHerobrineApparition(Player player) {
        Location pLoc = player.getLocation();
        Vector dir = pLoc.getDirection().normalize().multiply(12);
        Location spawnLoc = pLoc.clone().add(dir);
        spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc));

        World world = spawnLoc.getWorld();
        ArmorStand herobrine = (ArmorStand) world.spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        herobrine.setInvisible(false);
        herobrine.setCustomName(ChatColor.WHITE + "Herobrine");
        herobrine.setCustomNameVisible(false);
        herobrine.setArms(true);

        // Equipando túnica azul (tipo Steve) e cabeça de Herobrine
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner("MHF_Herobrine");
            head.setItemMeta(meta);
        }
        herobrine.getEquipment().setHelmet(head);
        herobrine.getEquipment().setChestplate(new ItemStack(Material.CYAN_TERRACOTTA));

        // Toca som distante
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f);

        // Desaparece após 2.5 segundos ou se o jogador chegar perto
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (herobrine.isValid()) {
                world.spawnParticle(Particle.SMOKE_NORMAL, herobrine.getLocation().add(0, 1, 0), 20, 0.2, 0.5, 0.2, 0.05);
                world.playSound(herobrine.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                herobrine.remove();
            }
        }, 50L); // 2.5s
    }

    // 7. Evento de Mobs Coordenados
    public void triggerCoordinatedMobEvent(Player player) {
        Location pLoc = player.getLocation();
        List<Entity> nearby = player.getNearbyEntities(25, 10, 25);
        for (Entity entity : nearby) {
            if (entity instanceof Mob mob) {
                mob.setTarget(player);
                mob.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, mob.getLocation().add(0, 1, 0), 3);
            }
        }
        player.playSound(pLoc, Sound.ENTITY_WITHER_SPAWN, 0.5f, 0.5f);
    }
}