package parcel_tracking;
import java.util.*; 
import java.sql.*;

public class Place {
	
	public int id;
	public String name;
	private static int count = 1;
	
	public Place(String place_name) {
		this.name = place_name;
		this.id = count;
	}

	public static String askPlace() {
		Askinput place = new Askinput("Enter here the name of the place: ");
		place.askQuestionText();
		return (place.text);
	}

	public void setPlaceID(int id) {
		this.id = id;
	}

	public void printPlace() {
		System.out.println("Name of the place:"+ name );
		System.out.println("ID:"+ id );
	}

	public static void updatePlaceCount(int current_count) throws SQLException {
		count = current_count + 1;
	}

	public String getName() {
		return (this.name);
	}

	public int getID() {
		return (this.id);
	}

	public int inDatabase(String db_connection, String db_name) throws SQLException {
		int id;

		id = getPlaceid(db_connection, db_name);
		if (id < 1) {
			System.out.println("This place is not in the database.");
			return (-1);
		} else {
			this.id = id;
			return(this.id);
		}
	}

	public int getPlaceid(String db_connection, String db_name) throws SQLException {
		int db_id = -1;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);

			p = db.prepareStatement("SELECT id FROM Place WHERE name=?");
			p.setString(1,this.name);

			r = p.executeQuery();
			db_id = r.getInt("id");
		} catch (Exception e) {
			System.out.println("Error: getting Place id failed");
			throw e;
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_id);
		}
	}

	public void fetchEvents(String date, String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		int count = 0;
		String connection_param = db_connection + db_name;

		date = date.replace("!", "!!")
		.replace("%", "!%")
		.replace("_", "!_")
		.replace("[", "![");

		 try {
			db = DriverManager.getConnection(connection_param);
			p = db.prepareStatement("SELECT b, name, COUNT(id) AS event_count FROM (SELECT id AS b, name FROM Place WHERE id=?) LEFT JOIN (SELECT id, place_id FROM Event WHERE event_time LIKE ? ESCAPE '!') ON b=place_id GROUP BY b");
			p.setInt(1,this.id);
			p.setString(2, date + "%");
	
			r = p.executeQuery();
			while (r.next()) {
				System.out.println("Place: "+r.getString("name")+" has "+r.getInt("event_count")+" events");
				count++;
			}
			if (count == 0) {
				System.out.println("This place has no scanning events on the date "+date+" in the system.");
			}
		 } catch (Exception e) {
			System.out.println("Error: Failed to fetch event amounts. Please try again.");
		 } finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		 }
	}

	public void insertPlace(String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		String connection_param = db_connection + db_name;

		try {
			db = DriverManager.getConnection(connection_param);
			p = db.prepareStatement("INSERT INTO Place(id,name) VALUES (?,?)");
			p.setInt(1,this.id);
			p.setString(2,this.name);

			p.executeUpdate();
			System.out.println("Added the following:");
			this.printPlace();
			count++;
		} catch (Exception e) {
			System.out.println("Error: inserting place failed. Please insert unique name for place and try again.");
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}

}