package com.uniocraft.UnioBungee.managers;

import com.uniocraft.UnioBungee.Main;
import com.uniocraft.UnioBungee.utils.packages.pool.CredentialPackageFactory;
import com.uniocraft.UnioBungee.utils.packages.pool.Pool;
import com.uniocraft.UnioBungee.utils.packages.pool.PoolDriver;
import com.uniocraft.UnioBungee.utils.packages.pool.properties.PropertyFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLManager {

    private Main plugin;

    private Pool pool;
    private String database;
    private String table;

    public SQLManager(Main plugin) {
        String host = plugin.config.getString("MySQL.host");
        String database = plugin.config.getString("MySQL.database");
        String username = plugin.config.getString("MySQL.username");
        String password = plugin.config.getString("MySQL.password");
        String table = plugin.config.getString("MySQL.table");

        pool = new Pool(CredentialPackageFactory.get(username, password), PoolDriver.MYSQL);
        pool.withMin(10).withMax(10).withMysqlUrl(host, database);
        pool.withProperty(PropertyFactory.leakDetectionThreshold(10000));
        pool.withProperty(PropertyFactory.connectionTimeout(15000));
        pool.build();

        this.plugin = plugin;
        this.database = database;
        this.table = table;
    }

    @SuppressWarnings("unused")
    private boolean updateSQL(String QUERY) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            int count = statement.executeUpdate();
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateSQLAsync(String QUERY) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try (Connection connection = pool.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(QUERY);
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        plugin.getProxy().getScheduler().runAsync(plugin, task);
    }

    public boolean isPlayerRegistered(String playerName) {
        String QUERY = "SELECT username FROM `website`.`xf_user` WHERE `username` = '" + playerName + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("username") == null) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerInWaitingList(String playerName) {
        String QUERY = "SELECT * FROM `" + database + "`.`" + table + "` WHERE `player` = '" + playerName + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("player") == null) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getSkinOfWaitingListPlayer(String playerName) {
        ArrayList<String> textureAndSignature = new ArrayList<String>();
        String QUERY = "SELECT * FROM `" + database + "`.`" + table + "` WHERE `player` = '" + playerName + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("texture") != null) {
                    textureAndSignature.add(res.getString("texture"));
                }
                if (res.getString("signature") != null) {
                    textureAndSignature.add(res.getString("signature"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return textureAndSignature;
    }

    public void addPlayerToWaitingList(String playerName, String texture, String signature) {
        if (isPlayerInWaitingList(playerName)) {
            updateSQLAsync("UPDATE `" + database + "`.`" + table + "` SET `texture` = '" + texture + "', `signature` = '" + signature + "' WHERE `" + table + "`.`player` = '" + playerName + "';");
        } else {
            updateSQLAsync("INSERT INTO `" + database + "`.`" + table + "` (`id`, `player`, `texture`, `signature`) VALUES (NULL, '" + playerName + "', '" + texture + "', '" + signature + "');");
        }
    }

    public void deletePlayerFromWaitingList(String playerName) {
        updateSQLAsync("DELETE FROM `" + database + "`.`" + table + "` WHERE `" + table + "`.`player` = '" + playerName + "'");
    }

    public String getPlayerDisguise(String player) {
        String QUERY = "SELECT * FROM `skywars`.`usw_disguise` WHERE `player` = '" + player + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                String disguiseName = res.getString("displayName");
                if (disguiseName == null) {
                    return null;
                }
                return disguiseName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPlayerInResetSkinList(String playerName) {
        String QUERY = "SELECT * FROM `" + database + "`.`unioskin_resetSkins` WHERE `player` = '" + playerName + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("player") == null) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deletePlayerFromResetSkinList(String playerName) {
        updateSQLAsync("DELETE FROM `" + database + "`.`unioskin_resetSkins` WHERE `unioskin_resetSkins`.`player` = '" + playerName + "'");
    }

    public ArrayList<String> getSkin(String playerName) {
        ArrayList<String> textureAndSignature = new ArrayList<String>();
        String QUERY = "SELECT * FROM `genel`.`skin` WHERE `player` = '" + playerName + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("texture") != null) {
                    textureAndSignature.add(res.getString("texture"));
                }
                if (res.getString("signature") != null) {
                    textureAndSignature.add(res.getString("signature"));
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return textureAndSignature;
    }

    public void onDisable() {
        pool.close();
    }
}
