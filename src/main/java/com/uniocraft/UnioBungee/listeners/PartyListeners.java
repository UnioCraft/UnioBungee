package com.uniocraft.UnioBungee.listeners;

import com.uniocraft.UnioBungee.Main;
import de.simonsator.partyandfriends.api.events.party.LeftPartyEvent;
import de.simonsator.partyandfriends.api.events.party.PartyJoinEvent;
import de.simonsator.partyandfriends.api.events.party.PartyLeaderChangedEvent;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PartyAPI;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PartyListeners implements Listener {

	private Main plugin;

	public PartyListeners(Main plugin) {
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	@EventHandler
	public void serverSwitchEvent(ServerSwitchEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String serverName = event.getPlayer().getServer().getInfo().getName();
		ServerInfo server = event.getPlayer().getServer().getInfo();
		PlayerParty party = PartyAPI.getParty(PAFPlayerManager.getInstance().getPlayer(player));
		String katilanOyuncular = null;

		if (party != null) {
			if (party.getLeader() != null) {
				katilanOyuncular = party.getLeader().getName();
				for (final OnlinePAFPlayer partyPlayer : party.getPlayers()) {
					katilanOyuncular = katilanOyuncular + " " + partyPlayer.getPlayer().getName();
				}
			}else {
				katilanOyuncular = player.getName();
				player.sendMessage("Parti sistemiyle alakalı bir teknik sorun oluştu. Sunucudan çıkıp girerek sorunu düzeltebilirsiniz.");
			}
		}else {
			katilanOyuncular = player.getName();
		}

		final String katilanOyuncularfinal = katilanOyuncular;
		final ServerInfo serverFinal = server;

		if (serverName.contains("Skywars")) {
			ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
				@Override
				public void run() {
					sendToBukkit("command", "sw autojoinplayers " + katilanOyuncularfinal, serverFinal);		
				}
			}, 1/4, TimeUnit.SECONDS);
		}
	}

	@EventHandler
	public void partyLeaveEvent(LeftPartyEvent event) {
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("SoloSkywars01"));
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("SoloSkywars02"));
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("SoloSkywars03"));
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("SoloSkywars04"));
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("DuoSkywars01"));
		sendToBukkit("command", "sw leavefromparty " + event.getPlayer().getName(), plugin.getProxy().getServerInfo("DuoSkywars02"));
	}

	@EventHandler
	public void partyJoinEvent(PartyJoinEvent event) {
		PlayerParty party = event.getParty();

		if (party.getAllPlayers().size() > 2) {
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars01"));
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars02"));
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars03"));
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars04"));
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("DuoSkywars01"));
			sendToBukkit("command", "sw jointoparty " + event.getPlayer().getName() + " " + event.getParty().getLeader().getName(), plugin.getProxy().getServerInfo("DuoSkywars02"));
		}else if (party.getAllPlayers().size() == 2) {
			String katilanOyuncular = party.getLeader().getName();

			for (final OnlinePAFPlayer partyPlayer : party.getAllPlayers()) {
				if (!partyPlayer.equals(party.getLeader())) {
					katilanOyuncular = katilanOyuncular + " " + partyPlayer.getName();
				}
			}

			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("SoloSkywars01"));
			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("SoloSkywars02"));
			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("SoloSkywars03"));
			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("SoloSkywars04"));
			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("DuoSkywars01"));
			sendToBukkit("command", "sw createparty " + katilanOyuncular, plugin.getProxy().getServerInfo("DuoSkywars02"));
		}
	}

	@EventHandler
	public void partyLeaderChangeEvent(PartyLeaderChangedEvent event) {
		PlayerParty party = event.getParty();

		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars01"));
		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars02"));
		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars03"));
		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("SoloSkywars04"));
		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("DuoSkywars01"));
		sendToBukkit("command", "sw changeleader " + party.getLeader().getName(), plugin.getProxy().getServerInfo("DuoSkywars02"));
	}

	private void sendToBukkit(String channel, String message, ServerInfo server) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF(channel);
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.sendData("Return", stream.toByteArray());

	}
}
