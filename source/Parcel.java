import java.util.*; 
import java.sql.*; // Import Java sql package

public class Parcel {
	
	public String id;
	public int order_id;;
	private static int count = 1;
	
	public Parcel (int orderer_id) {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here the parcel tracking code: ");
		this.id = input.nextLine();
		this.order_id = orderer_id;
	}

	public void printParcel() {
		System.out.println("Parcel id: "+ this.id );
		System.out.println("From the orderer whos id is "+ this.order_id);
	}

	public String inDatabase() throws SQLException {
		String id;

		id = getParcelid();
		if (id == null) {
			System.out.println("This parcel is not in the database.");
			return (null);
		} else {
			this.id = id;
			return(this.id);
		}
	}

	public String getParcelid() {
		String db_id = null;
		Connection db = null;
		PreparedStatement p = null;
		ResultSet r = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");

			p = db.prepareStatement("SELECT id FROM Parcel WHERE id=?");
			p.setString(1,this.id);

			r = p.executeQuery();
			db_id = r.getString("id");
		} catch (Exception e) {
			System.out.println("Error: getting Parcel id failed");
			throw e;
		} finally {
			try { r.close(); } catch (Exception e) { /* ignored */ }
    		try { p.close(); } catch (Exception e) { /* ignored */ }
			try { db.close(); } catch (Exception e) { /* ignored */ }
			return (db_id);
		}
	}


	public void insertParcel() throws SQLException {
		Connection db = null;
		PreparedStatement p = null;

		try {
			db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
			p = db.prepareStatement("INSERT INTO Parcel(id,order_id) VALUES (?,?)");
			p.setString(1,this.id);
			p.setInt(2,this.order_id);

			p.executeUpdate();
			System.out.println("Added the following:");
			this.printParcel();
		} catch (Exception e) {
			//TODO: handle exception
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}