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

public class App {
	
	private static App theInstance;
	private Connection connection;
	private PersonRepository personRepo;
	
	public static App getRootApp() {
		if (theInstance == null) {
			theInstance = new App();
		}
		return theInstance;
	}
	
	// constructor
	private App() {
	}

	private void dbConnenct() throws ClassNotFoundException, SQLException, IOException {

		try (ReadConfig rc = new ReadConfig("/home/sea2/RecapConfig.ini")) {
			HashMap<String,String> config = rc.readConfig();
			String dbConfig = String.format("jdbc:mariadb://%s:%s/%s?user=%s&password=%s",config.get("Server"), 
					config.get("Port"), config.get("Datenbank"), config.get("Benutzer"), config.get("Passwort"));	
			
			Class.forName(config.get("Driver"));
			connection = DriverManager.getConnection(dbConfig);
		} catch (IOException ex) { ex.fillInStackTrace();}
	}
	
	private void dbDisconnect() throws SQLException {
		if (connection != null && !connection.isClosed())  
			connection.close();
	}
	
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
