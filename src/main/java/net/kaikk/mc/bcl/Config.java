package net.kaikk.mc.bcl;

public class Config {
	public int maxHoursOffline, defaultChunksAmountAlwaysOn, defaultChunksAmountOnlineOnly, maxChunksAmountAlwaysOn, maxChunksAmountOnlineOnly;
	public String dataStore, mySqlHostname, mySqlUsername, mySqlPassword, mySqlDatabase;
	
	Config(BetterChunkLoader instance) {
		instance.getConfig().options().copyDefaults(true);
		instance.saveDefaultConfig();
		
		this.maxHoursOffline=instance.getConfig().getInt("MaxHoursOffline", 168);
		
		this.defaultChunksAmountAlwaysOn=instance.getConfig().getInt("DefaultChunksAmount.AlwaysOn", 5);
		this.defaultChunksAmountOnlineOnly=instance.getConfig().getInt("DefaultChunksAmount.OnlineOnly", 50);

		this.maxChunksAmountAlwaysOn=instance.getConfig().getInt("MaxChunksAmount.AlwaysOn", 57);
		this.maxChunksAmountOnlineOnly=instance.getConfig().getInt("MaxChunksAmount.OnlineOnly", 75);

		this.dataStore=instance.getConfig().getString("DataStore");
		
		this.mySqlHostname=instance.getConfig().getString("MySQL.Hostname");
		this.mySqlUsername=instance.getConfig().getString("MySQL.Username");
		this.mySqlPassword=instance.getConfig().getString("MySQL.Password");
		this.mySqlDatabase=instance.getConfig().getString("MySQL.Database");
	}
}