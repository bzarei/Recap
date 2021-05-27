package de.telekom.bzarei.lookup;

public enum Anrede {
	
	FRAU, HERR;
	
	public static Anrede fromString(String str) {
	
		switch (str.toUpperCase()) {
		case "F":
		case "FR":
		case "FRAU":
			return FRAU;
		case "H":
		case "HR":
		case "HERR":
			return HERR;
			
		default:
			throw new IllegalArgumentException("\"Unexpected value: " 
					+ str + " - zuläßige Angaben sind nur Frau/F/FR/Herr/H/HR");			
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		case FRAU: return "Frau";
		case HERR: return "Herr";
		default:
			throw new RuntimeException("unexpected case!"); 
		}
	}
		
	public byte toByte() {
		switch(this) {
		case FRAU: return 1;
		case HERR: return 2;
		default:
			throw new RuntimeException("unexpected case!");
		}	
	}
	
	public static Anrede fromByte(byte b) {
		switch(b) {
		case 1: return FRAU;
		case 2: return HERR;
		default:
			throw new RuntimeException("unexpected case!");
		}
	}
}
