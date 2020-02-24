import java.util.*;  // Import utilities like Scanner, lists etc.
import java.sql.*; // Impporta Java sql package

public class Dashboard {
	
	/*would it be better to create a Dashboar object at the beginning?
	or continue with the main class and no variations? */

	public static void main(String[] args) {
		int key;
		boolean error;

		key = 0;
		error = false;
		printWelcome();
		while (key != 9 && (!error))
		{
			printInstructions();
			key = askNextStep();
			error = switchTable(key);
		}

	}
	
	public static boolean initTable() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:testi.db");
        Statement s = db.createStatement();
        s.execute("CREATE TABLE Tuotteet (id INTEGER PRIMARY KEY, nimi TEXT, hinta INTEGER)");
        s.execute("INSERT INTO Tuotteet (nimi,hinta) VALUES ('retiisi',7)");
        s.execute("INSERT INTO Tuotteet (nimi,hinta) VALUES ('porkkana',5)");
        s.execute("INSERT INTO Tuotteet (nimi,hinta) VALUES ('nauris',4)");
        s.execute("INSERT INTO Tuotteet (nimi,hinta) VALUES ('lanttu',8)");
        s.execute("INSERT INTO Tuotteet (nimi,hinta) VALUES ('selleri',4)");

        ResultSet r = s.executeQuery("SELECT * FROM Tuotteet");
        while (r.next()) {
            System.out.println(r.getInt("id")+" "+r.getString("nimi")+" "+r.getInt("hinta"));
		}
	}

	public static int askNextStep() {
		int key;
		boolean inList;
		List<Integer> instructions = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		Scanner input = new Scanner(System.in);  // Create a Scanner object
		
		System.out.print("What to do next?: ");
		key = input.nextInt();
		while (!(inList = instructions.contains(key)))
		{
			/* this works only with the integers - 
			no exception thrown when one tries to add anytthing else
			also beyond max int/min int causes issues*/
			System.out.println("Nope! Try again: ");
			key = input.nextInt();
		}
		input = null;
		return (key);
	}

	
	public static boolean switchTable(int key) {
		boolean ok;
		
		ok = true;
		if (key == 1)
		{	
			System.out.println("Creating a database\n");
			initTable();
		}
		else if (key == 2)
		{
			System.out.println("Add a new place\n");
		}
		else if (key == 3)
		{
			System.out.println("add a new orderer\n");
		}
		else if (key == 4)
		{
			System.out.println("ADD A NEW APARCEL\n");
		}
		else if (key == 5)
		{
			System.out.println("add a new event\n");
		}
		else if (key == 6)
		{
			System.out.println("fetch all parcel events\n");
		}
		else if (key == 7)
		{
			System.out.println("fetch  all parcels of the orderer\n");
		}
		else if (key == 8)
		{
			System.out.println("fetch nnumber of events related to av place\n");
		}
		else
		{
			System.out.println("Byebye! System closes now\n");
		}
		return (ok);
	}
	
	public static void printInstructions() {
		System.out.println("\n-----------");
		System.out.println("Press 1 to create a new database");
		System.out.println("Press 2 to add a new place for parcel to be delivered");
		System.out.println("Press 3 to add orderer's information");
		System.out.println("Press 4 to add a new parcel to be delivered");
		System.out.println("Press 5 to add a new scanning event for the parcel");
		System.out.println("Press 6 to fetch all the events of parcel");
		System.out.println("Press 7 to fetch all the parcels and amount of events of an orderer");
		System.out.println("Press 8 to fetch the number of events related to a place");
		System.out.println("Press 9 to exit program");
		System.out.println("-----------\n");
	}
	
	public static void printWelcome() {
		System.out.println("\nWelcome to use the parcel tracker!\n");
		System.out.println("Instructions are printed to the screen after every action.");
	}
}