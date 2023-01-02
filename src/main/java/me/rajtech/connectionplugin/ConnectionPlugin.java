package me.rajtech.connectionplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ConnectionPlugin extends JavaPlugin {

    String db_url = "jdbc:mysql://localhost:3306/javabase";
    String db_username = "java";
    String db_password = "password";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("ConnectionPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ConnectionPlugin has been disabled!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // connect command
        if (cmd.getName().equalsIgnoreCase("connect")) {
            // get the sender's username
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();


            // create a new connection
            try {
                Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                String sql = "SELECT * FROM users WHERE username = '" + uuid + "'";

                try {
                    conn.createStatement().executeUpdate(sql);

                    // get the result
                    try {
                        ResultSet rs = conn.createStatement().executeQuery(sql);

                        try {
                            if (rs.next()) {
                                // if the username exists, check if the user is connected
                                if (rs.getBoolean("connected")) {
                                    sender.sendMessage("You are already connected.");
                                } else {
                                    // if the user is not connected, connect them
                                    ResultSet __rs = conn.createStatement().executeQuery("SELECT code FROM users WHERE username = '" + uuid + "'");
                                    sender.sendMessage("Go to the Discord server and execute the command `/mc connect " + __rs + "` to finish the connection.");
                                }
                            } else {
                                // if the username does not exist, create a new entry

                                int random_code = (int)(Math.random() * 1000000);
                                conn.createStatement().executeUpdate("INSERT INTO users (username, code, discord_id) VALUES ('" + uuid + "', '" + random_code + "', 0)");
                                conn.commit();
                                sender.sendMessage("Go to the Discord server and execute the command `/mc connect " + random_code + "` to finish the connection.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // disconnect command
        else if (cmd.getName().equalsIgnoreCase("disconnect")) {
            // get the sender's username
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();

            // connect to mysql database
            String db_url = "jdbc:mysql://localhost:3306/javabase";
            String db_username = "java";
            String db_password = "password";

            // create a new connection
            try {
                Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                String sql = "SELECT * FROM users WHERE username = '" + uuid + "'";

                try {
                    conn.createStatement().executeUpdate(sql);

                    // get the result
                    try {
                        ResultSet rs = conn.createStatement().executeQuery(sql);

                        try {
                            if (rs.next()) {
                                // if the username exists, check if the user is connected
                                if (rs.getBoolean("discord_id")) {
                                    // if the user is connected, disconnect them
                                    String __sql = "DELETE FROM users WHERE username = '" + uuid + "'";
                                    conn.createStatement().executeUpdate(__sql);
                                    sender.sendMessage("You have been disconnected.");
                                } else {
                                    sender.sendMessage("You are not connected.");
                                }
                            } else {
                                sender.sendMessage("You are not connected.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();


                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }
}