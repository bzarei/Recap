package de.telekom.bzarei;

import org.apache.log4j.xml.DOMConfigurator;

public class Main {

	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		App app = App.getRootApp();
		app.run(args);
	}
}