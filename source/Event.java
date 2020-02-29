import java.util.*;
import java.text.*; 
import java.sql.*; 

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

}