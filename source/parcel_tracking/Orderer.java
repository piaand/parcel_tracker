package parcel_tracking;
import java.util.*; 
import java.sql.*;

public class Orderer {
	
	public int id;
	public String first_name;
	public String last_name;
	private static int count = 1;
	
	public Orderer(String name, String lastname) {
		this.id = count;
		this.first_name = name;
		this.last_name = lastname;
	}

	public static String askOrdererFirstname() {
		Askinput orderer_first = new Askinput("Enter here the first name of the person that orders: ");
		orderer_first.askQuestionText();
		return (orderer_first.text);
	}

	public static String askOrdererLastname() {
		Askinput orderer_last = new Askinput("Then enter here the last name of the orderer: ");
		orderer_last.askQuestionText();
		return (orderer_last.text);
}	

	public void setOrdererID(int id) {
		this.id = id;
	}

	public static void updateOrdererCount(int current_count) throws SQLException {
		count = current_count + 1;
	}

	public void printOrderer() {
		System.out.println("Name of the orderer: "+ first_name +" "+ last_name);
		System.out.println("And their ID:"+ id );
	}

	public String getfName() {
		return this.first_name;
	}

	public String getlName() {
		return this.last_name;
	}

	public int getID() {
		return this.id;
	}

	public int inDatabase(String db_connection, String db_name) throws SQLException {
		int amount;
		int found;

		amount = getMatchingOrdererAmount(db_connection, db_name);
		if (amount < 1) {
			System.out.println("There is no orderer with this name in the database.");
			return (-1);
		} else if (amount == 1) {
			this.id = getOrdererID(db_connection, db_name);
			return(this.id);
		} else {
			Scanner input = new Scanner(System.in);
			System.out.println("Several orderers were found with this name.");
			System.out.println("Please insert the orderer's id: ");
			this.id = input.nextInt();
			found = checkOrdererID(db_connection, db_name);
			if (found > 0)
			{
				return (found);
			} else {
				return (-1);
			}
		}
	}

	public int getOrdererID(String db_connection, String db_name) throws SQLException {
		int db_id;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			preparedStatement = db.prepareStatement("SELECT id FROM Orderer WHERE first_name=? AND last_name=?");
			preparedStatement.setString(1,this.first_name);
			preparedStatement.setString(2,this.last_name);

			result = preparedStatement.executeQuery();
			db_id = result.getInt("id");
			return (db_id);
		} catch (Exception e) {
			System.out.println("Error: getting orderer id failed");
			throw e;
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
    		try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}

	public int checkOrdererID(String db_connection, String db_name) throws SQLException {
		int id = -1;
		String db_fname;
		String db_lname;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			preparedStatement = db.prepareStatement("SELECT * FROM Orderer WHERE id=?");
			preparedStatement.setInt(1,this.id);

			result = preparedStatement.executeQuery();
			if (result.next()) {
				System.out.println("ID "+result.getInt("id")+" was found!");
				db_fname = result.getString("first_name");
				db_lname = result.getString("last_name");
				if (this.first_name.equals(db_fname) && this.last_name.equals(db_lname)) {
					System.out.println("The orderer "+this.first_name+" "+this.last_name+" exists in the database.");
					id = this.id;
				}
				else {
					System.out.println("However, this id does not belong to the orderer "+this.first_name+" "+this.last_name);
				}
			} else {
				System.out.println("This ID represents no orderer in the database.");
			}
		} catch (Exception e) {
			System.out.println("Error: checking orderer id faced an exception. Please try again.");
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (id);
		}
	}

	public int getMatchingOrdererAmount(String db_connection, String db_name) throws SQLException {
		int row_nb = -1;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;
		
		try {
			db = DriverManager.getConnection(connection_param);
	
			preparedStatement = db.prepareStatement("SELECT COUNT(*) AS rowcount FROM Orderer WHERE first_name=? AND last_name=?");
			preparedStatement.setString(1,this.first_name);
			preparedStatement.setString(2,this.last_name);
	
			result = preparedStatement.executeQuery();
			row_nb = r.getInt("rowcount");	
		} catch (Exception e) {
			System.out.println("Error: finding orderer with name faced an exception. Please try again.");
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (row_nb);
		}
	}


	public void getParcelIDs(int orderer_id, String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		this.id = orderer_id;
		int count = 0;
		String connection_param = db_connection + db_name;

		 try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement("SELECT b, COUNT(id) AS event_count FROM (SELECT id AS b FROM Parcel WHERE orderer_id=?) LEFT JOIN Event ON b=tracking_id GROUP BY b");
			preparedStatement.setInt(1,this.id);
	
			result = preparedStatement.executeQuery();
			while (r.next()) {
				System.out.println("Parcel: "+r.getString("b")+" has "+r.getInt("event_count")+" events");
				count++;
			}
			if (count == 0) {
				System.out.println("This orderer has no parcels in the system.");
			}
		 } catch (Exception e) {
			System.out.println("Error: getting parcels from orderer faced an exception. Please try again.");
		 } finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		 }
	}

	public void insertOrderer(String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement preparedStatement = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement("INSERT INTO Orderer(id,first_name,last_name) VALUES (?,?,?)");
			preparedStatement.setInt(1,this.id);
			preparedStatement.setString(2,this.first_name);
			preparedStatement.setString(3,this.last_name);

			preparedStatement.executeUpdate();
			System.out.println("Added the following:");
			this.printOrderer();
			count++;
		} catch (Exception e) {
			System.out.println("Error: inserting orderer faced an exception. Please try again.");
		} finally {
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}
}