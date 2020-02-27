import java.util.*; 
import java.sql.*; // Impporta Java sql package

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