package com.uniocraft.UnioBungee.managers;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    private Main plugin;

    public Map<ServerInfo, List<ProxiedPlayer>> queue = new LinkedHashMap<>();
    public List<ServerInfo> serverInfos = new ArrayList<>();
    public Set<ProxiedPlayer> joining = new LinkedHashSet<>();
    public boolean queueActive = true;
    public boolean debug = false;

    public QueueManager(Main plugin) {
        this.plugin = plugin;

        for (String server : plugin.config.getStringList("QueueServers")) {
            if (plugin.getProxy().getServerInfo(server) != null) {
                serverInfos.add(plugin.getProxy().getServerInfo(server));
            } else {
                plugin.getLogger().severe("There is no server named " + server + "!");
            }
        }

        for (ServerInfo server : serverInfos) {
            queue.put(server, new ArrayList<>());
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + server.getName() + " isimli sunucu queue listesine eklendi.");
            }
        }

        processQueue();
    }

    private void processQueue() {
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            /*if (debug) {
                plugin.getLogger().warning("[DEBUG] Queue işleniyor. Zaman: " + System.currentTimeMillis());
            }*/
            for (ServerInfo server : serverInfos) {
                /*if (debug) {
                    plugin.getLogger().warning("[DEBUG] " + server.getName() + " queue'sü işleniyor. Zaman: " + System.currentTimeMillis());
                }*/
                int i = 0;
                for (ProxiedPlayer player : new ArrayList<>(queue.get(server))) {
                    i++;
                    if (i > 5) {
                        if (debug) {
                            plugin.getLogger().warning("[DEBUG] Sıra işlendi. " + player.getName() + " sunucuya giremedi. Server: " + server.getName() + " Kod: 1 i: " + i + " Zaman: " + System.currentTimeMillis());
                        }
                        player.sendMessage(Main.prefix + ChatColor.GOLD + server.getName() + " sunucusuna giriş sırasındasınız. Sıranız: " + ChatColor.GOLD + ChatColor.BOLD + getQueueIndex(player, server) + "/" + getTotalQueue(server));
                        continue;
                    }

                    if (player.getServer().getInfo().equals(server)) {
                        removePlayer(player);
                        continue;
                    }

                    joining.add(player);
                    final int iCopy = i;

                    player.connect(server, (joined, throwable) -> {
                        if (joined) {
                            removePlayer(player);
                            if (debug) {
                                plugin.getLogger().warning("[DEBUG] Sıra işlendi. " + player.getName() + " sunucuya girebildi. Server: " + server.getName() + " i: " + iCopy + " Zaman: " + System.currentTimeMillis());
                            }
                        } else {
                            if (debug) {
                                plugin.getLogger().warning("[DEBUG] Sıra işlendi. " + player.getName() + " sunucuya giremedi. Server: " + server.getName() + " Kod: 2 i: " + iCopy + " Zaman: " + System.currentTimeMillis());
                            }
                        }
                        joining.remove(player);
                    });
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    public boolean connectServer(ProxiedPlayer player, ServerInfo server) {
        if (joining.contains(player)) return false;
        if (player.getServer() != null && player.getServer().getInfo().equals(server)) return false;

        if (!serverInfos.contains(server)) {
            if (removePlayer(player)) {
                player.sendMessage(Main.prefix + ChatColor.GOLD + "Başka bir sunucuya girdiğiniz için kuyruktan çıkarıldınız.");
            }
            return false;
        }

        int queueStatus = queueStatus(player, server);

        if (queueStatus == 1) {
            player.sendMessage(Main.prefix + ChatColor.GOLD + "Zaten sunucuya giriş sırasındasınız. Sıranız: " + getQueueIndex(player, server) + "/" + getTotalQueue(server));
            return true;
        }

        if (queueStatus == 2) {
            removePlayer(player);
            addToQueue(player, server);
            player.sendMessage(Main.prefix + ChatColor.GOLD + "Kuyruktan silindiniz ve " + server.getName() + " sunucusuna girmek için sıraya girdiniz. Sıranız: " + ChatColor.GOLD + ChatColor.BOLD + getQueueIndex(player, server) + "/" + getTotalQueue(server));
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + player.getName() + " kuyruktan silindi ve yeni sıraya girdi. Server: " + server.getName() + " Zaman: " + System.currentTimeMillis());
            }
        }

        if (queueStatus == 3) {
            addToQueue(player, server);
            player.sendMessage(Main.prefix + ChatColor.GOLD + server.getName() + " sunucusuna girmek için sıraya girdiniz. Sıranız: " + ChatColor.GOLD + ChatColor.BOLD + getQueueIndex(player, server) + "/" + getTotalQueue(server));
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + player.getName() + " sıraya girdi. Server: " + server.getName() + " Zaman: " + System.currentTimeMillis());
            }
        }
        return true;
    }

    private void addToQueue(ProxiedPlayer player, ServerInfo server) {
        if (!queue.get(server).contains(player)) {
            queue.get(server).add(player);
        }
    }

    public boolean removePlayer(ProxiedPlayer player) {
        if (debug) {
            plugin.getLogger().warning("[DEBUG] " + player.getName() + " tüm sıralardan siliniyor. Zaman: " + System.currentTimeMillis());
        }
        for (ServerInfo server : queue.keySet()) {
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncunun " + server.getName() + " sırasından silinmesi deneniyor. Zaman: " + System.currentTimeMillis());
            }
            if (queue.get(server).contains(player)) {
                queue.get(server).remove(player);
                if (debug) {
                    plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncunun " + server.getName() + " sırasından silindi. Zaman: " + System.currentTimeMillis());
                }
                return true;
            }
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncu " + server.getName() + " sırasından silinemedi. Zaman: " + System.currentTimeMillis());
            }
        }
        if (debug) {
            plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncu sıradan silinemedi. Zaman: " + System.currentTimeMillis());
        }
        return false;
    }

    private int queueStatus(ProxiedPlayer player, ServerInfo server) {
        if (queue.get(server).contains(player)) {
            if (debug) {
                plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncu " + server.getName() + " sırasında mevcut. Zaman: " + System.currentTimeMillis());
            }
            return 1; // In queue for given server
        }

        for (ServerInfo serverInfo : queue.keySet()) {
            if (queue.get(serverInfo).contains(player)) {
                if (debug) {
                    plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncu " + server.getName() + " sırasında mevcut değil ama başka bir sırada mevcut. Zaman: " + System.currentTimeMillis());
                }
                return 2; // In queue for another server
            }
        }
        if (debug) {
            plugin.getLogger().warning("[DEBUG] " + player.getName() + " isimli oyuncu " + server.getName() + " sırasında mevcut değil. Başka bir yerde de değil. Zaman: " + System.currentTimeMillis());
        }
        return 3; // Not in queue
    }

    private int getQueueIndex(ProxiedPlayer player, ServerInfo server) {
        return queue.get(server).indexOf(player) + 1;
    }

    private int getTotalQueue(ServerInfo server) {
        int queuesize = 0;
        for (ProxiedPlayer ignored : queue.get(server)) {
            queuesize++;
        }
        if (debug) {
            plugin.getLogger().warning("[DEBUG] Hesaplanan queue size: " + queuesize + " queue.get(server).size(): " + queue.get(server).size() + ". Zaman: " + System.currentTimeMillis());
        }
        return queue.get(server).size();
    }
}
