import java.util.*;
import java.text.*; 
import java.sql.*; // Impporta Java sql package

public class Event {
	
	public int id;
	public String track_id;
	public int place_id;
	public String desc;
	public String timestamp;
	private static int count = 1;
	
	public Event(int place_id, String track_id) {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here event description at the moment of scanning: ");
		this.desc = input.nextLine();
		this.place_id = place_id;
		this.track_id = track_id;
		this.id = count;
	}

	public void printEvent() {
		System.out.println("Event description: "+ desc );
		System.out.println("ID:"+ id );
		System.out.println("Was given during scanning "+ this.track_id +" parcel at location "+this.place_id+" at the time: "+this.timestamp);
	}

	/*public int inDatabase() throws SQLException {
		int id;

		id = getPlaceid();
		if (id < 1) {
			System.out.println("This place is not in the database.");
			return (-1);
		} else {
			this.id = id;
			return(this.id);
	}*/

	/*public int getPlaceid() throws SQLException {
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
		} finally {*/
	//		try { r.close(); } catch (Exception e) { /* ignored */ }
    //		try { p.close(); } catch (Exception e) { /* ignored */ }
	//		try { db.close(); } catch (Exception e) { /* ignored */ }
	/*		return (db_id);
		}
	}*/

	public void insertEvent() throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String ts = sdf.format(timestamp);

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			p = db.prepareStatement("INSERT INTO Event(id,tracing_id,place_id,event_time,description) VALUES (?,?,?,?,?)");
			p.setInt(1,this.id);
			p.setString(2,this.track_id);
			p.setInt(3,this.place_id);
			p.setString(4,ts);
			p.setString(5,this.desc);

			p.executeUpdate();
			this.timestamp = ts;
			System.out.println("Added the following:");
			this.printEvent();
			count++;
		} catch (Exception e) {
			System.out.println("Error: inserting event failed. Please try again.");
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
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

}