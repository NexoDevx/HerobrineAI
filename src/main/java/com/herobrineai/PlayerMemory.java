package com.herobrineai;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerMemory {

    public static class PlayerData {
        private final UUID uuid;
        private String name;
        private int encounters = 0;
        private int chases = 0;
        private int fledCount = 0;
        private int foughtCount = 0;
        private int ignoredCount = 0;
        private int phase = 1; // 1: sutil, 2: frequente, 3: agressivo
        private int fearLevel = 0;
        private final Set<String> visitedBiomes = new HashSet<>();
        private long lastEncounterTime = 0;

        public PlayerData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getEncounters() { return encounters; }
        public void setEncounters(int encounters) { this.encounters = encounters; }
        public void incrementEncounters() { this.encounters++; this.lastEncounterTime = System.currentTimeMillis(); }

        public int getChases() { return chases; }
        public void setChases(int chases) { this.chases = chases; }
        public void incrementChases() { this.chases++; }

        public int getFledCount() { return fledCount; }
        public void incrementFled() { this.fledCount++; adjustFear(2); }

        public int getFoughtCount() { return foughtCount; }
        public void incrementFought() { this.foughtCount++; adjustFear(-1); }

        public int getIgnoredCount() { return ignoredCount; }
        public void incrementIgnored() { this.ignoredCount++; }

        public int getPhase() { return phase; }
        public void setPhase(int phase) { this.phase = Math.max(1, Math.min(3, phase)); }

        public int getFearLevel() { return fearLevel; }
        public void setFearLevel(int fearLevel) { this.fearLevel = Math.max(0, Math.min(100, fearLevel)); }
        public void adjustFear(int delta) { setFearLevel(this.fearLevel + delta); }

        public Set<String> getVisitedBiomes() { return visitedBiomes; }
        public void recordBiomeVisit(String biome) { if (biome != null) visitedBiomes.add(biome); }

        public long getLastEncounterTime() { return lastEncounterTime; }
        public void setLastEncounterTime(long time) { this.lastEncounterTime = time; }
    }

    private final HerobrineAI plugin;
    private final Map<UUID, PlayerData> memoryMap = new HashMap<>();
    private File file;
    private YamlConfiguration config;

    public PlayerMemory(HerobrineAI plugin) {
        this.plugin = plugin;
        initFile();
        loadData();
    }

    private void initFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        file = new File(plugin.getDataFolder(), "players_memory.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players_memory.yml!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerData getOrCreate(Player player) {
        return memoryMap.computeIfAbsent(player.getUniqueId(), uuid -> {
            PlayerData data = new PlayerData(uuid, player.getName());
            loadPlayerData(data);
            return data;
        });
    }

    public PlayerData getPlayerData(UUID uuid) {
        return memoryMap.get(uuid);
    }

    // Convenience API Methods
    public void recordBiome(Player player, String biome) {
        getOrCreate(player).recordBiomeVisit(biome);
    }

    public int getEncounters(Player player) {
        return getOrCreate(player).getEncounters();
    }

    public int getChases(Player player) {
        return getOrCreate(player).getChases();
    }

    public int getPhase(Player player) {
        return getOrCreate(player).getPhase();
    }

    public void setPhase(Player player, int phase) {
        getOrCreate(player).setPhase(phase);
    }

    public void recordEncounter(Player player) {
        getOrCreate(player).incrementEncounters();
    }

    public void recordReaction(Player player, String reaction) {
        PlayerData data = getOrCreate(player);
        if ("fled".equalsIgnoreCase(reaction)) {
            data.incrementFled();
        } else if ("fought".equalsIgnoreCase(reaction)) {
            data.incrementFought();
        } else if ("ignored".equalsIgnoreCase(reaction)) {
            data.incrementIgnored();
        }
    }

    public int getFled(Player player) { return getOrCreate(player).getFledCount(); }
    public int getFought(Player player) { return getOrCreate(player).getFoughtCount(); }
    public int getIgnored(Player player) { return getOrCreate(player).getIgnoredCount(); }

    public Map<UUID, PlayerData> getAllData() {
        return Collections.unmodifiableMap(memoryMap);
    }

    public void loadData() {
        if (config == null) return;
        memoryMap.clear();
        if (!config.contains("players")) return;

        for (String uuidStr : config.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                String path = "players." + uuidStr;
                String name = config.getString(path + ".name", "Unknown");

                PlayerData data = new PlayerData(uuid, name);
                data.setEncounters(config.getInt(path + ".encounters", 0));
                data.setChases(config.getInt(path + ".chases", 0));
                data.incrementFled(); // reset count offset
                data.fledCount = config.getInt(path + ".fled", 0);
                data.foughtCount = config.getInt(path + ".fought", 0);
                data.ignoredCount = config.getInt(path + ".ignored", 0);
                data.setPhase(config.getInt(path + ".phase", 1));
                data.setFearLevel(config.getInt(path + ".fearLevel", 0));
                data.setLastEncounterTime(config.getLong(path + ".lastEncounterTime", 0));

                List<String> biomes = config.getStringList(path + ".biomes");
                for (String b : biomes) {
                    data.recordBiomeVisit(b);
                }

                memoryMap.put(uuid, data);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load player data for key: " + uuidStr);
            }
        }
    }

    public void loadPlayerData(PlayerData data) {
        if (config == null || data == null) return;
        String path = "players." + data.getUuid().toString();
        if (config.contains(path)) {
            data.setEncounters(config.getInt(path + ".encounters", 0));
            data.setChases(config.getInt(path + ".chases", 0));
            data.fledCount = config.getInt(path + ".fled", 0);
            data.foughtCount = config.getInt(path + ".fought", 0);
            data.ignoredCount = config.getInt(path + ".ignored", 0);
            data.setPhase(config.getInt(path + ".phase", 1));
            data.setFearLevel(config.getInt(path + ".fearLevel", 0));
            data.setLastEncounterTime(config.getLong(path + ".lastEncounterTime", 0));

            List<String> biomes = config.getStringList(path + ".biomes");
            data.getVisitedBiomes().clear();
            for (String b : biomes) {
                data.recordBiomeVisit(b);
            }
        }
    }

    public void saveData() {
        if (config == null || file == null) return;
        for (Map.Entry<UUID, PlayerData> entry : memoryMap.entrySet()) {
            String path = "players." + entry.getKey().toString();
            PlayerData pd = entry.getValue();

            config.set(path + ".name", pd.getName());
            config.set(path + ".encounters", pd.getEncounters());
            config.set(path + ".chases", pd.getChases());
            config.set(path + ".fled", pd.getFledCount());
            config.set(path + ".fought", pd.getFoughtCount());
            config.set(path + ".ignored", pd.getIgnoredCount());
            config.set(path + ".phase", pd.getPhase());
            config.set(path + ".fearLevel", pd.getFearLevel());
            config.set(path + ".lastEncounterTime", pd.getLastEncounterTime());
            config.set(path + ".biomes", new ArrayList<>(pd.getVisitedBiomes()));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players_memory.yml!");
        }
    }
}