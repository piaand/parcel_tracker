package parcel_tracking;
import java.util.*; 
import java.sql.*;

public class Parcel {
	
	public String id;
	public int orderer_id;
	private static int count = 1;
	
	public Parcel (String tracking_id, int orderer_id) {
		this.id = tracking_id;
		this.orderer_id = orderer_id;
	}

	public static String askParcelID() {
		Askinput parcel = new Askinput("Enter here the parcel tracking code: ");
		parcel.askQuestionText();
		return (parcel.text);
	}

	public static void updateParcelCount(int current_count) throws SQLException {
		count = current_count + 1;
	}

	public void printParcel() {
		System.out.println("Parcel id: "+ this.id );
		System.out.println("From the orderer whos id is "+ this.orderer_id);
	}

	public String getTrackID() {
		return this.id;
	}

	public int getOrderer() {
		return this.orderer_id;
	}

	public String inDatabase(String db_connection, String db_name) throws SQLException {
		String id;

		id = getParcelid(db_connection, db_name);
		if (id == null) {
			System.out.println("This parcel is not in the database.");
			return (null);
		} else {
			this.id = id;
			return(this.id);
		}
	}

	public static void getParcelEvents(String id, String db_connection, String db_name) {
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		int count = 0;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			preparedStatement = db.prepareStatement("SELECT * FROM Event WHERE tracking_id=?");
			preparedStatement.setString(1,id);

			result = preparedStatement.executeQuery();
			while (result.next()) {
				System.out.println("Event id: "+result.getInt("id")+" at place "+result.getInt("place_id")+" happened "+result.getString("event_time")+"\nDescription: "+result.getString("description"));
				count++;
			}
			if (count == 0) {
				System.out.println("This parcel id has no events in the database.");
			}
		} catch (Exception e) {
			System.out.println("Error: this parcel id found no events.");
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
    		try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

	public static int getParcelOrderer(String id, String db_connection, String db_name) {
		int db_orderid = -1;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			preparedStatement = db.prepareStatement("SELECT * FROM Parcel WHERE id=?");
			preparedStatement.setString(1,id);

			result = preparedStatement.executeQuery();
			db_orderid = result.getInt("orderer_id");
		} catch (Exception e) {
			System.out.println("Error: this parcel id found no orderer id.");
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
    		try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_orderid);
		}
	}

	public String getParcelid(String db_connection, String db_name) {
		String db_id = null;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			preparedStatement = db.prepareStatement("SELECT id FROM Parcel WHERE id=?");
			preparedStatement.setString(1,this.id);

			result = preparedStatement.executeQuery();
			db_id = result.getString("id");
		} catch (Exception e) {
			System.out.println("Error: getting Parcel id failed");
			throw e;
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
    		try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_id);
		}
	}

	public void insertParcel(String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement preparedStatement = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement("INSERT INTO Parcel(id,orderer_id) VALUES (?,?)");
			preparedStatement.setString(1,this.id);
			preparedStatement.setInt(2,this.orderer_id);

			preparedStatement.executeUpdate();
			System.out.println("Added the following:");
			this.printParcel();
		} catch (Exception e) {
			System.out.println("Error: inserting parcel data faced an exception. Please try again.");
		} finally {
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}