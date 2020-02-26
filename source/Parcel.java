import java.util.*; 
import java.sql.*; // Impporta Java sql package

public class Parcel {
	
	public String id;
	public int order_id;
	public int current_place_id;
	private static int count = 1;
	
	public Parcel (int orderer_id) {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here the parcel tracking code: ");
		this.id = input.nextLine();
		this.order_id = orderer_id;
		this.current_place_id = 0;
	}

	public void printParcel() {
		System.out.println("Parcel id: "+ this.id );
		System.out.println("From the orderer whos id is "+ this.order_id);
		System.out.println("This parcel is currently at location "+ this.current_place_id);
	}

	public void insertParcel() throws SQLException {
		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
		PreparedStatement p = db.prepareStatement("INSERT INTO Parcel(id,order_id,current_place_id) VALUES (?,?,?)");
        p.setString(1,this.id);
		p.setInt(2,this.order_id);
		p.setInt(2,this.current_place_id);

        p.executeUpdate();
		System.out.println("Added the following:");
		this.printParcel();
	}

}