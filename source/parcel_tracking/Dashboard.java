package parcel_tracking;
import java.text.*;
import java.util.*;
import java.sql.*;

public class Dashboard {
	static List<String> table_names = Arrays.asList("Place", "Orderer", "Event", "Parcel");
	static String db_name = "parcels.db";
	static String db_connection = "jdbc:sqlite:";
	static boolean index_on = false;
	
	public static void main(String[] args) throws SQLException {
		try  {
			int i = 0;
			int count;

			printWelcome();
			initDatabase(db_connection, db_name, index_on);
			while (i < 4) {
				String name = table_names.get(i);
				updateCount(db_connection, db_name, name);
				i++;
			}

			int key = 0;
			while (key != 9)
			{
				key = runInstruction(key);
			}
		} catch (SQLException e) {
			System.out.println("Error: system will close");
			throw e;
		}
	}

	public static int runInstruction(int key_nb) throws SQLException {
		boolean is_int;
		boolean inList;
		List<Integer> instructions = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		Askinput key = new Askinput("What to do next: ");
		printInstructions();
		is_int = key.askQuestionInt();
		key_nb = key.nb;
		while (is_int && !(inList = instructions.contains(key_nb))) {
			System.out.println("Nope, try again.");
			is_int = key.askQuestionInt();
		}
		if (is_int) {
			switchTable(key_nb);
		}
		return(key_nb);
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
		String place_name;
		try {
			place_name = Place.askPlace();
			Place myplace = new Place(place_name);
			myplace.insertPlace(db_connection, db_name);
		} catch (Exception e) {
			System.out.println("Error: Adding a new place to database didn't succeed - please try agin.\n");
		}
	}

	public static void addParceltoDB() throws SQLException {
		int orderer_id;
		String parcel_id;

		System.out.println("Add a new parcel\n");
		try {
			String first_name = Orderer.askOrdererFirstname();
			String last_name = Orderer.askOrdererLastname();
			Orderer myorderer = new Orderer(first_name, last_name);
			orderer_id = myorderer.inDatabase(db_connection, db_name);
			if (orderer_id > 0) {
				parcel_id = Parcel.askParcelID();
				Parcel myparcel = new Parcel(parcel_id, orderer_id);
				myparcel.insertParcel(db_connection, db_name);
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
			String first_name = Orderer.askOrdererFirstname();
			String last_name = Orderer.askOrdererLastname();
			Orderer myorderer = new Orderer(first_name, last_name);
			myorderer.insertOrderer(db_connection, db_name);
		} catch (Exception e) {
			System.out.println("Error: Adding a new orderer to database didn't succeed - please try again.\n");
		}
	}

	public static void addEventtoDB() throws SQLException {
		int orderer_id;
		int place_id;
		String place_name;
		String parcel_id;

		System.out.println("Add a new event\n");
		try {
			place_name = Place.askPlace();
			Place myplace = new Place(place_name);
			place_id = myplace.inDatabase(db_connection, db_name);
			if (place_id > 0) {
				parcel_id = Parcel.askParcelID();
				orderer_id = Parcel.getParcelOrderer(parcel_id, db_connection, db_name);
				if (orderer_id > 0)
				{
					Event myevent = new Event(place_id, parcel_id);
					myevent.askEventDescription();
					myevent.insertEvent(db_connection, db_name);
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
		String parcel_id;

		System.out.println("Fetch all events of a parcel\n");
		parcel_id = Parcel.askParcelID();
		Parcel.getParcelEvents(parcel_id, db_connection, db_name);
	}

	public static void fetchOrdererParcels() throws SQLException {
		int orderer_id;
		System.out.println("Fetch amount events of all parcels of the orderer\n");
		String first_name = Orderer.askOrdererFirstname();
		String last_name = Orderer.askOrdererLastname();
		Orderer myorderer = new Orderer(first_name, last_name);
		orderer_id = myorderer.inDatabase(db_connection, db_name);
		if (orderer_id > 0) {
			myorderer.getParcelIDs(orderer_id, db_connection, db_name);
		} else {
			System.out.println("Please enter a unique orderer name or orderer name with representative id.");
		}
	}

	public static void fetchPlaceParcels() throws SQLException {
		int place_id;
		String place_name;
		System.out.println("Fetch amount events at a place on a date\n");
		place_name = Place.askPlace();
		Place myplace = new Place(place_name);
		place_id = myplace.inDatabase(db_connection, db_name);
		if (place_id > 0) {
			Askinput date = new Askinput("Enter the date with the format yyyy-mm-dd: ");
			date.askQuestionText();
			myplace.fetchEvents(date.text, db_connection, db_name);
		} else {
			System.out.println("This place is not in the database.");
		}
	}

	public static void runPerformanceTest(boolean index_on, Ptest mytest) throws SQLException {
		String db_name_test = "performancetest.db";
		initDatabase(db_connection, db_name_test, index_on);
		mytest.addTestData(db_connection, db_name_test);
		mytest.queryParcels(db_connection, db_name_test);
		mytest.queryEvents(db_connection, db_name_test);
		mytest.deleteTestdata(db_name_test);
	}
	
	public static void performanceTesting() throws SQLException {
		boolean is_int = false;
		int index_nb = -1;

		System.out.println("Doing performance tests with 1000 places, 1000 parcels, 1000 orderers and 1 million events.\n");
		while (!is_int) {
			Askinput index = new Askinput("Press 0 to run test without index and 1 to run with index: ");
			is_int = index.askQuestionInt();
			if (is_int) {
				index_nb = index.nb;
			}
		}
		Ptest mytest = new Ptest();
		if (index_nb == 0) {
			index_on = false;
			runPerformanceTest(index_on, mytest);
		} else if (index_nb == 1) {
			index_on = true;
			runPerformanceTest(index_on, mytest);
		} else {
			System.out.println("Press either 1 or 0.\n");
		}
	}

	public static void queryAll(String table_name) throws SQLException {
		Connection db = null;
		Statement statement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			statement = db.createStatement();
			if (table_name.equals("Place")) {
				result = statement.executeQuery("SELECT * FROM Place");
				while (result.next()) {
					System.out.println(result.getInt("id")+" "+result.getString("name"));
				}
			} else if (table_name.equals("Orderer")) {
				result = statement.executeQuery("SELECT * FROM Orderer");
				while (result.next()) {
					System.out.println(r.getInt("id")+" "+result.getString("first_name")+" "+result.getString("last_name"));
				}
			} else if (table_name.equals("Parcel")) {
				result = statement.executeQuery("SELECT * FROM Parcel");
				while (result.next()) {
					System.out.println(result.getString("id")+" "+result.getInt("orderer_id"));
				}
			} else if (table_name.equals("Event")) {
				result = statement.executeQuery("SELECT * FROM Event");
				while (result.next()) {
					System.out.println(result.getInt("id")+" "+result.getString("tracking_id")+" "+result.getInt("place_id")+" "+result.getString("event_time")+" "+result.getString("description"));
				}
			} else {
				System.out.println("Table name was not found");
			}
		} catch (Exception e) {
			System.out.println("Error: querying tables faced an exception.");
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { statement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}


	public static void initDatabase(String db_connection, String db_name, boolean index) throws SQLException {
		Connection db = null;
		Statement statement = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			statement = db.createStatement();
			statement.execute("CREATE TABLE Parcel (id STRING UNIQUE PRIMARY KEY, orderer_id INTEGER, FOREIGN KEY(orderer_id) REFERENCES Orderer(id))");
			statement.execute("CREATE TABLE Orderer (id INTEGR PRIMARY KEY, first_name STRING, last_name STRING)");
			statement.execute("CREATE TABLE Place (id INTEGER PRIMARY KEY, name STRING UNIQUE)");
			statement.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracking_id STRING, place_id INTEGER, event_time STRING, description STRING, FOREIGN KEY(tracking_id) REFERENCES Parcel(id), FOREIGN KEY(place_id) REFERENCES Place(id))");
		} catch (SQLException e) {
			int error = e.getErrorCode();
			if (error == 1) {
				System.out.print("Using already existing database named "+db_name);
			} else {
				System.out.print("Here the Error: "+error+"\n\nand the end.");
				throw e;
			}
		} try {
			placeIndex(index, statement);
		} catch (SQLException e) {
			int error = e.getErrorCode();
			System.out.print("Error: placing index faced an error: "+error+"\n\nand the end.");
			System.out.print( e );
		} finally {
			try { statement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

	public static void placeIndex(boolean on, Statement statement) throws SQLException {
		try {
			if (on) {
				statement.execute("CREATE INDEX e_parcel_index ON Event(tracking_id)");
				statement.execute("CREATE INDEX p_orderer_index ON Parcel(orderer_id)");
			} else {
				statement.execute("DROP INDEX IF EXISTS e_parcel_index");
				statement.execute("DROP INDEX IF EXISTS p_orderer_index");
			}	
		} catch (SQLException e) {
			throw e;
		}
	}

	public static void updateCount(String db_connection, String db_name, String table_name) throws SQLException {
		String event_query = "SELECT COALESCE(MAX(id),0) AS max FROM Event";
		String parcel_query = "SELECT COALESCE(MAX(id),0) AS max FROM Parcel";
		String place_query = "SELECT COALESCE(MAX(id),0) AS max FROM Place";
		String orderer_query = "SELECT COALESCE(MAX(id),0) AS max FROM Orderer";
		int count;
		try {
			if (table_name == "Event") {
				count = getCurrentCount(db_connection, db_name, event_query);
				Event.updateEventCount(count);
			} else if (table_name == "Parcel") {
				count = getCurrentCount(db_connection, db_name, parcel_query);
				Parcel.updateParcelCount(count);
			} else if (table_name == "Place") {
				count = getCurrentCount(db_connection, db_name, place_query);
				Place.updatePlaceCount(count);
			} else if (table_name == "Orderer") {
				count = getCurrentCount(db_connection, db_name, orderer_query);
				Orderer.updateOrdererCount(count);
			} else {
				System.out.print("This table name doesn't exist.");
			}	
		} catch (Exception e) {
			throw e;
		}
	}

	public static int getCurrentCount(String db_connection, String db_name, String query) throws SQLException {
		String connection_param = db_connection + db_name;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		int count = 0;

		try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement(query);
			result = preparedStatement.executeQuery();
			count = result.getInt("max");
		} catch (Exception e) {
			System.out.println("Error: updating id count faced an error. System will exit.");
			System.out.println( e );
			throw e;
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (count);
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