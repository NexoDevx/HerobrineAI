package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MysteriousStructures {
    private final HerobrineAI plugin;
    private final Random random = new Random();

    private final List<String> MYSTERIOUS_MESSAGES = Arrays.asList(
            "Estou observando",
            "Não olhe para trás",
            "Você voltou",
            "Herobrine vê você",
            "Saia daqui",
            "Ele está logo atrás",
            "Ninguém vai te salvar",
            "404 Not Found"
    );

    public MysteriousStructures(HerobrineAI plugin) {
        this.plugin = plugin;
    }

    public boolean isAreaProtected(Location loc) {
        if (loc == null || loc.getWorld() == null) return true;
        Location spawn = loc.getWorld().getSpawnLocation();
        int protectionRadius = plugin.getConfig().getInt("settings.spawn-protection-radius", 100);
        if (loc.distanceSquared(spawn) < protectionRadius * protectionRadius) {
            return true;
        }
        return false;
    }

    public void generateRandomStructure(Player player) {
        if (player == null || isAreaProtected(player.getLocation())) return;

        int choice = random.nextInt(8);
        switch (choice) {
            case 0: buildSandPyramid(player.getLocation()); break;
            case 1: build2x2Tunnel(player.getLocation()); break;
            case 2: createLeaflessForest(player.getLocation()); break;
            case 3: buildSanctuary(player.getLocation()); break;
            case 4: buildDungeon(player.getLocation()); break;
            case 5: buildCobblePillar(player.getLocation()); break;
            case 6: modifyTrees(player.getLocation()); break;
            case 7: placeMysteriousSign(player.getLocation()); break;
        }
    }

    // 1. Pirâmide de areia
    public boolean buildSandPyramid(Location center) {
        Location target = center.clone().add(random.nextInt(30) - 15, 0, random.nextInt(30) - 15);
        target.setY(target.getWorld().getHighestBlockYAt(target));
        if (isAreaProtected(target)) return false;

        World world = target.getWorld();
        int baseSize = 5;
        for (int level = 0; level < 3; level++) {
            int radius = (baseSize / 2) - level;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location bLoc = target.clone().add(x, level, z);
                    world.getBlockAt(bLoc).setType(Material.SANDSTONE);
                }
            }
        }
        Location top = target.clone().add(0, 3, 0);
        world.getBlockAt(top).setType(Material.REDSTONE_TORCH);
        plugin.getLogger().info("Pirâmide de areia gerada em " + target.getBlockX() + ", " + target.getBlockZ());
        return true;
    }

    // 2. Túneis 2x2
    public boolean build2x2Tunnel(Location center) {
        Location target = center.clone().add(random.nextInt(20) - 10, random.nextInt(10) - 5, random.nextInt(20) - 10);
        if (isAreaProtected(target)) return false;

        World world = target.getWorld();
        BlockFace direction = BlockFace.values()[random.nextInt(4)];
        int length = 10 + random.nextInt(8);

        Location curr = target.clone();
        for (int i = 0; i < length; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dy = 0; dy < 2; dy++) {
                    Location bLoc = curr.clone().add(dx, dy, 0);
                    world.getBlockAt(bLoc).setType(Material.AIR);
                }
            }
            curr.add(direction.getModX(), 0, direction.getModZ());
        }
        // Adiciona tocha de redstone no final do túnel
        curr.add(0, 1, 0);
        world.getBlockAt(curr).setType(Material.REDSTONE_TORCH);
        return true;
    }

    // 3. Floresta sem folhas
    public boolean createLeaflessForest(Location center) {
        if (isAreaProtected(center)) return false;
        World world = center.getWorld();
        int radius = 15;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -5; y <= 15; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(loc);
                    if (block.getType().name().contains("LEAVES")) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        return true;
    }

    // 4. Santuário com ouro e netherrack
    public boolean buildSanctuary(Location center) {
        Location target = center.clone().add(random.nextInt(40) - 20, 0, random.nextInt(40) - 20);
        target.setY(target.getWorld().getHighestBlockYAt(target));
        if (isAreaProtected(target)) return false;

        World world = target.getWorld();
        // Base 3x3 Netherrack
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.getBlockAt(target.clone().add(x, 0, z)).setType(Material.NETHERRACK);
            }
        }
        // Centro ouro com fogo em cima
        world.getBlockAt(target.clone().add(0, 1, 0)).setType(Material.GOLD_BLOCK);
        world.getBlockAt(target.clone().add(0, 2, 0)).setType(Material.FIRE);

        // Tochas de redstone nos 4 cantos
        world.getBlockAt(target.clone().add(-1, 1, -1)).setType(Material.REDSTONE_TORCH);
        world.getBlockAt(target.clone().add(1, 1, -1)).setType(Material.REDSTONE_TORCH);
        world.getBlockAt(target.clone().add(-1, 1, 1)).setType(Material.REDSTONE_TORCH);
        world.getBlockAt(target.clone().add(1, 1, 1)).setType(Material.REDSTONE_TORCH);
        return true;
    }

    // 5. Masmorra abandonada de cobblestone
    public boolean buildDungeon(Location center) {
        Location target = center.clone().add(random.nextInt(30) - 15, -10, random.nextInt(30) - 15);
        if (target.getY() < 10) target.setY(20);
        if (isAreaProtected(target)) return false;

        World world = target.getWorld();
        int size = 5;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < size; z++) {
                    Location bLoc = target.clone().add(x, y, z);
                    if (x == 0 || x == size - 1 || z == 0 || z == size - 1 || y == 0 || y == 3) {
                        Material mat = random.nextBoolean() ? Material.COBBLESTONE : Material.MOSSY_COBBLESTONE;
                        world.getBlockAt(bLoc).setType(mat);
                    } else {
                        world.getBlockAt(bLoc).setType(Material.AIR);
                    }
                }
            }
        }
        // Placa no centro da sala
        Location signLoc = target.clone().add(2, 1, 2);
        placeSignAt(signLoc);
        return true;
    }

    // 6. Construções de cobblestone (Pilares misteriosos)
    public boolean buildCobblePillar(Location center) {
        Location target = center.clone().add(random.nextInt(30) - 15, 0, random.nextInt(30) - 15);
        target.setY(target.getWorld().getHighestBlockYAt(target));
        if (isAreaProtected(target)) return false;

        World world = target.getWorld();
        int height = 4 + random.nextInt(4);
        for (int h = 0; h < height; h++) {
            Material mat = random.nextBoolean() ? Material.COBBLESTONE : Material.MOSSY_COBBLESTONE;
            world.getBlockAt(target.clone().add(0, h, 0)).setType(mat);
        }
        world.getBlockAt(target.clone().add(0, height, 0)).setType(Material.REDSTONE_TORCH);
        return true;
    }

    // 7. Árvores modificadas (Cortes e furos)
    public boolean modifyTrees(Location center) {
        if (isAreaProtected(center)) return false;
        World world = center.getWorld();
        int count = 0;
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location loc = center.clone().add(x, 0, z);
                loc.setY(world.getHighestBlockYAt(loc));
                Block b = world.getBlockAt(loc);
                if (b.getType().name().contains("LOG") || b.getType().name().contains("WOOD")) {
                    world.getBlockAt(loc.clone().add(0, 1, 0)).setType(Material.AIR);
                    count++;
                }
            }
        }
        return count > 0;
    }

    // 8. Placa misteriosa
    public boolean placeMysteriousSign(Location center) {
        Location target = center.clone().add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
        target.setY(target.getWorld().getHighestBlockYAt(target));
        if (isAreaProtected(target)) return false;

        placeSignAt(target);
        return true;
    }

    private void placeSignAt(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.OAK_SIGN);
        if (block.getState() instanceof Sign sign) {
            String msg = MYSTERIOUS_MESSAGES.get(random.nextInt(MYSTERIOUS_MESSAGES.size()));
            try {
                // Suporte para Paper 1.20+ ou Spigot API
                SignSide side = sign.getSide(Side.FRONT);
                side.setLine(1, msg);
            } catch (Throwable t) {
                sign.setLine(1, msg);
            }
            sign.update();
        }
    }
}