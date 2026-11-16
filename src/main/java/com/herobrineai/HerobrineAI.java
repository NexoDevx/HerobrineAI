package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HerobrineAI extends JavaPlugin {

    private static HerobrineAI instance;
    private PlayerMemory playerMemory;
    private MysteriousStructures mysteriousStructures;
    private ScaryEvents scaryEvents;
    private HerobrineBehavior behaviorManager;
    private boolean aiEnabled = true;

    @Override
    public void onEnable() {
        instance = this;

        // Salva a configuração padrão se não existir
        saveDefaultConfig();

        // Inicializa Componentes Principais
        this.playerMemory = new PlayerMemory(this);
        this.mysteriousStructures = new MysteriousStructures(this);
        this.scaryEvents = new ScaryEvents(this);
        this.behaviorManager = new HerobrineBehavior(this, playerMemory, scaryEvents, mysteriousStructures);

        // Registra o Escutador de Eventos
        getServer().getPluginManager().registerEvents(new HerobrineListener(this, playerMemory, behaviorManager), this);

        // Registra Comandos
        HerobrineCommand commandExecutor = new HerobrineCommand(this, playerMemory, scaryEvents, mysteriousStructures);
        if (getCommand("herobrine") != null) {
            getCommand("herobrine").setExecutor(commandExecutor);
            getCommand("herobrine").setTabCompleter(commandExecutor);
        }

        // Carrega memórias de jogadores já online
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerMemory.loadPlayerData(p);
        }

        // Inicia Loops da IA
        behaviorManager.startBehaviorLoop();

        getLogger().info("HerobrineAI ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (behaviorManager != null) {
            behaviorManager.stopBehaviorLoop();
        }

        if (scaryEvents != null) {
            scaryEvents.removeAllApparitions();
        }

        if (playerMemory != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                playerMemory.savePlayerData(p);
            }
            playerMemory.saveData();
        }

        getLogger().info("HerobrineAI desativado com sucesso.");
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
        if (playerMemory != null) {
            playerMemory.loadData();
        }
    }
}
