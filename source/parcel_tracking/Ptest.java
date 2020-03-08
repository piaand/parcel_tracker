package parcel_tracking;
import java.sql.*; // Impporta Java sql package
import java.util.*;
import java.text.*;
import java.io.*;  

public class Ptest {
	int place_amount = 1000;
	int orderer_amount = 1000;
	int parcel_amount = 1000;
	int event_amount = 1000000;
	List<Place> test_places;
	List<Orderer> test_orderers;
	List<Testparcel> tparcel;
	List<Event> test_events;

	public Ptest() {
		List<Place> test_places = new ArrayList<>();
		List<Orderer> test_orderers = new ArrayList<>();
		List<Testparcel> tparcel = new ArrayList<>();
		List<Event> test_events = new ArrayList<>();
		this.test_places = createPlaces(test_places);
		this.test_orderers = createOrderers(test_orderers);
		this.tparcel = createParcels(tparcel, test_orderers);
		this.test_events = createEvents(test_events, tparcel, test_places);
		
	}
	private void measureTime(long start, String mssg) {
		long stop = System.nanoTime();
		long nanos = stop - start;
		double elapsed = (double) nanos/1000000000;
		System.out.println(nanos+" nanoseconds or "+elapsed+" seconds to "+mssg);
	}

	public void queryParcels(String db_name, String db_connection) throws SQLException {
		String connection_param = db_name + db_connection;
		Random rand = new Random();
		long start = 0;
		Connection db = null;
		PreparedStatement p = null;
		int id = 0;
		int count = 0;
		int queries = 1000;

		 try {
			db = DriverManager.getConnection(connection_param);
			p = db.prepareStatement("SELECT COUNT(b) AS parcel_count FROM (SELECT Parcel.id AS b, Orderer.id AS c FROM Orderer LEFT JOIN Parcel ON Parcel.order_id=Orderer.id) WHERE c=?");
			start = System.nanoTime();
			while (count < queries) {
				id = test_orderers.get(rand.nextInt(test_orderers.size())).getID();
				p.setInt(1,id);
				p.executeQuery();
				count++;
			}
			measureTime(start, "query amounts of orderers' parcels");
		 } catch (Exception e) {
			System.out.println("Error: performace test faced an exception. Please try again.");
		 } finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		 }
	}

	public void queryEvents(String db_name, String db_connection) throws SQLException {
		String connection_param = db_name + db_connection;
		Random rand = new Random();
		long start = 0;
		Connection db = null;
		PreparedStatement p = null;
		String id = null;
		int count = 0;
		int queries = 1000;

		 try {
			db = DriverManager.getConnection(connection_param);
			p = db.prepareStatement("SELECT COUNT(b) AS event_count FROM (SELECT Event.id AS b, Parcel.id AS c FROM Parcel LEFT JOIN Event ON Parcel.id=Event.tracing_id) WHERE c=?");
			start = System.nanoTime();
			while (count < queries) {
				id = tparcel.get(rand.nextInt(tparcel.size())).getTrackID();
				p.setString(1,id);
				p.executeQuery();
				count++;
			}
			measureTime(start, "query event amounts of parcels");
		 } catch (Exception e) {
			System.out.println("Error: performace test faced an exception. Please try again.");
		 } finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		 }
	}

	public void addTestData(String db_name, String db_connection) throws SQLException {
		String connection_param = db_name + db_connection;
		long start = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp timestamp = null;
		String ts = null;
		Connection db = null;
		PreparedStatement p1 = null;
		PreparedStatement p2 = null;
		PreparedStatement p3 = null;
		PreparedStatement p4 = null;
		int id_place = 0;
		int id_orderer = 0;
		int id_event;
		int orderer_id = 0;
		int place_id = 0;
		String place = null;
		String f_name = null;
		String l_name = null;
		String id_parcel = null;
		String parcel_id = null;
		String desc = null;
		int len_place = this.test_places.size();
		int len_orderer = this.test_orderers.size();
		int len_parcel = this.tparcel.size();
		int len_event = this.test_events.size();
		int i = 0;
		try {
			db = DriverManager.getConnection(connection_param);
			db.setAutoCommit(false);
			start = System.nanoTime();
			p1 = db.prepareStatement("INSERT INTO Place(id,name) VALUES (?,?)");
			
			while (i < len_place) {
				id_place = this.test_places.get(i).getID();
				place = this.test_places.get(i).getName();
				p1.setInt(1,id_place);
				p1.setString(2,place);
				p1.executeUpdate();
				i++;
			}
			measureTime(start, "insert places");

			start = System.nanoTime();
			i = 0;
			p2 = db.prepareStatement("INSERT INTO Orderer(id,first_name,last_name) VALUES (?,?,?)");
			while (i < len_orderer) {
				id_orderer = this.test_orderers.get(i).getID();
				f_name = this.test_orderers.get(i).getfName();
				l_name = this.test_orderers.get(i).getlName();
				p2.setInt(1,id_orderer);
				p2.setString(2,f_name);
				p2.setString(3,l_name);
				p2.executeUpdate();
				i++;
			}
			measureTime(start, "insert orderers");
			
			start = System.nanoTime();
			i = 0;
			p3 = db.prepareStatement("INSERT INTO Parcel(id,order_id) VALUES (?,?)");
			while (i < len_parcel) {
				id_parcel = this.tparcel.get(i).getTrackID();
				orderer_id = this.tparcel.get(i).getOrderer();
				p3.setString(1,id_parcel);
				p3.setInt(2,orderer_id);
				p3.executeUpdate();
				i++;
			}
			measureTime(start, "insert parcels");
			
			start = System.nanoTime();
			i = 0;
			p4 = db.prepareStatement("INSERT INTO Event(id,tracing_id,place_id,event_time,description) VALUES (?,?,?,?,?)");
			while (i < len_event) {
				id_event = this.test_events.get(i).getID();
				place_id = this.test_events.get(i).getPlaceID();
				parcel_id = this.test_events.get(i).getParcelID();
				desc = this.test_events.get(i).getDescription();
				p4.setInt(1,id_event);
				p4.setInt(2,place_id);
				p4.setString(3,parcel_id);
				timestamp = new Timestamp(System.currentTimeMillis());
				ts = sdf.format(timestamp);
				p4.setString(4,ts);
				p4.setString(5,desc);
				p4.executeUpdate();
				i++;
			}
			measureTime(start, "insert events");
			
			db.commit();
		} catch (Exception e) {
			System.out.println("Error: inserting places in perfromance test failed.");
		} finally {
			try { p1.close(); } catch (Exception e) { /* ignored */ }
			try { p2.close(); } catch (Exception e) { /* ignored */ }
			try { p3.close(); } catch (Exception e) { /* ignored */ }
			try { p4.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}

	public void deleteTestdata(String db_name) throws SQLException {
		try {
			File db_file = new File("/src/"+db_name); 
			if (db_file.delete()) { 
            System.out.println("Database file for performace testing deleted successfully."); 
        	} else { 
			System.out.println("Failed to delete the database file.");
			} 
		} catch (Exception e) {
			System.out.println("e");
		}
	}

	private List<Place> createPlaces(List<Place> test_places) {
		String name;
		String rootname = "PL";
		int count = 1;
		
		while (count <= place_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			Place testPlace = new Place(name);
			testPlace.setPlaceID(count);
			test_places.add(testPlace);
			count++;
		}
		return (test_places);	
	}

	private List<Orderer> createOrderers(List<Orderer> test_orderers) {
		String name;
		String rootname = "O";
		String lastname = "Test";
		int count = 1;
		
		while (count <= orderer_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			Orderer testOrderer = new Orderer(name, lastname);
			testOrderer.setOrdererID(count);
			test_orderers.add(testOrderer);
			count++;
		}
		return (test_orderers);	
	}

	private List<Testparcel> createParcels(List<Testparcel> tparcel, List<Orderer> test_orderers) {
		String name;
		String rootname = "PP";
		int orderer;
		Random rand = new Random();
		int count = 1;
		
		while (count <= parcel_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			orderer = test_orderers.get(rand.nextInt(test_orderers.size())).getID(); 
			Testparcel testParcel = new Testparcel(name, orderer);
			tparcel.add(testParcel);
			count++;
		}
		return (tparcel);	
	}

	private List<Event> createEvents(List<Event> test_events, List<Testparcel> tparcel, List<Place> test_places) {
		Random rand = new Random();
		String parcel;
		int place;
		int count = 1;
		
		while (count <= event_amount) {
			place = test_places.get(rand.nextInt(test_places.size())).getID(); 
			parcel = tparcel.get(rand.nextInt(tparcel.size())).getTrackID(); 
			Event testevent = new Event(place, parcel);
			testevent.setID(count);
			test_events.add(testevent);
			count++;
		}
		return (test_events);	
	}
}