package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class HerobrineAI extends JavaPlugin {

    private static HerobrineAI instance;
    private PlayerMemory playerMemory;
    private MysteriousStructures mysteriousStructures;
    private ScaryEvents scaryEvents;
    private HerobrineBehavior behaviorManager;
    private HerobrineNpc npcManager;
    private PossessedMobs possessedMobs;
    private boolean aiEnabled = true;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.aiEnabled = getConfig().getBoolean("ai.enabled", true);

        // Inicializar Sistemas
        this.playerMemory = new PlayerMemory(this);
        this.mysteriousStructures = new MysteriousStructures(this);
        this.scaryEvents = new ScaryEvents(this);
        this.npcManager = new HerobrineNpc(this);
        this.behaviorManager = new HerobrineBehavior(this);
        this.possessedMobs = new PossessedMobs(this);

        // Registrar Eventos e Comandos
        getServer().getPluginManager().registerEvents(new HerobrineListener(this, playerMemory, behaviorManager), this);
        
        if (getCommand("herobrine") != null) {
            getCommand("herobrine").setExecutor(new HerobrineCommand(this, playerMemory, scaryEvents, mysteriousStructures));
        }

        getLogger().info("========================================");
        getLogger().info("  HerobrineAI (v1.0.0) ATIVADO COM SUCESSO ");
        getLogger().info("  Lenda, IA Adaptativa e Mobs Possuídos  ");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.removeAll();
        }
        getLogger().info("HerobrineAI foi desativado. Ele continua observando nas sombras...");
    }

    public static HerobrineAI getInstance() {
        return instance;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
        getConfig().set("ai.enabled", aiEnabled);
        saveConfig();
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.aiEnabled = getConfig().getBoolean("ai.enabled", true);
    }

    public PlayerMemory getPlayerMemory() {
        return playerMemory;
    }

    public MysteriousStructures getMysteriousStructures() {
        return mysteriousStructures;
    }

    public ScaryEvents getScaryEvents() {
        return scaryEvents;
    }

    public HerobrineBehavior getBehaviorManager() {
        return behaviorManager;
    }

    public HerobrineNpc getNpcManager() {
        return npcManager;
    }

    public PossessedMobs getPossessedMobs() {
        return possessedMobs;
    }
}
