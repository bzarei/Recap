package de.telekom.bzarei.ui;

import java.util.Scanner;
import de.telekom.bzarei.lookup.*;
import de.telekom.bzarei.model.Person;
import de.telekom.bzarei.repository.PersonRepository;
import de.telekom.bzarei.sst.*;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class Menu implements Closeable, EventListener {

	private PersonRepository personRepo;
	private Scanner scanner = new Scanner(System.in);
	private Logger log;

	public Menu() {
		log = Logger.getLogger(this.getClass());
		log.info("Hier beginnt Logging!");
	}
	
	public void setRepository(PersonRepository repo) {
		personRepo = repo;
	}
	
	@Override
	public void close() {
		scanner.close();
		System.out.println("Das Programm ist jetzt beendet. Goodbye!!");
	}

	public void receiveEvent(Event event) {
		System.out.println("Info: " + event.getEventMsg());
	}

	public void keepAsking() throws Exception {
		String choice;
		do {
			showMenu();
			choice = this.inputMenu();
			log.info("Auswahl " + choice + " gewählt");
			checkMenu(choice);
		} while (!choice.toUpperCase().equals("Q"));
	}

	public void keepSearch() throws IOException, SQLException {
		String choice;
		do {
			showSearchMenu();
			choice = this.inputMenu();
			checkSearchMenu(choice);
		} while (!choice.toUpperCase().equals("Q"));
	}

	private void showMenu() {
		System.out.println();
		System.out.println("*****************************************");
		System.out.println("*   Hauptmenü - bitte Auswahl treffen   *");
		System.out.println("* ------------------------------------- *");
		System.out.println("*  1. Person anlegen                    *");
		System.out.println("*  2. Person löschen                    *");
		System.out.println("*  3. Person Name Ändern                *");
		System.out.println("*  4. Personenliste zeigen              *");
		System.out.println("*  5. Personenliste löschen             *");
		System.out.println("*  6. Anzahl der angemeldeten Personen  *");
		System.out.println("*  7. Anzahl der freien Plätze          *");
		System.out.println("*  8. Search-Menü (Suche Personen)      *");
		System.out.println("* ------------------------------------- *");
		System.out.println("*  H. Help-Menü                         *");
		System.out.println("*  Q. Programm beenden                  *");
		System.out.println("*****************************************");
		System.out.print(">>");
	}

	private void showSearchMenu() {
		System.out.println();
		System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
		System.out.println("|  Search-Menü  -   Auswahl 1,2,3,4   |");
		System.out.println("| ----------------------------------- |");
		System.out.println("|   1. Suche nach Vorname             |");
		System.out.println("|   2. Suche nach Nachname            |");
		System.out.println("|   3. Suche nach Id                  |");
		System.out.println("|   4. Suche nach Standort            |");
		System.out.println("| ----------------------------------- |");
		System.out.println("|   Q. Zurück                         |");
		System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
		System.out.print(">>");
	}

	// nimmt die Usereingabe entgegen
	private String inputMenu() {
		var input = scanner.nextLine();
		return input;
	}

	/**
	 * this method offers handling in user interface via 10 options in main manu.
	 * user gets an interface as main menu in console with different choices:
	 * >> administration of a person (create, remove, update)
	 * >> controlling the list of persons online checking number of persons
	 *    and free capacity in the list
	 * >> displaying whole of persons from the list
	 * >> search a person or more persons (via id, name, last name and  location)
	 * >> exit from main menu
	 * >> help for how could user interact with interface.  
	 * 
	 * @param eingabe
	 * @throws Exception
	 * @throws MyException
	 */
	private void checkMenu(String eingabe) throws Exception {
		switch (eingabe.toUpperCase()) {
		case "1":
			inputPerson();
			break;
		case "2":
			removePerson();
			break;
		case "3":
			updatePerson();
			break;
		case "4":
			listAllPersons();
			break;
		case "5":
			removeAll();
			break;
		case "6":
			countPersons();
			break;
		case "7":
			freeCapacity();
			break;
		case "8":
			searchPerson();
			break;
		case "H":
			showHelpMenu();
			break;
		case "Q":
			break;
		default:
			System.out.println("falsche Eingabe!!!");
			break;
		}
	}

	/**
	 * this method offers looking for a person or more persons:
	 * in sub menu "search" you have choices to select search criteria
	 * by id, name, last name and location. 
	 *
	 * @param eingabe
	 * @throws MyException
	 * @throws IOException
	 * @throws SQLException
	 */
	private void checkSearchMenu(String eingabe) throws IOException, SQLException {
		switch (eingabe.toUpperCase()) {
		case "1":
			searchPersonByName("1");
			break;
		case "2":
			searchPersonByName("2");
			break;
		case "3":
			searchPersonById();
			break;
		case "4":
			searchPersonByOrt();
			break;
		case "Q":
			break;
		default:
			System.out.println("falsche Eingabe!!!");
			break;
		}
	}

	// 1. Person anlegen
	private void inputPerson() throws IOException, SQLException {
		Person person = new Person();
		System.out.println("Anrede eingeben:");
		String anrede = scanner.nextLine().trim();
		try {
			person.setAnrede(Anrede.fromString(anrede));
		} catch (Exception e) {
			System.out.println("Anrede darf nur Frau/F/Fr, Herr/Hr/H sein!");
			return;
		}
		System.out.println("Vorname eingeben:");
		String vorname = scanner.nextLine().trim();
		person.setVorname(vorname);

		System.out.println("Nachname eingeben:");
		String nachname = scanner.nextLine().trim();
		person.setNachname(nachname);

		System.out.println("Standort eingeben:");
		String ort = scanner.nextLine().trim();
		try {
			person.setStandort(Standort.fromString(ort));
		} catch (Exception e) {
			System.out.println("Standort darf nur Bonn/Köln/Berlin/Darmstadt sein!");
			return;
		}

		if ((vorname != "") || (nachname != "")) {
			personRepo.create(person);
		} else
			System.out.println("Name und Nachname sind leer. Anmeldung konnte nicht erfolgen!"
					+ "\nbitte nochmal mit Auswahl '1' versuchen:");
		freeCapacity();
	}

	// 2. Person löschen
	private void removePerson() throws SQLException {
		System.out.print("ID zum löschen: ");
		int l = scanner.nextInt();
		scanner.nextLine();
		if (!personRepo.delete(l)) {
			System.out.println(String.format("=> Id: %s nicht gefunden!",l));
		}
	}

	// 3. Person Ändern (Vorname oder Nachname)
	private void updatePerson() throws SQLException {
		System.out.print("ID zum Ändern: ");
		int l = scanner.nextInt();
		scanner.nextLine();
		Person person = new Person();
		person.setId(l);

		System.out.println("evtl. neue Anrede eingeben:");
		String anrede = scanner.nextLine().trim();
		try {
			person.setAnrede(Anrede.fromString(anrede));
		} catch (Exception e) {
			System.out.println("Anrede darf nur Frau/F/Fr, Herr/Hr/H sein!");
			return;
		}
		System.out.println("evtl. neue Vorname eingeben:");
		String vorname = scanner.nextLine().trim();
		person.setVorname(vorname);
		System.out.println("neue Nachname eingeben:");
		String nachname = scanner.nextLine().trim();
		person.setNachname(nachname);
		System.out.println("evtl. neue Standort eingeben:");
		String standort = scanner.nextLine().trim();
		try {
			person.setStandort(Standort.fromString(standort));
		} catch (Exception e) {
			System.out.println("Standort darf nur Köln/Bonn/Berlin/Darmstadt sein!");
			return;
		}
		if (!personRepo.update(person)) {
			System.out.println("Kein Update möglich: Person nicht gefunden!");
		} else
			System.out.println(String.format("new: %s %s",vorname,nachname));
	}

	// 4. Personenliste zeigen
	private void listAllPersons() throws Exception {
		// printHeadline(); // only use for delivery from DB
		// personRepo.printRecords(); // from DB
		printTheLine();
		personRepo.getAll(); // from the list: Person[]
	}

	// 5. Personenliste löschen
	private void removeAll() throws SQLException {
		personRepo.deleteAll();
	}

	// 6. Anzahl der Belegte Plätze zeigen
	private void countPersons() throws SQLException {
		System.out.println(String.format("Aktuelle Anzahl der Teilnehmern: %s",personRepo.size()));
	}

	// 7. Anzahl der freien Plätze
	private void freeCapacity() throws SQLException {
		System.out.println("-----------------------------------------");
		System.out.println(String.format("Freie Plätze: %s - belegt: %s",personRepo.getMaxsize()-personRepo.size(),personRepo.size()));	
	}

	// 8. Suche Personen
	private void searchPerson() throws IOException, SQLException {
		keepSearch();
	}

	// 8.1 & 8.2 Suche Personen nach Vorname oder Nachname
	private void searchPersonByName(String eingabe) throws SQLException {
		switch (eingabe) {
		case "1":
			System.out.print("Vorname zum Suchen: ");
			break;
		case "2":
			System.out.print("Nachname zum Suchen: ");
			break;
		}
		String name = scanner.nextLine().trim();
		personRepo.getSimilarNames(name, eingabe);
	}

	// 8.3 Suche Personen nach Teilnehmer-Id
	private void searchPersonById() throws SQLException {

		System.out.print("Id zum Suchen: ");
		int l = scanner.nextInt();
		scanner.nextLine();
		if (personRepo.get(l) != null) {
			System.out.println(String.format("Id: %s %s %s %s %s",personRepo.get(l).getId(),personRepo.get(l).getAnrede(),
					personRepo.get(l).getVorname(),personRepo.get(l).getNachname(), personRepo.get(l).getStandort().toString()));
		} else
			System.out.println(String.format("Person mit Id %s nicht gefunden!",l));
	}

	// 8.4 Suche Personen nach Standort
	private void searchPersonByOrt() throws SQLException {
		System.out.print("Standort: ");
		String ort = scanner.nextLine().trim();
		personRepo.getAllPersonsByOrt(ort);
	}
 
	// Help-Menü
	private void showHelpMenu() {
		System.out.println();
		System.out.println("* ------------------------------------- *");
		System.out.println("*  Hilfe bei Standorte und Anrede       *");
		System.out.println("* ------------------------------------- *");
		System.out.println("*  Standorte:  | Eingabe-Möglichkeiten  *");
		System.out.println("*  -------     | ---------------------  *");
		System.out.println("*  Bonn        |   b/bn/bo              *");
		System.out.println("*  Berlin      |   br/be                *");
		System.out.println("*  Köln/Koeln  |   kn/kln               *");
		System.out.println("*  Darmstadt   |   d/darm/da/ds         *");
		System.out.println("*  Magedeburg  |   m/mg/ma/mag          *");
		System.out.println("*              |                        *");
		System.out.println("*  Anrede:     |                        *");
		System.out.println("*  -------     | ---------------------  *");
		System.out.println("*  Frau        |   f/fr                 *");
		System.out.println("*  Herr        |   h/hr                 *");
		System.out.println("* ------------------------------------- *");
	}
	
	private void printTheLine() throws SQLException {
		System.out.println("---------------------------------------");
		System.out.println(String.format("  Inhalt der Liste mit %s Personen:",personRepo.size()));
		System.out.println("---------------------------------------");
	}
	
	private void printHeadline() {
		System.out.println("+----+--------+---------+------------+");
		System.out.println("| ID | ANREDE | VORNAME | NACHNAME   |");
		System.out.println("+----+--------+---------+------------+");
	}

} // Ende Class Menu