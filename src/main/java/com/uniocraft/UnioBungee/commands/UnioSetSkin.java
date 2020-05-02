package com.uniocraft.UnioBungee.commands;

import com.github.games647.changeskin.bungee.task.SkinApplier;
import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.uniocraft.UnioBungee.Main;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnioSetSkin extends Command {
	
	private Main plugin;

	public UnioSetSkin(String name, Main plugin) {
		super(name);
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			if (args.length == 3) {
				String playerName = args[0];
				String value = args[1];
				String signature = args[2];

				SkinModel skinModel = SkinModel.createSkinFromEncoded(value, signature);
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);

				if (player != null) {
					Runnable task = new SkinApplier(plugin.changeSkinPlugin, sender, player, skinModel, false, true);
					ProxyServer.getInstance().getScheduler().runAsync(plugin, task);
					player.sendMessage(Main.prefix + "Tebrikler! Skininiz değiştirildi. Oyundan çıkıp girerek yeni skininizi etkinleştirebilirsiniz.");
				}else {
					plugin.sqlManager.addPlayerToWaitingList(playerName, value, signature);
				}
			}
		}
	}
}