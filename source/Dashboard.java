import java.text.*;
import java.util.*;  // Import utilities like Scanner, lists etc.
import java.sql.*; // Impporta Java sql package

public class Dashboard {
	
	/*would it be better to create a Dashboar object at the beginning?
	or continue with the main class and no variations? */

	public static void main(String[] args) throws SQLException {
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
	
	public static void initTable() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
		Statement s = db.createStatement();
        s.execute("CREATE TABLE Parcel (id INTEGER PRIMARY KEY, order_id INTEGER, current_place_id INTEGER, FOREIGN KEY(order_id) REFERENCES Orderer(id), FOREIGN KEY(current_place_id) REFERENCES Place(id))");
		s.execute("CREATE TABLE Orderer (id INTEGER PRIMARY KEY, first_name STRING, last_name STRING)");
		s.execute("CREATE TABLE Place (id INTEGER PRIMARY KEY, name STRING)");
		//s.execute("CREATE TABLE Events (id INTEGER PRIMARY KEY, tracing_id INTEGER FOREIGN KEY, place_id INTEGER FOREIGN KEY, event_time (date TEXT), description STRING)");

		s.execute("INSERT INTO Orderer (id,first_name,last_name) VALUES (1,'Jukka','Kuoppanen')");
		s.execute("INSERT INTO Place (id,name) VALUES (2,'Helsinki')");
		s.execute("INSERT INTO Parcel (id,order_id,current_place_id) VALUES (10,8,12)");

        ResultSet r = s.executeQuery("SELECT * FROM Orderer");
        while (r.next()) {
            System.out.println(r.getInt("id")+" "+r.getString("first_name")+" "+r.getString("last_name"));
		}
		ResultSet m = s.executeQuery("SELECT * FROM Parcel");
		while (m.next()) {
            System.out.println(m.getInt("id")+" "+m.getInt("order_id")+" "+m.getInt("current_place_id"));
		}
	}

	/*public static void insertPlace() throws SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String ts = sdf.format(timestamp);
		s.execute("INSERT INTO Events (id,tracing_id,place_id,event_time,description) VALUES (7, 70001, 3, ?, 'we describe her')"); {
        	s.setString(1, ts);
		}

		ResultSet m = ps.executeQuery("SELECT * FROM Events");
        while (m.next()) {
            System.out.println(m.getInt("id")+" "+m.getInt("tracing_id")+" "+m.getInt("place_id")+" "+m.getString("event_time")+" "+m.getString("description"));
		}
	}*/

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

	
	public static boolean switchTable(int key) throws SQLException {
		boolean error;
		
		error = false;
		if (key == 1)
		{	
			System.out.println("Creating a database\n");
			try {
				initTable();	
			} catch (Exception e) {
				System.out.println( e );
				System.out.println("Creating database didn't succeed - exiting program.\n");
				throw e;
			}
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
		return (error);
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