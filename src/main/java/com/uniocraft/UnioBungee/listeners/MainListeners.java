package com.uniocraft.UnioBungee.listeners;

import com.google.common.base.Charsets;
import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MainListeners implements Listener {

    private Main plugin;
    private static final Map<Connection, Long> PACKET_USAGE = new ConcurrentHashMap<>();
    private static final Map<Connection, AtomicInteger> CHANNELS_REGISTERED = new ConcurrentHashMap<>();

    public MainListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void Konusuldugunda(ChatEvent olay) {
        if (olay.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer oyuncu = (ProxiedPlayer) olay.getSender();
            if (olay.getMessage().contains("̇")) {
                olay.setCancelled(true);
                oyuncu.sendMessage("§2[§bUnioCraft§2] §cHatalı bir mesaj girdiniz.");
            }
        }
    }

    @EventHandler
    public void onPreLoginEvent(PreLoginEvent event) {
        String player = event.getConnection().getName();
        ProxiedPlayer oyuncu = ProxyServer.getInstance().getPlayer(player);
        if (oyuncu != null) {
            event.setCancelReason("§4Sunucuya bağlantı başarısız. \n§4Sebep: §cSunucuya zaten bağlısınız.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer) || e.isCancelled()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        String message = e.getMessage().toLowerCase();

        if (e.isCommand()) {
            String[] cmd = message.substring(1).split(" ", 2);

            if (cmd[0].equalsIgnoreCase("skinguncelle")) {
                if (player.getServer().getInfo().getName().equalsIgnoreCase("Girislobi")) {
                    e.setCancelled(true);
                    player.sendMessage("§cBu komutu giriş lobide kullanamazsınız.");
                }
            }
            if (cmd[0].equalsIgnoreCase("skinupdate")) {
                player.sendMessage("§cBu komut değiştirilmiştir. Lütfen §a/skinguncelle §ckomutunu kullanınız.");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPacket(PluginMessageEvent event) {
        String name = event.getTag();
        if (!"MC|BSign".equals(name) && !"MC|BEdit".equals(name) && !"REGISTER".equals(name))
            return;

        Connection connection = event.getSender();
        if (!(connection instanceof ProxiedPlayer))
            return;

        try {
            if ("REGISTER".equals(name)) {
                if (!CHANNELS_REGISTERED.containsKey(connection))
                    CHANNELS_REGISTERED.put(connection, new AtomicInteger());

                for (int i = 0; i < new String(event.getData(), Charsets.UTF_8).split("\0").length; i++)
                    if (CHANNELS_REGISTERED.get(connection).incrementAndGet() > 124)
                        throw new IOException("Too many channels");
            } else {
                if (elapsed(PACKET_USAGE.getOrDefault(connection, -1L), 100L)) {
                    PACKET_USAGE.put(connection, System.currentTimeMillis());
                } else {
                    throw new IOException("Packet flood");
                }
            }
        } catch (Throwable ex) {
            connection.disconnect(TextComponent.fromLegacyText("Hatalı bir paket gönderdiniz!"));

            plugin.getLogger().warning(connection.getAddress() + " tried to exploit CustomPayload: " + ex.getMessage());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        CHANNELS_REGISTERED.remove(event.getPlayer());
        PACKET_USAGE.remove(event.getPlayer());
    }

    private boolean elapsed(long from, long required) {
        return from == -1L || System.currentTimeMillis() - from > required;
    }

    private String color(String s) {
        s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent e) {
        Connection p = e.getSender();
        if (("WDL|INIT".equalsIgnoreCase(e.getTag())) && ((e.getSender() instanceof ProxiedPlayer))) {
            p.disconnect(new TextComponent(color("&cWorldDownloader kullanmak yasaktır!")));
        }
        if (("PERMISSIONSREPL".equalsIgnoreCase(e.getTag())) && (new String(e.getData()).contains("mod.worlddownloader"))) {
            p.disconnect(new TextComponent(color("&cWorldDownloader kullanmak yasaktır!")));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        byte[] payload = getPayload(player);
        if (payload != null) {
            player.sendData("schematica", payload);
        }
    }

    private byte[] getPayload(ProxiedPlayer player) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeByte(0);
            dataOutputStream.writeBoolean(player.hasPermission("schematica.printer"));
            dataOutputStream.writeBoolean(player.hasPermission("schematica.save"));
            dataOutputStream.writeBoolean(player.hasPermission("schematica.load"));

            return byteArrayOutputStream.toByteArray();
        } catch (IOException ioe) {
            plugin.getLogger().throwing("SchematicaPlugin", "getPayload", ioe);
        }
        return null;
    }
}
