import java.text.*;
import java.util.*;
import java.sql.*;

public class Dashboard {
	static String db_name = "parcels.db";
	static String db_connection = "jdbc:sqlite:";
	static boolean index_on = false;
	
	public static void main(String[] args) throws SQLException {
		boolean inList;
		List<Integer> instructions = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		initDatabase(db_connection, db_name, index_on);
		printWelcome();

		Askinput key = new Askinput("What to do next: ");
		while (key.nb != 9)
		{
			printInstructions();
			key.askQuestionInt();
			while (!(inList = instructions.contains(key.nb))) {
				System.out.println("Nope, try again.");
				key.askQuestionInt();
			}
			switchTable(key.nb);
		}

	}

	public static void switchTable(int key) throws SQLException {
		if (key == 1)
		{
			showContentsDB();
		}
		else if (key == 2)
		{
			addPlacetoDB();
		}
		else if (key == 3)
		{
			addOrderertoDB();
		}
		else if (key == 4)
		{
			addParceltoDB();
		}
		else if (key == 5)
		{
			addEventtoDB();
		}
		else if (key == 6)
		{
			fetchParcelEvents();
		}
		else if (key == 7)
		{
			fetchOrdererParcels();
		}
		else if (key == 8)
		{
			fetchPlaceParcels();
		}
		else if (key == 10)
		{
			performanceTesting();
		}
		else if (key == 9)
		{
			System.out.println("Byebye! System closes now\n");
		}
	}


	public static void showContentsDB() throws SQLException {
		List<String> table_names = Arrays.asList("Place", "Orderer", "Event", "Parcel");

		try {
			System.out.println("Show contents of database.\nThe current tables are");
			table_names.forEach(name -> System.out.println(name));
			Askinput table_name = new Askinput("Enter the name of the table you want to query: ");
			table_name.askQuestionText();
			if (table_names.contains(table_name.text)) {
				queryAll(table_name.text);
				System.out.println("Printing ends.");
			}
			else {
				System.out.println("Please enter a table name mentioned in the list");
			}
		} catch (Exception e) {
			System.out.println("Please try again.");
		}
	}

	public static void addPlacetoDB() throws SQLException {
		System.out.println("Add a new place\n");
		try {
			Place myplace = new Place();
			myplace.insertPlace();
		} catch (Exception e) {
			System.out.println("Error: Adding a new place to database didn't succeed - please try agin.\n");
		}
	}

	public static void addParceltoDB() throws SQLException {
		int orderer_id;
		System.out.println("Add a new parcel\n");
		try {
			Orderer myorderer = new Orderer();
			orderer_id = myorderer.inDatabase();
			if (orderer_id > 0) {
				Parcel myparcel = new Parcel(orderer_id);
				myparcel.insertParcel();
			} else {
				System.out.println("Please enter a unique orderer name or orderer name with representative id.");
			}
		} catch (Exception e) {
			System.out.println("Error: Adding a new orderer to database didn't succeed - please try agin.\n");
		}
	}

	public static void addOrderertoDB() throws SQLException {
		System.out.println("Add a new orderer\n");
		try {
			Orderer myorderer = new Orderer();
			myorderer.insertOrderer();
		} catch (Exception e) {
			System.out.println("Error: Adding a new orderer to database didn't succeed - please try agin.\n");
		}
	}

	public static void addEventtoDB() throws SQLException {
		int orderer_id;
		int place_id;
		System.out.println("Add a new event\n");
		try {
			Place myplace = new Place();
			place_id = myplace.inDatabase();
			if (place_id > 0) {
				Askinput parcel_id = new Askinput("Enter the parcel tracking id: ");
				parcel_id.askQuestionText();
				orderer_id = getParcelOrderer(parcel_id.text);
				if (orderer_id > 0)
				{
					Event myevent = new Event(place_id, parcel_id.text);
					myevent.insertEvent();
				} else {
					System.out.println("This parcel is not in the database.");
				}
			} else {
				System.out.println("This place is not in the database.");
			}
		} catch (Exception e) {
			System.out.println("Error: Adding a new orderer to database didn't succeed - please try agin.\n");
		}
	}

	public static void fetchParcelEvents() throws SQLException {
		System.out.println("Fetch all events of a parcel\n");
		Askinput parcel_id = new Askinput("Enter the parcel tracking id: ");
		parcel_id.askQuestionText();
		getParcelEvents(parcel_id.text);
	}

	public static void fetchOrdererParcels() throws SQLException {
		int orderer_id;
		System.out.println("Fetch amount events of all parcels of the orderer\n");
		Orderer myorderer = new Orderer();
		orderer_id = myorderer.inDatabase();
		if (orderer_id > 0) {
			myorderer.getParcelIDs(orderer_id);
		} else {
			System.out.println("Please enter a unique orderer name or orderer name with representative id.");
		}
	}

	public static void fetchPlaceParcels() throws SQLException {
		int place_id;
		System.out.println("Fetch amount events at a place on a date\n");
		Place myplace = new Place();
		place_id = myplace.inDatabase();
		if (place_id > 0) {
			Askinput date = new Askinput("Enter the date with the format yyyy-mm-dd: ");
			date.askQuestionText();
			myplace.fetchEvents(date.text);
		} else {
			System.out.println("This place is not in the database.");
		}
	}

	public static void runPerformanceTest(boolean index_on, Ptest mytest) throws SQLException {
		initDatabase(db_connection, db_name, index_on);
		mytest.addTestData(db_connection, db_name);
		mytest.queryParcels(db_connection, db_name);
		mytest.queryEvents(db_connection, db_name);
		mytest.deleteTestdata(db_connection, db_name);
	}
	
	public static void performanceTesting() throws SQLException {
		int index;
		Scanner input = new Scanner(System.in);
		System.out.println("Doing performance tests\n");
		System.out.print("Press 0 to run test without index and 1 to run with index: ");
		index = input.nextInt();
		Ptest mytest = new Ptest();
		db_name = "performancetest.db";
		if (index == 0) {
			index_on = false;
			runPerformanceTest(index_on, mytest);
		} else if (index == 1) {
			index_on = true;
			runPerformanceTest(index_on, mytest);
		} else {
			System.out.println("Press either 1 or 0.\n");
		}
	}

	

	public static void queryAll(String table_name) throws SQLException {
		Connection db = null;
		Statement s = null;
		ResultSet r = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			s = db.createStatement();
			if (table_name.equals("Place")) {
				r = s.executeQuery("SELECT * FROM Place");
				while (r.next()) {
					System.out.println(r.getInt("id")+" "+r.getString("name"));
				}
			} else if (table_name.equals("Orderer")) {
				r = s.executeQuery("SELECT * FROM Orderer");
				while (r.next()) {
					System.out.println(r.getInt("id")+" "+r.getString("first_name")+" "+r.getString("last_name"));
				}
			} else if (table_name.equals("Parcel")) {
				r = s.executeQuery("SELECT * FROM Parcel");
				while (r.next()) {
					System.out.println(r.getString("id")+" "+r.getInt("order_id"));
				}
			} else if (table_name.equals("Event")) {
				r = s.executeQuery("SELECT * FROM Event");
				System.out.println("Event print not applied yet");
				/*while (r.next()) {
					System.out.println(r.getString("id")+" "+r.getInt("order_id"));
				}*/
			} else {
				System.out.println("Table name was not found");
			}
		} catch (Exception e) {
			System.out.println("Error: querying tables faced an exception.");
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

	public static void initDatabase(String db_connection, String db_name, boolean index) throws SQLException {
		Connection db = null;
		Statement statement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			statement = db.createStatement();
			statement.execute("CREATE TABLE Parcel (id STRING PRIMARY KEY, order_id INTEGER, FOREIGN KEY(order_id) REFERENCES Orderer(id))");
			statement.execute("CREATE TABLE Orderer (id INTEGR PRIMARY KEY, first_name STRING, last_name STRING)");
			statement.execute("CREATE TABLE Place (id INTEGER PRIMARY KEY, name STRING UNIQUE)");
			statement.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracing_id STRING, place_id INTEGER, event_time STRING, description STRING, FOREIGN KEY(tracing_id) REFERENCES Parcel(id), FOREIGN KEY(place_id) REFERENCES Place(id))");
			placeIndex(index, statement);
		} catch (SQLException e) {
			int error = e.getErrorCode();
			if (error == 1) {
				System.out.print("Using already existing database named "+db_name);
			} else {
				System.out.print("Here the Error: "+error+"\n\nand the end.");
				throw e;
			}
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { statement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

	public static void placeIndex(boolean on, Statement statement) throws SQLException {
		try {
			if (on) {
				statement.execute("CREATE INDEX e_parcel_index ON Event(tracing_id)");
				statement.execute("CREATE INDEX p_orderer_index ON Parcel(order_id)");
			} else {
				statement.execute("DROP INDEX [IF EXISTS] e_parcel_index");
				statement.execute("DROP INDEX [IF EXISTS] p_orderer_index");
			}	
		} catch (SQLException e) {
			int error = e.getErrorCode();
			System.out.print("Error: placing index faced an error: "+error+"\n\nand the end.");
			throw e;
		}
	}


	public static void printInstructions() {
		System.out.println("\n-----------");
		System.out.println("Press 1 to query the contents of parcel database");
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