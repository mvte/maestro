package maestro.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import maestro.Config;

public class SQLiteDataSource implements DatabaseManager {
	private final HikariDataSource ds;
	
	public SQLiteDataSource() {
		try {
			final File dbFile = new File("database.db");
			if(!dbFile.exists()) {
				if(dbFile.createNewFile())
					System.out.println("created database file");
				else
					System.out.println("could not create database file");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:database.db");
		config.setConnectionTestQuery("SELECT 1");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLmit", "2048");
		ds = new HikariDataSource(config);
		
		try(final Statement statement = getConnection().createStatement()) {
			final String defaultPrefix = Config.get("prefix");
			
			statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"guild_id VARCHAR(20) NOT NULL," +
					"prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "');");
			
			System.out.println("table initialized");
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private Connection getConnection() throws SQLException {
		return ds.getConnection();
	}


	@Override
	public String getPrefix(long guildId) {
		try (final PreparedStatement preparedStatement = getConnection()
				.prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {
			
			preparedStatement.setString(1, String.valueOf(guildId));
			
			try(final ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					return resultSet.getString("prefix");
				}
			}
			
			// If the guild is not logged in the database, we're going to log it here
			try(final PreparedStatement insertStatement = getConnection()
					.prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {
				insertStatement.setString(1, String.valueOf(guildId));
				System.out.println("logging new guild into database");
				
				insertStatement.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return Config.get("prefix");
	}


	@Override
	public void setPrefix(long guildId, String prefix) {
		try (final PreparedStatement preparedStatement = getConnection().
				prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {
			
			preparedStatement.setString(1, prefix);
			preparedStatement.setString(2,  String.valueOf(guildId));
			
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
		
}
