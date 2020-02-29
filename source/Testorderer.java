public class Testorderer {
	String fname;
	String lname;
	int id;

	public Testorderer(String name, String lastname, int id) {
		this.fname = name;
		this.lname = lastname;
		this.id = id;
	}

	public String getfName() {
		return this.fname;
	}

	public String getlName() {
		return this.lname;
	}

	public int getID() {
		return this.id;
	}
}