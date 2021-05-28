package de.telekom.bzarei;

import de.telekom.bzarei.repository.PersonRepository;
import de.telekom.bzarei.App;
import de.telekom.bzarei.io.ReadConfig;
import de.telekom.bzarei.ui.Menu;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class App {
	
	private static App theInstance;
	private Connection connection;
	private PersonRepository personRepo;
	
	// constructor
	private App() {
	}

	// singleton
	public static App getRootApp() {
		if (theInstance == null) {
			theInstance = new App();
		}
		return theInstance;
	}

	/**
	 * this method uses method readConfig from Class ReadConfig in order to catch 
	 * configuration data like user, password, server, data bank an port.
	 * finally with this information it will tried to create connection with data bank.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private void dbConnenct() throws ClassNotFoundException, SQLException, IOException {
		try (ReadConfig rc = new ReadConfig("/home/sea2/RecapConfig.ini")) {
			HashMap<String,String> config = rc.readConfig();
			String dbConfig = String.format("jdbc:mariadb://%s:%s/%s?user=%s&password=%s",
					config.get("Server"),config.get("Port"),config.get("Datenbank"),
					config.get("Benutzer"), config.get("Passwort"));	
			Class.forName(config.get("Driver"));
			connection = DriverManager.getConnection(dbConfig);
		} catch (IOException ex) { ex.fillInStackTrace();}
	}
	
	/**
	 * in this method connection with data bank will disconnected:
	 * only if already there is a connection and it's still opened  
	 * @throws SQLException
	 */
	private void dbDisconnect() throws SQLException {
		if (connection != null && !connection.isClosed())  
			connection.close();
	}
	
	/**
	 * in this method it will be tried to do followings step by step:
	 * 1. create an instance of class Menu
	 * 2. create connection with data banak
	 * 3. create an instance of repository with connection as input parameter
	 * 4. 
	 * 5. start subscribing of Menu
	 * 6. start and keeping handling with UI 
	 * 7. end of subscribing of Menu
	 * 8. disconnect from data bank
	 *   
	 * @param args
	 * @throws Exception
	 */
	public void run(String[] args) throws Exception {
		try (Menu menu = new Menu()) {
			dbConnenct();
			personRepo = new PersonRepository(connection);
			menu.setRepository(personRepo);
			personRepo.subscribe(menu);
			menu.keepAsking();
			personRepo.unsubscribe(menu);
			dbDisconnect();				
		} catch (Exception ex) {
			System.out.println(" Fehler bei DB-Verbindung! ");
			ex.fillInStackTrace();
		  }			
		}
}
