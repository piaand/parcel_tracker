package parcel_tracking;
import java.util.*; 
import java.sql.*;

public class Parcel {
	
	public String id;
	public int orderer_id;
	private static int count = 1;
	
	public Parcel (String tracking_id, int orderer_id) {
		this.id = tracking_id;
		this.orderer_id = orderer_id;
	}

	public static String askParcelID() {
		Askinput parcel = new Askinput("Enter here the parcel tracking code: ");
		parcel.askQuestionText();
		return (parcel.text);
	}

	public static void updateParcelCount(int current_count) throws SQLException {
		count = current_count + 1;
	}

	public void printParcel() {
		System.out.println("Parcel id: "+ this.id );
		System.out.println("From the orderer whos id is "+ this.orderer_id);
	}

	public String getTrackID() {
		return this.id;
	}

	public int getOrderer() {
		return this.orderer_id;
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
			p.setInt(2,this.orderer_id);

			p.executeUpdate();
			System.out.println("Added the following:");
			this.printParcel();
		} catch (Exception e) {
			System.out.println("Error: inserting parcel data faced an exception. Please try again.");
		} finally {
			try { p.close(); } catch (Exception e) { /* ignored */ }
    		try { db.close(); } catch (Exception e) { /* ignored */ }
		}
	}

}