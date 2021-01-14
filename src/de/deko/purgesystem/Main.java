package de.deko.purgesystem;

import de.deko.purgesystem.commands.PurgeCommand;
import de.deko.purgesystem.mysql.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends JavaPlugin {

    static Main instance;

    private MySQL mySQL;

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public String getPrefix() {
        return "§8[§6GrieferGames§8] ";
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerCommands();
        loadConfig();
        ConnectMySQL();
    }

    public void registerCommands() {
        getCommand("preport").setExecutor(new PurgeCommand());
    }

    private void loadConfig() {
        getConfig().addDefault("Server", "CB1");
        getConfig().addDefault("host", "localhost");
        getConfig().addDefault("database", "purgesystem");
        getConfig().addDefault("user", "root");
        getConfig().addDefault("password", "");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    String host = this.getConfig().getString("host");
    String database = this.getConfig().getString("database");
    String user = this.getConfig().getString("user");
    String password = this.getConfig().getString("password");

    private void ConnectMySQL() {
        mySQL = new MySQL(host, database, user, password);
        mySQL.update("CREATE TABLE IF NOT EXISTS `PurgeReport` (`ID` INT(255) auto_increment UNIQUE, `Reporter` VARCHAR(64), `CityBuild` VARCHAR(64), `Plot` VARCHAR(64), `Grund` VARCHAR(64))");

    }

}
