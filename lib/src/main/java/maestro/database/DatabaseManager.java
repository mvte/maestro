package maestro.database;

public interface DatabaseManager {
	
	// If you want to implement your own database
		// Create a new DataSource class that implements this interface
		// Change INSTANCE to an instance of the new DataSource class
	DatabaseManager INSTANCE = new SQLiteDataSource();
	
	String getPrefix(long guildId);
	
	void setPrefix(long guildId, String prefix);

}
