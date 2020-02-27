import java.text.*;
import java.util.*;  // Import utilities like Scanner, lists etc.
import java.sql.*; // Impporta Java sql package

public class Dashboard {
	
	/*would it be better to create a Dashboar object at the beginning?
	or continue with the main class and no variations? */
	
	public static void main(String[] args) throws SQLException {
		int key;

		key = 0;
		printWelcome();
		while (key != 9)
		{
			printInstructions();
			key = askNextStep();
			switchTable(key);
			queryAll("Place");
			queryAll("Orderer");
			queryAll("Parcel");
		}

	}
	
	public static void initTable() throws SQLException {
		Connection db = null;
		Statement s = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			s = db.createStatement();
			s.execute("CREATE TABLE Parcel (id STRING PRIMARY KEY, order_id INTEGER, current_place_id INTEGER, FOREIGN KEY(order_id) REFERENCES Orderer(id), FOREIGN KEY(current_place_id) REFERENCES Place(id))");
			s.execute("CREATE TABLE Orderer (id INTEGER PRIMARY KEY, first_name STRING, last_name STRING)");
			s.execute("CREATE TABLE Place (id INTEGER PRIMARY KEY, name STRING UNIQUE)");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracing_id INTEGER FOREIGN KEY, place_id INTEGER FOREIGN KEY, event_time (date TEXT), description STRING)");
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { s.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
        

	}

	public static int askNextStep() {
		int key;
		boolean inList;
		List<Integer> instructions = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
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
		return (key);
	}

	public static String askParcelId() {
		String id;
		Scanner input = new Scanner(System.in);  // Create a Scanner object
		
		System.out.print("Enter the parcel tracking id: ");
		id = input.nextLine();
		return (id);
	}

	
	public static void switchTable(int key) throws SQLException {

		int orderer_id;
		int place_id;
		String parcel_id;
		if (key == 1)
		{	
			System.out.println("Creating a database\n");
			try {
				initTable();	
			} catch (Exception e) {
				System.out.println( e );
				System.out.println("Error: Creating database didn't succeed - exiting program.\n");
				throw e;
			}
		}
		else if (key == 2)
		{
			System.out.println("Add a new place\n");
			try {
				Place myplace = new Place();
				myplace.insertPlace();
			} catch (Exception e) {
				System.out.println("Error: Adding a new place to database didn't succeed - please try agin.\n");
			}
		}
		else if (key == 3)
		{
			System.out.println("Add a new orderer\n");
			Orderer myorderer = new Orderer();
			myorderer.insertOrderer();
		}
		else if (key == 4)
		{
			System.out.println("Add a new parcel\n");
			Orderer myorderer = new Orderer();
			orderer_id = myorderer.inDatabase();
			if (orderer_id > 0) {
				Parcel myparcel = new Parcel(orderer_id);
				myparcel.insertParcel();
			} else {
				System.out.println("Please enter a unique orderer name or orderer name with representative id.");
			}
		}
		else if (key == 5)
		{
			System.out.println("Add a new event\n");
			Place myplace = new Place();
			place_id = myplace.inDatabase();
			if (place_id > 0) {
				parcel_id = askParcelId();
				orderer_id = getParcelOrderer(parcel_id);
				if (orderer_id > 1)
				{
					Event myevent = new Event(place_id, parcel_id);
					myevent.insertEvent();
				} else {
					System.out.println("This parcel is not in the database.");
				}
			} else {
				System.out.println("This place is not in the database.");
			}
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
		else if (key == 10)
		{
			System.out.println("Doing performance tests\n");
		}
		else if (key == 9)
		{
			System.out.println("Byebye! System closes now\n");
		}
	}
	
	
	public static void queryAll(String table_name) throws SQLException {
		Connection db = null;
		Statement s = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			s = db.createStatement();
			if (table_name == "Place") {
				r = s.executeQuery("SELECT * FROM Place");
				while (r.next()) {
					System.out.println(r.getInt("id")+" "+r.getString("name"));
				}
			} else if (table_name == "Orderer") {
				r = s.executeQuery("SELECT * FROM Orderer");
				while (r.next()) {
					System.out.println(r.getInt("id")+" "+r.getString("first_name")+" "+r.getString("last_name"));
				}
			} else if (table_name == "Parcel") {
				r = s.executeQuery("SELECT * FROM Parcel");
				while (r.next()) {
					System.out.println(r.getString("id")+" "+r.getInt("order_id")+" "+r.getInt("current_place_id"));
				}
			} else {
				// pass
			}
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
			try { s.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}

	public static int getParcelOrderer(String id) {
		int db_orderid = -1;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

			p = db.prepareStatement("SELECT * FROM Parcel WHERE id=?");
			p.setString(1,id);

			r = p.executeQuery();
			db_orderid = r.getInt("order_id");
		} catch (Exception e) {
			System.out.println("Error: this parcel id found no orderer id.");
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_orderid);
		}
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
		System.out.println("Press 10 to run the performance tests");
		System.out.println("-----------\n");
	}
	
	public static void printWelcome() {
		System.out.println("\nWelcome to use the parcel tracker!\n");
		System.out.println("Instructions are printed to the screen after every action.");
	}
}