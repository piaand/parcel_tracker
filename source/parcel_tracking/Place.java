package parcel_tracking;
import java.util.*; 
import java.sql.*;

public class Place {
	
	public int id;
	public String name;
	private static int count = 1;
	
	public Place() {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here the name of the place: ");
		this.name = input.nextLine();
		this.id = count;
	}

	public void printPlace() {
		System.out.println("Name of the place:"+ name );
		System.out.println("ID:"+ id );
	}

	public int inDatabase() throws SQLException {
		int id;

		id = getPlaceid();
		if (id < 1) {
			System.out.println("This place is not in the database.");
			return (-1);
		} else {
			this.id = id;
			return(this.id);
		}
	}

	public int getPlaceid() throws SQLException {
		int db_id = -1;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

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

	public void fetchEvents(String date) throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		int count = 0;

		date = date.replace("!", "!!")
		.replace("%", "!%")
		.replace("_", "!_")
		.replace("[", "![");

		 try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
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

	public void insertPlace() throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
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