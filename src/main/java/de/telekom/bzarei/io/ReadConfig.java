package de.telekom.bzarei.io;

import java.io.*;
import java.util.HashMap;

public class ReadConfig implements Closeable {

	private FileReader fileReader;
	private BufferedReader buffReader;

	// Constructor
	public ReadConfig(String filename) throws IOException {
		File file = new File(filename);      // name = /home/$USER/RecapConfig.ini
		fileReader = new FileReader(file);
		buffReader = new BufferedReader(fileReader);
	}

	// Constructor
	public ReadConfig(BufferedReader br) throws FileNotFoundException {
		this.buffReader = br;
	}

	@Override
	public void close() throws IOException {
		fileReader.close();
		buffReader.close();
	}

	public HashMap<String,String> readConfig() throws IOException {
		String str = buffReader.readLine();
		HashMap<String,String> configList = new HashMap<String, String>();
		while (str != null) {			
			String[] result = str.split("=");
			configList.put(result[0], result[1].trim());
			str = buffReader.readLine();  // lese nächste Zeile
		}
		return configList;
	}
}
