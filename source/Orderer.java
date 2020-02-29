import java.util.*; 
import java.sql.*; // Impport Java sql package

public class Orderer {
	
	public int id;
	public String first_name;
	public String last_name;
	private static int count = 1;
	
	public Orderer() {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here the first name of the person that orders: ");
		this.first_name = input.nextLine();
		System.out.println("Then enter here the last name of the orderer: ");
		this.last_name = input.nextLine();
		this.id = count;
	}

	public void printOrderer() {
		System.out.println("Name of the orderer: "+ first_name +" "+ last_name);
		System.out.println("And their ID:"+ id );
	}

	public int inDatabase() throws SQLException {
		int amount;
		int found;

		amount = getMatchingOrdererAmount();
		if (amount < 1) {
			System.out.println("There is no orderer with this name in the database.");
			return (-1);
		} else if (amount == 1) {
			this.id = getOrdererID();
			return(this.id);
		} else {
			Scanner input = new Scanner(System.in);
			System.out.println("Several orderers were found with this name.");
			System.out.println("Please insert the orderer's id: ");
			this.id = input.nextInt();
			found = checkOrdererID();
			if (found > 0)
			{
				return (found);
			} else {
				return (-1);
			}
		}
	}

	public int getOrdererID() throws SQLException {
		int db_id;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

			p = db.prepareStatement("SELECT id FROM Orderer WHERE first_name=? AND last_name=?");
			p.setString(1,this.first_name);
			p.setString(2,this.last_name);

			r = p.executeQuery();
			db_id = r.getInt("id");
			return (db_id);
		} catch (Exception e) {
			System.out.println("Error: getting orderer id failed");
			throw e;
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}

	public int checkOrdererID() throws SQLException {
		int id = -1;
		String db_fname;
		String db_lname;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

			p = db.prepareStatement("SELECT * FROM Orderer WHERE id=?");
			p.setInt(1,this.id);

			r = p.executeQuery();
			if (r.next()) {
				System.out.println("ID "+r.getInt("id")+" was found!");
				db_fname = r.getString("first_name");
				db_lname = r.getString("last_name");
				if (this.first_name.equals(db_fname) && this.last_name.equals(db_lname)) {
					System.out.println("The orderer "+this.first_name+" "+this.last_name+" exists in the database.");
					id = this.id;
				}
				else {
					System.out.println("However, this id does not belong to the orderer "+this.first_name+" "+this.last_name);
				}
			} else {
				System.out.println("This ID represents no orderer in the database.");
			}
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (id);
		}
	}

	public int getMatchingOrdererAmount() throws SQLException {
		int row_nb = -1;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		
		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
	
			p = db.prepareStatement("SELECT COUNT(*) AS rowcount FROM Orderer WHERE first_name=? AND last_name=?");
			p.setString(1,this.first_name);
			p.setString(2,this.last_name);
	
			r = p.executeQuery();
			row_nb = r.getInt("rowcount");	
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (row_nb);
		}
	}


	public void getParcelIDs(int orderer_id) throws SQLException {
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;
		this.id = orderer_id;
		int count = 0;

		 try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			p = db.prepareStatement("SELECT b, COUNT(id) AS event_count FROM (SELECT id AS b FROM Parcel WHERE order_id=?) LEFT JOIN Event ON b=tracing_id GROUP BY b");
			p.setInt(1,this.id);
	
			r = p.executeQuery();
			while (r.next()) {
				System.out.println("Parcel: "+r.getString("b")+" has "+r.getInt("event_count")+" events");
				count++;
			}
			if (count == 0) {
				System.out.println("This orderer has no parcels in the system.");
			}
		 } catch (Exception e) {
			 throw e;
		 } finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		 }
	}

	public void insertOrderer() throws SQLException {
		Connection db = null;
		PreparedStatement p = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			p = db.prepareStatement("INSERT INTO Orderer(id,first_name,last_name) VALUES (?,?,?)");
			p.setInt(1,this.id);
			p.setString(2,this.first_name);
			p.setString(3,this.last_name);

			p.executeUpdate();
			System.out.println("Added the following:");
			this.printOrderer();
			count++;
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
		}
		
	}
}