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
		PreparedStatement p = null;
		ResultSet r = null;
		int count = 0;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

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

	public static int getParcelOrderer(String id, String db_connection, String db_name) {
		int db_orderid = -1;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

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

	public String getParcelid(String db_connection, String db_name) {
		String db_id = null;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			p = db.prepareStatement("SELECT id FROM Parcel WHERE id=?");
			p.setString(1,this.id);

			r = p.executeQuery();
			db_id = r.getString("id");
		} catch (Exception e) {
			System.out.println("Error: getting Parcel id failed");
			throw e;
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_id);
		}
	}

	public void insertParcel(String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			p = db.prepareStatement("INSERT INTO Parcel(id,order_id) VALUES (?,?)");
			p.setString(1,this.id);
			p.setInt(2,this.orderer_id);

			p.executeUpdate();
			System.out.println("Added the following:");
			this.printParcel();
		} catch (Exception e) {
			System.out.println("Error: inserting parcel data faced an exception. Please try again.");
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}