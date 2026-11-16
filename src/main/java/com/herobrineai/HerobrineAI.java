package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class HerobrineAI extends JavaPlugin {

    private static HerobrineAI instance;
    private PlayerMemory playerMemory;
    private MysteriousStructures mysteriousStructures;
    private ScaryEvents scaryEvents;
    private HerobrineBehavior behaviorManager;
    private boolean aiEnabled = true;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Inicializa sistemas
        this.playerMemory = new PlayerMemory(this);
        this.mysteriousStructures = new MysteriousStructures(this);
        this.scaryEvents = new ScaryEvents(this);
        this.behaviorManager = new HerobrineBehavior(this);

        // Registra Eventos
        getServer().getPluginManager().registerEvents(new HerobrineListener(this, playerMemory, behaviorManager), this);

        // Registra Comandos
        if (getCommand("herobrine") != null) {
            getCommand("herobrine").setExecutor(new HerobrineCommand(this, playerMemory, scaryEvents, mysteriousStructures));
        }

        // Inicia Loop de Comportamento da IA
        behaviorManager.startBehaviorLoop();

        getLogger().info(ChatColor.DARK_RED + "========================================");
        getLogger().info(ChatColor.DARK_RED + "   HerobrineAI ATIVADO com sucesso!");
        getLogger().info(ChatColor.DARK_RED + "   Skin Clássica e IA Comportamental Prontas.");
        getLogger().info(ChatColor.DARK_RED + "========================================");
    }

    @Override
    public void onDisable() {
        if (playerMemory != null) {
            playerMemory.saveAll();
        }
        getLogger().info("HerobrineAI desativado. Memórias salvas.");
    }

    public static HerobrineAI getInstance() {
        return instance;
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

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public void reloadPluginConfig() {
        reloadConfig();
    }
}
