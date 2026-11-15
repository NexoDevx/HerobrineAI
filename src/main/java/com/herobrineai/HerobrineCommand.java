package com.herobrineai;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HerobrineCommand implements CommandExecutor, TabCompleter {

    private final HerobrineAI plugin;
    private final PlayerMemory memory;
    private final ScaryEvents scaryEvents;
    private final MysteriousStructures structures;

    public HerobrineCommand(HerobrineAI plugin, PlayerMemory memory, ScaryEvents scaryEvents, MysteriousStructures structures) {
        this.plugin = plugin;
        this.memory = memory;
        this.scaryEvents = scaryEvents;
        this.structures = structures;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("herobrineai.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "toggle":
                boolean state = !plugin.isAiEnabled();
                plugin.setAiEnabled(state);
                sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.YELLOW + "IA global " + (state ? ChatColor.GREEN + "ATIVADA" : ChatColor.RED + "DESATIVADA"));
                break;

            case "reload":
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.GREEN + "Configuração e memórias recarregadas com sucesso.");
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /herobrine info <jogador>");
                    return true;
                }
                Player targetInfo = Bukkit.getPlayer(args[1]);
                if (targetInfo == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                    return true;
                }
                sender.sendMessage(ChatColor.DARK_RED + "=== [ Memória do Herobrine: " + targetInfo.getName() + " ] ===");
                sender.sendMessage(ChatColor.YELLOW + "Fase Atual: " + ChatColor.WHITE + memory.getPhase(targetInfo));
                sender.sendMessage(ChatColor.YELLOW + "Encontros Totais: " + ChatColor.WHITE + memory.getEncounters(targetInfo));
                sender.sendMessage(ChatColor.YELLOW + "Perseguições: " + ChatColor.WHITE + memory.getChases(targetInfo));
                sender.sendMessage(ChatColor.YELLOW + "Fugiu: " + ChatColor.WHITE + memory.getFled(targetInfo) + " | Enfrentou: " + memory.getFought(targetInfo) + " | Ignorou: " + memory.getIgnored(targetInfo));
                break;

            case "phase":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /herobrine phase <jogador> <1-5>");
                    return true;
                }
                Player targetPhase = Bukkit.getPlayer(args[1]);
                if (targetPhase == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                    return true;
                }
                try {
                    int p = Integer.parseInt(args[2]);
                    if (p < 1 || p > 5) throw new NumberFormatException();
                    memory.setPhase(targetPhase, p);
                    sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.YELLOW + "Fase de " + targetPhase.getName() + " alterada para " + p);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "A fase deve ser um número entre 1 e 5.");
                }
                break;

            case "spawn":
                Player targetSpawn = args.length > 1 ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
                if (targetSpawn == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não especificado ou offline.");
                    return true;
                }
                scaryEvents.spawnHerobrineApparition(targetSpawn);
                sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.YELLOW + "Aparição do Herobrine forçada em " + targetSpawn.getName());
                break;

            case "event":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /herobrine event <tochas|portas|passos|relampago|mobs> [jogador]");
                    return true;
                }
                Player targetEvt = args.length > 2 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);
                if (targetEvt == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não especificado ou offline.");
                    return true;
                }
                String evtType = args[1].toLowerCase();
                switch (evtType) {
                    case "tochas" -> scaryEvents.replaceTorches(targetEvt);
                    case "portas" -> scaryEvents.toggleDoors(targetEvt);
                    case "passos" -> scaryEvents.playCreepySounds(targetEvt);
                    case "relampago" -> scaryEvents.strikeLightningNear(targetEvt);
                    case "mobs" -> scaryEvents.makeMobsStare(targetEvt);
                    default -> {
                        sender.sendMessage(ChatColor.RED + "Evento inválido. Opções: tochas, portas, passos, relampago, mobs.");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.YELLOW + "Evento '" + evtType + "' executado em " + targetEvt.getName());
                break;

            case "structure":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /herobrine structure <pyramid|tunnel|leafless|sanctuary|dungeon|cobble|sign> [jogador]");
                    return true;
                }
                Player targetStruct = args.length > 2 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);
                if (targetStruct == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não especificado ou offline.");
                    return true;
                }
                String stType = args[1].toLowerCase();
                switch (stType) {
                    case "pyramid" -> structures.createPyramid(targetStruct.getLocation());
                    case "tunnel" -> structures.createTunnel(targetStruct.getLocation());
                    case "leafless" -> structures.createLeaflessForest(targetStruct.getLocation());
                    case "sanctuary" -> structures.buildSanctuary(targetStruct.getLocation());
                    case "dungeon" -> structures.buildDungeon(targetStruct.getLocation());
                    case "cobble" -> structures.buildCobblePillar(targetStruct.getLocation());
                    case "sign" -> structures.placeMysteriousSign(targetStruct.getLocation());
                    default -> {
                        sender.sendMessage(ChatColor.RED + "Estrutura inválida.");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.GOLD + "[HerobrineAI] " + ChatColor.YELLOW + "Estrutura '" + stType + "' gerada em " + targetStruct.getName());
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "=== [ Ajuda - HerobrineAI ] ===");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine toggle " + ChatColor.WHITE + "- Ativa/desativa IA global");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine reload " + ChatColor.WHITE + "- Recarrega as configurações");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine info <jogador> " + ChatColor.WHITE + "- Estatísticas do jogador");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine phase <jogador> <1-5> " + ChatColor.WHITE + "- Altera fase de pavor");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine spawn [jogador] " + ChatColor.WHITE + "- Spawna Herobrine");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine event <tipo> [jogador] " + ChatColor.WHITE + "- Dispara evento assustador");
        sender.sendMessage(ChatColor.YELLOW + "/herobrine structure <tipo> [jogador] " + ChatColor.WHITE + "- Gera estrutura misteriosa");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("toggle", "reload", "info", "phase", "spawn", "event", "structure");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("event")) {
                return Arrays.asList("tochas", "portas", "passos", "relampago", "mobs");
            } else if (args[0].equalsIgnoreCase("structure")) {
                return Arrays.asList("pyramid", "tunnel", "leafless", "sanctuary", "dungeon", "cobble", "sign");
            }
        }
        return new ArrayList<>();
    }
}