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
			queryAll("jdbc:sqlite:parcels.db", "Place");
			queryAll("jdbc:sqlite:parcels.db", "Orderer");
			queryAll("jdbc:sqlite:parcels.db", "Parcel");
		}

	}
	
	public static void initTable(String db_name, int index) throws SQLException {
		Connection db = null;
		Statement s = null;

		try {
			db = DriverManager.getConnection(db_name);
			s = db.createStatement();
			s.execute("CREATE TABLE Parcel (id STRING PRIMARY KEY, order_id INTEGER, FOREIGN KEY(order_id) REFERENCES Orderer(id))");
			s.execute("CREATE TABLE Orderer (id INTEGER PRIMARY KEY, first_name STRING, last_name STRING)");
			s.execute("CREATE TABLE Place (id INTEGER PRIMARY KEY, name STRING UNIQUE)");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracing_id STRING, place_id INTEGER, event_time STRING, description STRING, FOREIGN KEY(tracing_id) REFERENCES Parcel(id), FOREIGN KEY(place_id) REFERENCES Place(id))");
			if (index == 1) {
				s.execute("CREATE INDEX e_parcel_index ON Event(tracing_id)");
				s.execute("CREATE INDEX p_orderer_index ON Parcel(order_id)");
			} else {
				//pass
			}
		} catch (Exception e) {
			System.out.print( e );
			throw e;
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
		Scanner input = new Scanner(System.in);
		
		System.out.print("Enter the parcel tracking id: ");
		id = input.nextLine();
		return (id);
	}

	public static String askDate() {
		String date;
		Scanner input = new Scanner(System.in);

		System.out.print("Enter the date with the format yyyy-mm-dd: ");
		date = input.nextLine();
		return (date);
	}

	
	public static void switchTable(int key) throws SQLException {
		int orderer_id;
		int place_id;
		int index;
		String parcel_id;
		String date;
		String table_name;
		if (key == 1)
		{	
			System.out.println("Creating a database\n");
			try {
				table_name = "jdbc:sqlite:parcels.db";
				initTable(table_name, 0);	
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
				if (orderer_id > 0)
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
			System.out.println("Fetch all events of a parcel\n");
			parcel_id = askParcelId();
			getParcelEvents(parcel_id);
		}
		else if (key == 7)
		{
			System.out.println("Fetch amount events of all parcels of the orderer\n");
			Orderer myorderer = new Orderer();
			orderer_id = myorderer.inDatabase();
			if (orderer_id > 0) {
				myorderer.getParcelIDs(orderer_id);
			} else {
				System.out.println("Please enter a unique orderer name or orderer name with representative id.");
			}
		}
		else if (key == 8)
		{
			System.out.println("Fetch amount events at a place on a date\n");
			Place myplace = new Place();
			place_id = myplace.inDatabase();
			if (place_id > 0) {
				date = askDate();
				myplace.fetchEvents(date);
			} else {
				System.out.println("This place is not in the database.");
			}
		}
		else if (key == 10)
		{
			Scanner input = new Scanner(System.in);
			System.out.println("Doing performance tests\n");
			System.out.print("Press 0 to run test without index and 1 to run with index: ");
			index = input.nextInt();
			Ptest mytest = new Ptest();
			table_name = "jdbc:sqlite:performancetest.db";
			if (index == 0) {
				initTable(table_name, index);
				mytest.addTestData(table_name);
				mytest.queryParcels(table_name);
				mytest.queryEvents(table_name);
			} else if (index == 1) {
				initTable(table_name, index);
				mytest.addTestData(table_name);
				mytest.queryParcels(table_name);
				mytest.queryEvents(table_name);
			} else {
				System.out.println("Press either 1 or 0.\n");
			}
		}
		else if (key == 9)
		{
			System.out.println("Byebye! System closes now\n");
		}
	}
	
	
	public static void queryAll(String db_name, String table_name) throws SQLException {
		Connection db = null;
		Statement s = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection(db_name);
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
					System.out.println(r.getString("id")+" "+r.getInt("order_id"));
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

	public static void getParcelEvents(String id) {
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		int count = 0;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

			p = db.prepareStatement("SELECT * FROM Event WHERE tracing_id=?");
			p.setString(1,id);

			r = p.executeQuery();
			while (r.next()) {
				System.out.println("Event id: "+r.getInt("id")+" at place "+r.getInt("place_id")+" happened "+r.getString("event_time")+"\nDescription: "+r.getString("description"));
				count++;
			}
			if (count == 0) {
				System.out.println("This parcel id has no events in the database.");
			}
		} catch (Exception e) {
			System.out.println("Error: this parcel id found no events.");
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
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
		System.out.println("Press 6 to fetch all the events of a parcel");
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