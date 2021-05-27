package de.telekom.bzarei.repository;

import java.sql.*;
import java.util.ArrayList;
import de.telekom.bzarei.lookup.*;
import de.telekom.bzarei.model.Person;
import de.telekom.bzarei.sst.*;

public class PersonRepository implements EventSubscription {

	private Connection connection;
	private String query;
	private static int MAX_PERSONS = 12;
	private EventListener listener;
	
	// constructor
	public PersonRepository (int size) throws Exception {
		if (size <= 0 || size > MAX_PERSONS) { 
			throw new Exception(String.format("\nAnzahl der Teilnehmern passt nicht!! muss zwischen 1 und %s sein!!!",MAX_PERSONS));
		}
	}
	
	// constructor
	public PersonRepository (Connection co, int size) {
		connection = co;
		MAX_PERSONS = size;
	}
	
	// constructor
	public PersonRepository() throws Exception {
		this(MAX_PERSONS);
	}
	
	public void subscribe(final EventListener listener ) {
		if (listener != null)
			this.listener = listener;
	}
	
	public void unsubscribe(EventListener listener) {
		if (listener != null && this.listener == listener)
			listener = null;
	}
	
	public void sendEvent(final int id) throws SQLException {
		if (listener != null) {
			Event event = new Event();
			query = "SELECT message FROM event WHERE id=?"; 
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();
			if (result.next()) {					
				event.setEventMsg(result.getString(1));
				event.setEventId(id);
				listener.receiveEvent(event);
			}
		}
	}
	
	// constructor
	public PersonRepository(final Connection co) {	
		this.connection = co;
	}
	
	public static int getMaxsize() {
		return MAX_PERSONS;
	}
	
	/**
	 * this method is used for adding a new person into table personen.
	 * if reference address of given person is null nothing to do and
	 * the method returns false otherwise inserts person into the table 
	 * and returns true. 
	 * Id for each person is calculated from the size(): next free id = size()+1
	 * @param p
	 * @return
	 * @throws SQLException
	 */
	public boolean create(final Person p) throws SQLException {
	
		if (p == null) 
			return false;
		if (size() >= MAX_PERSONS) {
			sendEvent(1);
			return false;
		}
		query = "INSERT INTO personen (ID, ANREDE, VORNAME, NACHNAME, STANDORT) VALUES ( ?, ?, ?, ?, ? )";		
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, getNextfreeId());
			ps.setByte(2, p.getAnrede().toByte());
			ps.setString(3, p.getVorname());
			ps.setString(4, p.getNachname());
			ps.setString(5, p.getStandort().toString());
			ps.execute();
		} catch (SQLException ex) {
			System.out.println(" Ein Fehler beim INSERT in DB aufgetretten! ");
			ex.getLocalizedMessage();
			ex.getSQLState();
			return false;
		  }
		sendEvent(3);
		return true;
	}
	
	/**
	 * this method looks for given id in the table personen.
	 * if id is found method returns all attributes of this person in form  of 
	 * class Person.
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Person get(int id) throws SQLException {
		
		Person person = new Person();
		query = "SELECT * FROM personen WHERE id=?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, id);		
			try (ResultSet result = ps.executeQuery()) {
				if (result.next()) {					
					person.setId(result.getInt(1));
					person.setAnrede(Anrede.fromByte(result.getByte(2)));
					person.setVorname(result.getString(3));
					person.setNachname(result.getString(4));
					person.setStandort(Standort.fromString(result.getString(5)));
					return person;
				}
				return null;
			} catch (SQLException ex) {
				ex.getSQLState();
				ex.getLocalizedMessage();
			  }
		} catch (SQLException ex) {
			ex.getSQLState();
			ex.getLocalizedMessage();
		  }
		return person;
	}
	
	/**
	 * in this method will be checked if the person who should be changed exists in
	 * the table. If the reference address of this given person is null or id of
	 * given person doesn't exists in the table nothing to do and the method returns false
	 * otherwise the row for this id will be updated and method returns true. 
	 * @param p
	 * @return
	 * @throws SQLException
	 */
	public boolean update(final Person p) throws SQLException {

		if (p == null)                       
			return false;
		int i = p.getId();
		if (get(i) == null)   // person doesn't exist in DB
			return false;
		
		query = "UPDATE personen SET anrede=?, vorname=?, nachname=?, standort=? WHERE id=?"; 
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setByte(1, p.getAnrede().toByte());
			ps.setString(2, p.getVorname());
			ps.setString(3, p.getNachname());
			ps.setString(4, p.getStandort().toString());
			ps.setInt(5, i);
			ps.execute();
		} catch (SQLException ex) {
			System.out.println(" Ein Fehler beim UPDATE in DB aufgetretten! ");
			System.out.println(ex.getSQLState());
			System.out.println(ex.getLocalizedMessage());
			return false;
		  }	
		return true;
	}
	
	/**
	 * in this method it will be checked if the given person p exists in the table.
	 * if reference address of given person is null or the person doesn't exist in the table
	 * nothing to do and the method returns false, otherwise found person 
	 * will be deleted from the table. 
	 * @param p
	 * @return
	 * @throws SQLException
	 */
	public boolean delete(final Person p) throws SQLException {
		
		if (p == null)                // person points of null 
			return false;
		if (get(p.getId()) == null)   // person doesn't exist in DB
			return false;
		
		query = "DELETE FROM personen WHERE id=?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, p.getId());
			ps.execute();
		} catch (SQLException ex) {
			System.out.println(" Fehler beim DELETE in DB aufgetretten! ");
			System.out.println(ex.getSQLState());
			System.out.println(ex.getLocalizedMessage());
			ex.fillInStackTrace();
			return false;
		  }
		return true;
	}
	
	/**
	 * in this method it will be checked if the given id exists in the table.
	 * if no nothing to do and the method returns false, otherwise found id 
	 * will be deleted from the table. 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public boolean delete(final int id) throws SQLException {
		
		if (get(id) == null)   // person doesn't exist in DB
			return false;
		
		query = "DELETE FROM personen WHERE id=?";	
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ps.execute();
		} catch (SQLException ex) {
			System.out.println("Ein Fehler beim DELETE mit ID aufgetretten! ");
			ex.getSQLState();
			ex.getLocalizedMessage();
			return false;
		  }
		sendEvent(2);
		return true;
	}
	
	public Person[] getAllPersonsByOrt(final String ort) throws SQLException {	
		
		Person[] list = new Person[size()];
		query = "SELECT * FROM personen WHERE standort like ?";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, ort + "%");
			try (ResultSet result = ps.executeQuery()) {
				if (result.first()) {
					result.beforeFirst();
					int index = 0;
					while (result.next()) {
						Person person = new Person();
						person.setId(result.getInt(1));
						person.setAnrede(Anrede.fromByte(result.getByte(2)));
						person.setVorname(result.getString(3));
						person.setNachname(result.getString(4));
						person.setStandort(Standort.fromString(result.getString(5)));
						list[index++] = person;
					}
					printList(list);
				}
			} catch (Exception ex) { ex.fillInStackTrace(); }
			return list;
		} catch (Exception ex) {
			ex.getLocalizedMessage();
			ex.fillInStackTrace();
		  }		
		return list;
	}
		
	/**
	 * At first it will be checked in this method if the table is empty.
	 * is not so all persons or lines will be deleted from table personen.
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteAll() throws SQLException {
	
		query = "SELECT COUNT(*) FROM personen";
		try (Statement ps = connection.createStatement()) { 
			try (ResultSet result = ps.executeQuery(query)) { 
				result.next();
				if (result.getInt(1) == 0) {
					return false;
				}
				query = "DELETE FROM personen";
				ps.execute(query);
			} catch (SQLException ex ) {
				ex.fillInStackTrace();
			  }
			
		} catch (SQLException ex) {
			System.out.println("Ein Fehler innerhalb Methode deleteAll() aufgetretten! ");
			ex.fillInStackTrace();
		  }
		return true; 
	}
	
	/**
	 * this method prints all attributes of a person from table personen on demand.
	 * @param result
	 * @throws SQLException
	 */
	private void printRecord(ResultSet result) throws SQLException {	
		while (result.next()) {	
			System.out.println(String.format("|  %s |      %s | %s | %s | %s",result.getInt(1),result.getByte(2),
			result.getString(3),result.getString(4),result.getString(5)));
		}		
	}
	
	/**
	 * in this method all records or persons will be printed from table personen.
	 * @throws SQLException
	 */
	public void printRecords() throws SQLException {
		query = "SELECT * FROM personen";
		try (Statement st = connection.createStatement()) {
			try (ResultSet result = st.executeQuery(query)) {
				while (result.next()) {
					System.out.println(String.format("|  %s |      %s | %s | %s | %s",result.getInt(1),result.getByte(2),
						result.getString(3),result.getString(4),result.getString(5)));
				}
			} catch (SQLException ex) {
				ex.fillInStackTrace();
			  }
		} catch (SQLException ex) {
			ex.fillInStackTrace();
		  }
	}
	
	/**
	 * this method goes through all records in data bank and stores every record in a array list of Persons.
	 * Address of each line will be stored in an element of array list ArrayList<Person>
	 * finally list of persons will be printed by using method printList().   
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Person> getAll() throws Exception {			
			ArrayList<Person> list = new ArrayList<Person>();		
			query = "SELECT * FROM personen";
			try (Statement st = connection.createStatement()) {
				try (ResultSet result = st.executeQuery(query)) {
					while (result.next()) {
						Person person = new Person();
						person.setId(result.getInt(1));
						person.setAnrede(Anrede.fromByte(result.getByte(2)));
						person.setVorname(result.getString(3));
						person.setNachname(result.getString(4));
						person.setStandort(Standort.fromString(result.getString(5)));
						list.add(person);
					}
					printList(list);
				} catch (SQLException ex) { ex.fillInStackTrace();}
			} catch (SQLException ex) { ex.fillInStackTrace();}
			return list;
	}
	
	/**
	 * this method returns all similar names. ctl is a control parameter 
	 * used to check whether a search should be carried out for first name or surname.
     * With ctrl = 1 (comming from Menu) will be found all similar first names 
     * and with ctl = 2 all similar last names will be found.  
	 * @param name
	 * @param ctl 
	 * @return list of persons if list not empty otherweise null!
	 * @throws SQLException
	 */
	public Person[] getSimilarNames(final String name, String ctl) throws SQLException {				
		Person[] list = new Person[size()];
		switch (ctl) { 
			case "1":
				query = "SELECT * FROM personen WHERE vorname like ?";
				break;
			case "2":
				query = "SELECT * FROM personen WHERE nachname like ?";
				break;
		}
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, name + "%");
			try (ResultSet result = ps.executeQuery()) {
				if (result.first()) {
					result.beforeFirst();
					int index = 0;
					while (result.next()) {
						Person person = new Person();
						person.setId(result.getInt(1));
						person.setAnrede(Anrede.fromByte(result.getByte(2)));
						person.setVorname(result.getString(3));
						person.setNachname(result.getString(4));
						person.setStandort(Standort.fromString(result.getString(5)));
						list[index++] = person;
					}
					printList(list);
				}
			} catch (Exception ex) { ex.fillInStackTrace(); }
			return list;
		} catch (Exception ex) {
			System.out.println(" Ein Fehler in Methode getSimilarNames aufgetretten! ");
			ex.getLocalizedMessage();
			ex.fillInStackTrace();
		  }		
		return list;
	}
	
	/**
	 * this method returns number of records in DB.
	 * This would be count of persons who are inserted in table personen.
	 * result.last() returns address of the last line in the table. 
	 * result.getRow() returns id of the last line. 
	 * @return number of the lines as integer
	 * @throws SQLException
	 */
	public int size() throws SQLException  {	
		query = "SELECT * FROM personen";
		try (Statement st = connection.createStatement()) { 
			try (ResultSet result = st.executeQuery(query)) { 
				if (result.first()) { 
					result.last();
					return result.getRow();
				}
			} catch (SQLException ex) {ex.fillInStackTrace();}
		} catch (SQLException ex) {ex.fillInStackTrace();}
		return 0;		
	} 
	
	/**
	 * this method calculates the next free id in the tables personen as following:
	 * the biggest id will be found and finally increased for next new person.  
	 * @return int
	 * @throws SQLException 
	 */
	private int getNextfreeId() throws SQLException {	
		int id = 0;
		query = "SELECT MAX (id) from personen";
		try (Statement st = connection.createStatement()){
			try (ResultSet result = st.executeQuery(query)){
				if (result.next()) {
					id = result.getInt(1);
				}
			} catch (SQLException ex) {ex.fillInStackTrace(); }
		} catch (SQLException ex) {ex.fillInStackTrace(); }
		return ++id;
	}
	
	/**
	 * this is a private method known only for this Repository to print 
	 * all array elements of Person list: Person[].
	 * @param list
	 * @throws SQLException
	 */
	private void printList(Person[] list) throws Exception {
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				if (list[i] != null)
					System.out.println(String.format("Id: %s - %s %s %s %s",list[i].getId(),list[i].getAnrede(),
							list[i].getVorname(),list[i].getNachname(),list[i].getStandort()));
			}
		}
		else 
			System.out.println("Liste ist leer!!");
			System.out.println();
	}	
	
	private void printList(ArrayList<Person> list) throws Exception {		
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null)
					System.out.println(String.format("Id: %s - %s %s %s %s",list.get(i).getId(),list.get(i).getAnrede(),
							list.get(i).getVorname(),list.get(i).getNachname(),list.get(i).getStandort()));
			}
		}
		else 
			System.out.println("Liste ist leer!!");
			System.out.println();
	}	
	
} // Ende Klasse PersonRepository
