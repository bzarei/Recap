package de.telekom.bzarei.model;

import de.telekom.bzarei.lookup.*;

public class Person {
	
	private int id;
	private String vorname;
	private String nachname;
	private Anrede anrede; 
	private Standort standort;
	
	// constructor
	public Person() {
	}
	
	// Constructor
	public Person(int id, Anrede anrede, String vorname, String nachname, Standort standort) {
		setId(id);
		setAnrede(anrede);
		setVorname(vorname);
		setNachname(nachname);
		setStandort(standort);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setAnrede(Anrede anrede) {	
		this.anrede = anrede;
	}
	
	public void setStandort(Standort standort) {	
		this.standort = standort;
	}
	
	public void setVorname(String name) {
		vorname = name;
	}
	
	public void setNachname(String name) {
		nachname = name;
	}
	
	public int getId() {
		return id;
	}
	
	public Anrede getAnrede() {
		return anrede;
	}
	
	public Standort getStandort() {
		return standort;
	}
	
	public String getVorname() {
		return vorname;
	}
	
	public String getNachname() {
		return nachname;
	}
	
}
