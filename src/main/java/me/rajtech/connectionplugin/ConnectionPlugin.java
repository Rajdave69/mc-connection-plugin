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
    if (cmd.getName().equalsIgnoreCase("connect")) {
      // get the sender's username
      String username = sender.getName();
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
                if (rs.getBoolean("connected")) {
                  sender.sendMessage("You are already connected.");
                } else {
                  // if the user is not connected, connect them
                  String __sql = "SELECT code FROM users WHERE username = '" + uuid + "'";
                  conn.createStatement().executeUpdate(__sql);
                  ResultSet __rs = conn.createStatement().executeQuery(__sql);
                  sender.sendMessage("Go to the Discord server and execute the command `/connect " + username + " " + __rs + "` to finish the connection.");
                }
              } else {
                // if the username does not exist, create a new entry

                int random_code = (int)(Math.random() * 1000000);
                String _sql = "INSERT INTO users (username, code, connected) VALUES ('" + uuid + "', '" + random_code + "', false)";
                conn.createStatement().executeUpdate(_sql);
                sender.sendMessage("Go to the Discord server and execute the command `/connect " + username + " " + random_code + "` to finish the connection.");
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

      // db schema - username, code, connected (boolean)

    }
    return false;
  }
}