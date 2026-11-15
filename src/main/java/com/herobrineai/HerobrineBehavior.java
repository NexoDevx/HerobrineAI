package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class HerobrineBehavior {
    private final HerobrineAI plugin;
    private final PlayerMemory memory;
    private final MysteriousStructures structures;
    private final ScaryEvents events;
    private final Random random = new Random();

    public HerobrineBehavior(HerobrineAI plugin, PlayerMemory memory, MysteriousStructures structures, ScaryEvents events) {
        this.plugin = plugin;
        this.memory = memory;
        this.structures = structures;
        this.events = events;
    }

    public void startBehaviorLoop() {
        // Executa a cada 200 ticks (10 segundos)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.isAiEnabled()) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    processPlayerAI(player);
                }
            }
        }.runTaskTimer(plugin, 100L, 200L);
    }

    private void processPlayerAI(Player player) {
        // Atualiza estatísticas do jogador
        memory.recordBiome(player, player.getLocation().getBlock().getBiome().name());

        int encounters = memory.getEncounters(player);
        int phase = calculatePhase(encounters);
        memory.setPhase(player, phase);

        // Chance base do Herobrine interagir nesta iteração
        double chance = 0.15 + (phase * 0.05);
        if (random.nextDouble() > chance) return;

        // Decidir se gera evento assustador ou estrutura misteriosa
        if (random.nextBoolean()) {
            events.triggerRandomEvent(player, phase);
            memory.recordEncounter(player);
        } else if (phase >= 3) {
            triggerStructureGeneration(player, phase);
        }
    }

    private int calculatePhase(int encounters) {
        if (encounters < 5) return 1;       // Fase 1: Observação
        if (encounters < 15) return 2;      // Fase 2: Curiosidade
        if (encounters < 30) return 3;      // Fase 3: Perseguição
        if (encounters < 50) return 4;      // Fase 4: Manipulação
        return 5;                           // Fase 5: Hostilidade
    }

    private void triggerStructureGeneration(Player player, int phase) {
        Location loc = player.getLocation();
        int choice = random.nextInt(6);

        switch (choice) {
            case 0 -> structures.createPyramid(loc);
            case 1 -> structures.createTunnel(loc);
            case 2 -> structures.createLeaflessForest(loc);
            case 3 -> {
                if (phase >= 4) structures.buildSanctuary(loc);
            }
            case 4 -> {
                if (phase >= 4) structures.buildDungeon(loc);
            }
            case 5 -> structures.placeMysteriousSign(loc);
        }
    }

    // Chamado quando o jogador encara/olha ou ataca
    public void handlePlayerReaction(Player player, String action) {
        if (action.equalsIgnoreCase("FLED")) {
            memory.recordReaction(player, "fled");
        } else if (action.equalsIgnoreCase("FOUGHT")) {
            memory.recordReaction(player, "fought");
        } else {
            memory.recordReaction(player, "ignored");
        }
    }
}