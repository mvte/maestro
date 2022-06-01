
package maestro;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
	//when merging, change dotenv.get to System.getenv
	
//	private static final Dotenv dotenv = Dotenv.load();
		//and get rid of this

	public static String get(String key) {
//		return dotenv.get(key.toUpperCase());
		return System.getenv(key.toUpperCase());
	}
}
