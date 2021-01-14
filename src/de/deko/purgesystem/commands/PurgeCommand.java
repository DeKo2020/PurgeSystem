package de.deko.purgesystem.commands;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import de.deko.purgesystem.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PurgeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("system.admin")) {
                String prefix = Main.getInstance().getPrefix();
                PlotAPI plotAPI = new PlotAPI();
                Plot plot = plotAPI.getPlot(player.getLocation());
                String server = Main.getInstance().getConfig().getString("Server");
                switch (args.length) {
                    case 0:
                        sendHelp(player);
                        break;
                    case 1:
                        switch (args[0]) {
                            case "1":
                                player.sendMessage(prefix + "§aDein Eintrag wurde gespeichert.");
                                create(player, server, plot.getId(),"Nationalsozialismus");
                                break;
                            case "2":
                                player.sendMessage(prefix + "§aDein Eintrag wurde gespeichert.");
                                create(player, server, plot.getId(),"Sexistisch");
                                break;
                            case "3":
                                player.sendMessage(prefix + "§aDein Eintrag wurde gespeichert.");
                                create(player, server, plot.getId(),"Beleidigend");
                                break;
                            case "4":
                                player.sendMessage(prefix + "§aDein Eintrag wurde gespeichert.");
                                create(player, server, plot.getId(),"Sonstiges");
                                break;
                            case "list":
                                try {
                                    sendList(player);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "cblist":
                                try {
                                    sendCityBuildList(player);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                sendHelp(player);
                        }
                        break;
                    case 2:
                        switch (args[0]) {
                            case "del":
                                int id = Integer.valueOf(args[1]).intValue();
                                if (IDExists(id)) {
                                    player.sendMessage(prefix + "§aDie Plotmeldung wurde gelöscht.");
                                    deleteID(id);
                                }else {
                                    player.sendMessage(prefix + "§7Es wurde kein Eintrag mit der ID §e" + id + " §7gefunden.");
                                }
                                break;
                        }
                         break;
                    default:
                        sendHelp(player);
                }
            }
        }
        return false;
    }

    private void sendList(Player player) throws SQLException {
        player.sendMessage("§6§l===== Plotmeldungen §6§l=====");
        player.sendMessage("§6§l= Seite §e§l1 §6§l/ §e§l1");
        ResultSet rs = Main.getInstance().getMySQL().query("SELECT * FROM PurgeReport WHERE CityBuild='" + Main.getInstance().getConfig().getString("Server") + "'");
        while (rs.next()) {

            TextComponent plotteleport = new TextComponent();
            plotteleport.setText(" §6[§f" + rs.getString("Plot") + "§6]");
            plotteleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p tp " + rs.getString("Plot")));
            plotteleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fKlicke, um dich zum Grundstück zu teleportieren.").create()));

            TextComponent delete = new TextComponent();
            delete.setText(" §6[§4✗ löschen§6]");
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/preport del " + rs.getString("ID")));
            delete.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fLöschen").create()));

            TextComponent text = new TextComponent();
            text.setText("§6ID " + rs.getString("ID") + " §7- §6" + rs.getString("Grund") + " §7- §f" + rs.getString("Reporter"));

            text.addExtra(plotteleport);
            text.addExtra(delete);

            player.spigot().sendMessage(text);

        }
    }

    private void sendCityBuildList(Player player) throws SQLException {
        player.sendMessage("§6§l===== Plotmeldungen §6§l=====");
        player.sendMessage("§6§l= Seite §e§l1 §6§l/ §e§l1");
        ResultSet rs = Main.getInstance().getMySQL().query("SELECT * FROM PurgeReport");
        while (rs.next()) {

            TextComponent teleport = new TextComponent();
            teleport.setText(" §6[§f" + rs.getString("Plot") + "§6]");
            teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + rs.getString("CityBuild")));
            teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fKlicke, um dich zum Grundstück zu teleportieren.").create()));

            TextComponent plotteleport = new TextComponent();
            plotteleport.setText(" §6[§f" + rs.getString("Plot") + "§6]");
            plotteleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p tp " + rs.getString("Plot")));
            plotteleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fKlicke, um dich zum Grundstück zu teleportieren.").create()));

            TextComponent delete = new TextComponent();
            delete.setText(" §6[§4✗ löschen§6]");
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/preport del " + rs.getString("ID")));
            delete.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fLöschen").create()));

            TextComponent text = new TextComponent();
            text.setText("§6ID " + rs.getString("ID") + " §7- §6" + rs.getString("CityBuild") + " §7- §6" + rs.getString("Grund") + " §7- §f" + rs.getString("Reporter"));

            if(Main.getInstance().getConfig().getString("Server").startsWith(rs.getString("CityBuild"))) {
                text.addExtra(plotteleport);
            }else {
                text.addExtra(teleport);
            }
            text.addExtra(delete);

            player.spigot().sendMessage(text);

        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8=============§6 Plotmeldung §8=============");
        player.sendMessage(" ");
        player.sendMessage("§e/preport <Grund> §8| §7Meldet das aktuelle Grundstück.");
        player.sendMessage("§e/preport list §8| §7Zeigt die Liste der offenen Reports an.");
        player.sendMessage("§e/preport cblist §8| §7Zeigt eine Übersicht über alle Citybuilds.");
        player.sendMessage("§e/preport del <ID> §8| §7Löscht den Report.");
        player.sendMessage( " ");
        player.sendMessage("§7Bitte stelle dich auf das Grundstück welches du melden möchtest und nutze §e/preport <Grund>");
        player.sendMessage(" ");
        player.sendMessage("§cID: 1 - Nationalsozialismus");
        player.sendMessage("§cID: 2 - Sexistisch");
        player.sendMessage("§cID: 3 - Beleidigend");
        player.sendMessage("§cID: 4 - Sonstiges");
        player.sendMessage("§8=============§6 Plotmeldung §8=============");
    }

    public static boolean IDExists(int id) {
        try {
            PreparedStatement preparedStatement = Main.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `PurgeReport` WHERE ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return (resultSet.getString("ID") != null);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void create(Player player, String citybuild, PlotId plotId, String reason) {
        try {
            PreparedStatement preparedStatement = Main.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO `PurgeReport` (Reporter, CityBuild, Plot, Grund) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, citybuild);
            preparedStatement.setString(3, String.valueOf(plotId));
            preparedStatement.setString(4, reason);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteID(int id) {
        try {
            PreparedStatement preparedStatement = Main.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM `PurgeReport` WHERE ID= '" + id + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
