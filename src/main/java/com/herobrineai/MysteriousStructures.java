package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Random;

public class MysteriousStructures {

    private final HerobrineAI plugin;
    private final Random random = new Random();

    public MysteriousStructures(HerobrineAI plugin) {
        this.plugin = plugin;
    }

    public void generateStructure(Player player) {
        Location loc = player.getLocation().add(random.nextInt(30) - 15, 0, random.nextInt(30) - 15);
        loc.setY(player.getWorld().getHighestBlockYAt(loc));

        int choice = random.nextInt(4);
        switch (choice) {
            case 0:
                buildPyramid(loc);
                break;
            case 1:
                digTunnel(loc, player);
                break;
            case 2:
                createLeaflessForest(loc);
                break;
            case 3:
                createDarkAltar(loc);
                break;
        }
    }

    private void buildPyramid(Location center) {
        for (int y = 0; y < 4; y++) {
            int radius = 3 - y;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location bLoc = center.clone().add(x, y, z);
                    bLoc.getBlock().setType(Material.SANDSTONE);
                }
            }
        }
        // Topo com tocha de redstone
        Location top = center.clone().add(0, 4, 0);
        top.getBlock().setType(Material.REDSTONE_TORCH);
    }

    private void digTunnel(Location start, Player player) {
        // Túnel 2x2 indo para baixo em direção ao bedrock
        for (int y = 0; y < 15; y++) {
            Location curr = start.clone().subtract(0, y, 0);
            curr.getBlock().setType(Material.AIR);
            curr.clone().add(1, 0, 0).getBlock().setType(Material.AIR);
            curr.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
            curr.clone().add(1, 1, 0).getBlock().setType(Material.AIR);
        }
        start.subtract(0, 15, 0).getBlock().setType(Material.CHEST);
    }

    private void createLeaflessForest(Location center) {
        // Remove folhas em volta e deixa árvores decapitadas / sem folhas
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location col = center.clone().add(x, 0, z);
                col.setY(center.getWorld().getHighestBlockYAt(col));
                Block b = col.getBlock();
                if (b.getType().name().contains("LEAVES")) {
                    b.setType(Material.AIR);
                }
                if (b.getType().name().contains("LOG") && random.nextBoolean()) {
                    b.setType(Material.NETHERRACK);
                }
            }
        }
    }

    private void createDarkAltar(Location center) {
        // Altar com pedra infestada, netherrack em chamas e placa enigmática
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location bLoc = center.clone().add(x, 0, z);
                bLoc.getBlock().setType(Material.INFESTED_STONE);
            }
        }
        Location core = center.clone().add(0, 1, 0);
        core.getBlock().setType(Material.NETHERRACK);
        core.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);

        Location signLoc = center.clone().add(2, 0, 0);
        signLoc.getBlock().setType(Material.OAK_SIGN);
        if (signLoc.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) signLoc.getBlock().getState();
            sign.setLine(0, "He is");
            sign.setLine(1, "Watching");
            sign.setLine(2, "You.");
            sign.update(true);
        }
    }
}
