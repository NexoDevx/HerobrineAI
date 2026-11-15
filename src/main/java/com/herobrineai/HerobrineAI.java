package com.herobrineai;

import org.bukkit.plugin.java.JavaPlugin;

public class HerobrineAI extends JavaPlugin {

    private static HerobrineAI instance;
    private PlayerMemory playerMemory;
    private MysteriousStructures mysteriousStructures;
    private ScaryEvents scaryEvents;
    private HerobrineNpc npcManager;
    private HerobrineBehavior behaviorManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config if not present
        saveDefaultConfig();

        // Initialize Core Components
        this.playerMemory = new PlayerMemory(this);
        this.mysteriousStructures = new MysteriousStructures(this);
        this.scaryEvents = new ScaryEvents(this);
        this.npcManager = new HerobrineNpc(this);
        this.behaviorManager = new HerobrineBehavior(this);

        // Register Event Listener
        getServer().getPluginManager().registerEvents(new HerobrineListener(this), this);

        // Register Commands
        HerobrineCommand commandExecutor = new HerobrineCommand(this);
        if (getCommand("herobrine") != null) {
            getCommand("herobrine").setExecutor(commandExecutor);
            getCommand("herobrine").setTabCompleter(commandExecutor);
        }

        // Start AI Logic Loops
        behaviorManager.startBehaviorLoop();

        getLogger().info("HerobrineAI dynamic horror engine has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (behaviorManager != null) {
            behaviorManager.stopBehaviorLoop();
        }

        if (npcManager != null) {
            npcManager.removeCurrentNpc();
        }

        if (playerMemory != null) {
            playerMemory.saveData();
        }

        getLogger().info("HerobrineAI plugin disabled cleanly and player memory saved.");
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

    public HerobrineNpc getNpcManager() {
        return npcManager;
    }

    public HerobrineBehavior getBehaviorManager() {
        return behaviorManager;
    }
}