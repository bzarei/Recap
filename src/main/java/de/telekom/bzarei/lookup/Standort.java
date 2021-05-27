package de.telekom.bzarei.lookup;

public enum Standort {

	BONN, DARMSTADT, BERLIN, KOELN, MAGDEBURG;
	
	public static Standort fromString(String str) {
		switch (str.toUpperCase() ) {
			case "BR":
			case "BE":
			case "BERLIN":
				return BERLIN;
			case "BO":
			case "BN":
			case "BONN":
			case "B":
				return BONN;
			case "D":
			case "DARM":
			case "DA":
			case "DS":
			case "DARMSTADT":
				return DARMSTADT;
			case "K":
			case "KLN":
			case "KN":
			case "KÖLN":
			case "KOELN":
				return KOELN;
			case "M":
			case "MG":
			case "MA":
			case "MAGDEBURG":
			case "MAG":
				return MAGDEBURG;
			default:
				throw new IllegalArgumentException("Unexpected value: " 
						+ str + " - zuläßige Angaben sind nur "
						+ "Köln/Bonn/Magdeburg/Berlin/Darmstadt");	
		}
	}
	
	@Override
	public String toString() {
		switch(this) {
		case BONN: return "Bonn";
		case KOELN: return "Koeln";
		case DARMSTADT: return "Darmstadt";
		case BERLIN: return "Berlin";
		case MAGDEBURG: return "Magdeburg";
		default:
			throw new RuntimeException("Unexpected case!");
		}
	}
}
