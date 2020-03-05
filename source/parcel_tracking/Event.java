package parcel_tracking;
import java.util.*;
import java.text.*; 
import java.sql.*;
import parcel_tracking.*;

public class Event {
	
	public int id;
	public String parcel_id;
	public int place_id;
	public String description = "description is missing";
	public String timestamp;
	private static int count;
	
	public Event(int place_id, String parcel_id) {
		this.id = count;
		this.parcel_id = parcel_id;
		this.place_id = place_id;
	}

	public static void updateEventCount(String db_connection, String db_name) throws SQLException {
		String connection_param = db_connection + db_name;
		Connection db = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement("SELECT MAX(id) AS max FROM Event");
			result = preparedStatement.executeQuery();
			count = result.getInt("max") + 1;
		} catch (Exception e) {
			System.out.println("Error: updating id count faced an error. System will exit.");
			throw e;
		} finally {
			try { result.close(); } catch (Exception e) { /* ignored */ }
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}

	}

	public void askEventDescription () {
		Askinput event_description = new Askinput("Enter here event description at the moment of scanning: ");
		event_description.askQuestionText();
		this.description = event_description.text;
	}
	
	public void printEvent() {
		System.out.println("Event description: "+ this.description );
		System.out.println("ID:"+ id );
		System.out.println("Was given during scanning "+ this.parcel_id +" parcel at location "+this.place_id+" at the time: "+this.timestamp);
	}

	/* Running count can be bypassed for performance testing */
	public void setID(int id) {
		this.id = id;
	}

	public String getParcelID() {
		return this.parcel_id;
	}

	public int getID() {
		return this.id;
	}

	public int getPlaceID() {
		return this.place_id;
	}

	public String getDescription() {
		return this.description;
	}

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
			p.setString(2,this.parcel_id);
			p.setInt(3,this.place_id);
			p.setString(4,ts);
			p.setString(5,this.description);

			p.executeUpdate();
			this.timestamp = ts;
			System.out.println("Added the following:");
			this.printEvent();
			count++;
		} catch (Exception e) {
			System.out.println("Error: inserting event failed. Please try again.");
			System.out.println( e );
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}