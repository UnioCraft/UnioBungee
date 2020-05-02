package com.uniocraft.UnioBungee.listeners;

import com.github.games647.changeskin.bungee.ChangeSkinBungee;
import com.github.games647.changeskin.bungee.task.NameResolver;
import com.github.games647.changeskin.bungee.task.SkinApplier;
import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class SkinListeners implements Listener {

    private Main plugin;
    private ChangeSkinBungee changeSkinPlugin;

    public SkinListeners(Main plugin) {
        this.plugin = plugin;
        this.changeSkinPlugin = (ChangeSkinBungee) ProxyServer.getInstance().getPluginManager().getPlugin("ChangeSkin");
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer p = event.getPlayer();
        String playerName = p.getName();

        /* DEBUG
        long timeNow = System.currentTimeMillis();
        ProxyServer.getInstance().getLogger().log(Level.INFO, playerName + " için skin kontrolü başladı.");
         */

        ArrayList<String> textureAndSignature = plugin.sqlManager.getSkinOfWaitingListPlayer(playerName);

        if (textureAndSignature != null) {
            if (textureAndSignature.size() == 2) {
                String texture = textureAndSignature.get(0);
                String signature = textureAndSignature.get(1);

                SkinModel skinModel = SkinModel.createSkinFromEncoded(texture, signature);

                Runnable task = new SkinApplier(changeSkinPlugin, plugin.getProxy().getConsole(), p, skinModel, false, true);
                ProxyServer.getInstance().getScheduler().runAsync(plugin, task);
                p.sendMessage(Main.prefix + "Tebrikler! Skininiz değiştirildi. Oyundan çıkıp girerek yeni skininizi etkinleştirebilirsiniz.");
                plugin.sqlManager.deletePlayerFromWaitingList(playerName);
            }
        }
        /* DEBUG
        ProxyServer.getInstance().getLogger().log(Level.INFO, playerName + " için skin kontrolü bitti. Bu işlem: " + (System.currentTimeMillis() - timeNow) + " ms sürdü.");
         */
    }

    @EventHandler
    public void serverSwitchEvent(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String serverName = event.getPlayer().getServer().getInfo().getName();

        if (serverName.contains("Skywars")) {
        	/* DEBUG
			long timeNow = System.currentTimeMillis();
			ProxyServer.getInstance().getLogger().log(Level.INFO, player.getName() + " için disguise skin kontrolü başladı.");
        	 */
            String disguiseName = plugin.sqlManager.getPlayerDisguise(player.getName());
            if (disguiseName != null) {
                Runnable nameResolver = new NameResolver(changeSkinPlugin, plugin.getProxy().getConsole(), player, disguiseName, false, false);
                ProxyServer.getInstance().getScheduler().runAsync(plugin, nameResolver);
            }
            /* DEBUG
			ProxyServer.getInstance().getLogger().log(Level.INFO, player.getName() + " için disguise skin kontrolü bitti. Bu işlem: " + (System.currentTimeMillis() - timeNow) + " ms sürdü.");
             */
            return;
        }

        /* DEBUG
		long timeNow = System.currentTimeMillis();
		ProxyServer.getInstance().getLogger().log(Level.INFO, player.getName() + " için skin kontrolü başladı.");
         */
        if (plugin.sqlManager.isPlayerInResetSkinList(player.getName())) {
            plugin.sqlManager.deletePlayerFromResetSkinList(player.getName());
            ArrayList<String> textureAndSignature = plugin.sqlManager.getSkin(player.getName());
            if (textureAndSignature != null) {
                if (textureAndSignature.size() == 2) {
                    String texture = textureAndSignature.get(0);
                    String signature = textureAndSignature.get(1);
                    SkinModel skinModel = SkinModel.createSkinFromEncoded(texture, signature);
                    Runnable task = new SkinApplier(changeSkinPlugin, plugin.getProxy().getConsole(), player, skinModel, false, true);
                    ProxyServer.getInstance().getScheduler().runAsync(plugin, task);
                    return;
                }
            }
            Runnable nameResolver = new NameResolver(changeSkinPlugin, plugin.getProxy().getConsole(), player, player.getName(), false, false);
            ProxyServer.getInstance().getScheduler().runAsync(plugin, nameResolver);
        }
        /* DEBUG
		ProxyServer.getInstance().getLogger().log(Level.INFO, player.getName() + " için skin kontrolü bitti. Bu işlem: " + (System.currentTimeMillis() - timeNow) + " ms sürdü.");
         */
    }
}
