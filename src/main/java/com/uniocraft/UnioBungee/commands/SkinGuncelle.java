package com.uniocraft.UnioBungee.commands;

import com.github.games647.changeskin.bungee.task.SkinApplier;
import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class SkinGuncelle extends Command {

	private Main plugin;

	public SkinGuncelle(String name, Main plugin) {
		super(name);
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, final String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatColor.RED + "Bu komut yalnızca oyuncular tarafından kullanılabilir. Evet tarafından.");
			return;
		}

		ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
			public void run() {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				ArrayList<String> skin = plugin.sqlManager.getSkin(sender.getName());
				if (skin != null && skin.size() == 2) {
					String value = skin.get(0);
					String signature = skin.get(1);

					SkinModel skinModel = SkinModel.createSkinFromEncoded(value, signature);

					Runnable task = new SkinApplier(plugin.changeSkinPlugin, sender, player, skinModel, false, true);
					ProxyServer.getInstance().getScheduler().runAsync(plugin, task);
					player.sendMessage(Main.prefix + "Skininiz güncelleştirilmiştir. Oyundan çıkıp girerek değişiklikleri görebilirsiniz.");
				}else {
					ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "setskin " + player.getName() + " " + player.getName());
					ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "skinupdate " + player.getName());
					player.sendMessage(Main.prefix + "Skininiz güncelleştirilmiştir. Oyundan çıkıp girerek değişiklikleri görebilirsiniz.");
				}
			}
		});
	}
}