/*
 
 when merging, change dotenv.get to System.getenv!!!
 
 */
package maestro;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

	//private static final Dotenv dotenv = Dotenv.load();

	public static String get(String key) {
		return System.getenv(key.toUpperCase());
	
	}
}
