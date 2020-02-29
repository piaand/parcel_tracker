import java.sql.*; // Impporta Java sql package
import java.util.*;
import java.text.*;  

public class Ptest {
	int place_amount = 1000;
	int orderer_amount = 1000;
	int parcel_amount = 1000;
	int event_amount = 1000000;
	List<Testplace> tplace;
	List<Testorderer> torderer;
	List<Testparcel> tparcel;
	List<Testevent> tevent;

	public Ptest() {
		List<Testplace> tplace = new ArrayList<>();
		List<Testorderer> torderer = new ArrayList<>();
		List<Testparcel> tparcel = new ArrayList<>();
		List<Testevent> tevent = new ArrayList<>();
		this.tplace = createPlaces(tplace);
		this.torderer = createOrderers(torderer);
		this.tparcel = createParcels(tparcel, torderer);
		this.tevent = createEvents(tevent, tparcel, tplace);
		
	}
	private void measureTime(long start, String mssg) {
		long stop = System.nanoTime();
		long nanos = stop - start;
		double elapsed = (double) nanos/1000000000;
		System.out.println(nanos+" nanoseconds or "+elapsed+" seconds to "+mssg);
	}

	public void queryParcels(String table_name) throws SQLException {
		Random rand = new Random();
		long start = 0;
		Connection db = null;
		PreparedStatement p = null;
		int id = 0;
		int count = 0;
		int queries = 1000;

		 try {
			db = DriverManager.getConnection(table_name);
			p = db.prepareStatement("SELECT COUNT(b) AS parcel_count FROM (SELECT Parcel.id AS b, Orderer.id AS c FROM Orderer LEFT JOIN Parcel ON Parcel.order_id=Orderer.id) WHERE c=?");
			start = System.nanoTime();
			while (count < queries) {
				id = torderer.get(rand.nextInt(torderer.size())).getID();
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

	public void queryEvents(String table_name) throws SQLException {
		Random rand = new Random();
		long start = 0;
		Connection db = null;
		PreparedStatement p = null;
		String id = null;
		int count = 0;
		int queries = 1000;

		 try {
			db = DriverManager.getConnection(table_name);
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

	public void addTestData(String table_name) throws SQLException {
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
		int len_place = this.tplace.size();
		int len_orderer = this.torderer.size();
		int len_parcel = this.tparcel.size();
		int len_event = this.tevent.size();
		int i = 0;
		try {
			db = DriverManager.getConnection(table_name);
			db.setAutoCommit(false);
			start = System.nanoTime();
			p1 = db.prepareStatement("INSERT INTO Place(id,name) VALUES (?,?)");
			
			while (i < len_place) {
				id_place = this.tplace.get(i).getID();
				place = this.tplace.get(i).getName();
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
				id_orderer = this.torderer.get(i).getID();
				f_name = this.torderer.get(i).getfName();
				l_name = this.torderer.get(i).getlName();
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
				id_event = this.tevent.get(i).getID();
				place_id = this.tevent.get(i).getPlaceID();
				parcel_id = this.tevent.get(i).getParcelID();
				desc = this.tevent.get(i).getDescription();
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

	private List<Testplace> createPlaces(List<Testplace> tplace) {
		String name;
		String rootname = "PL";
		int count = 1;
		
		while (count <= place_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			Testplace testPlace = new Testplace(name, count);
			tplace.add(testPlace);
			count++;
		}
		return (tplace);	
	}

	private List<Testorderer> createOrderers(List<Testorderer> torderer) {
		String name;
		String rootname = "O";
		String lastname = "Test";
		int count = 1;
		
		while (count <= orderer_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			Testorderer testOrderer = new Testorderer(name, lastname, count);
			torderer.add(testOrderer);
			count++;
		}
		return (torderer);	
	}

	private List<Testparcel> createParcels(List<Testparcel> tparcel, List<Testorderer> torderer) {
		String name;
		String rootname = "PP";
		int orderer;
		Random rand = new Random();
		int count = 1;
		
		while (count <= parcel_amount) {
			String itoa = Integer.toString(count);
			name = String.join("", rootname, itoa);
			orderer = torderer.get(rand.nextInt(torderer.size())).getID(); 
			Testparcel testParcel = new Testparcel(name, orderer);
			tparcel.add(testParcel);
			count++;
		}
		return (tparcel);	
	}

	private List<Testevent> createEvents(List<Testevent> tevent, List<Testparcel> tparcel, List<Testplace> tplace) {
		Random rand = new Random();
		String parcel;
		int place;
		int count = 1;
		
		while (count <= event_amount) {
			place = tplace.get(rand.nextInt(tplace.size())).getID(); 
			parcel = tparcel.get(rand.nextInt(tparcel.size())).getTrackID(); 
			Testevent testEvent = new Testevent(count, parcel, place);
			tevent.add(testEvent);
			count++;
		}
		return (tevent);	
	}
}