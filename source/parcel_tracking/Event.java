package parcel_tracking;
import java.util.*;
import java.text.*; 
import java.sql.*;

public class Event {
	
	public int id;
	public String tracking_id;
	public int place_id;
	public String description = "description is missing";
	public String timestamp;
	private static int count;
	
	public Event(int place_id, String tracking_id) {
		this.id = count;
		this.tracking_id = tracking_id;
		this.place_id = place_id;
	}
	

	public static void updateEventCount(int current_count) throws SQLException {
		count = current_count + 1;
	}

	public void askEventDescription () {
		Askinput event_description = new Askinput("Enter here event description at the moment of scanning: ");
		event_description.askQuestionText();
		this.description = event_description.text;
	}

	public void printEvent() {
		System.out.println("Event description: "+ this.description );
		System.out.println("ID:"+ id );
		System.out.println("Was given during scanning "+ this.tracking_id +" parcel at location "+this.place_id+" at the time: "+this.timestamp);
	}

	/* Running count can be bypassed for performance testing */
	public void setID(int id) {
		this.id = id;
	}

	public String getTrackingID() {
		return this.tracking_id;
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

	public void insertEvent(String db_connection, String db_name) throws SQLException {
		Connection db = null;
		PreparedStatement preparedStatement = null;
		String connection_param = db_connection + db_name;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String event_time = sdf.format(timestamp);

		try {
			db = DriverManager.getConnection(connection_param);
			preparedStatement = db.prepareStatement("INSERT INTO Event(id,tracking_id,place_id,event_time,description) VALUES (?,?,?,?,?)");
			preparedStatement.setInt(1,this.id);
			preparedStatement.setString(2,this.tracking_id);
			preparedStatement.setInt(3,this.place_id);
			preparedStatement.setString(4,event_time);
			preparedStatement.setString(5,this.description);

			preparedStatement.executeUpdate();
			this.timestamp = event_time;
			System.out.println("Added the following:");
			this.printEvent();
			count++;
		} catch (Exception e) {
			System.out.println("Error: inserting event failed. Please try again.");
		} finally {
			try { preparedStatement.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}