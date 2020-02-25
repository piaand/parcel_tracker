import java.util.*; 
import java.sql.*; // Impporta Java sql package

public class Orderer {
	
	public int id;
	public String first_name;
	public String last_name;
	private static int count = 1;
	
	public Orderer() {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter here the first name of the person that orders: ");
		this.first_name = input.nextLine();
		System.out.println("Then enter the last name: ");
		this.last_name = input.nextLine();
		this.id = count++;
	}

	public void printOrderer() {
		System.out.println("Orderers full name is: "+ this.first_name + " " + this.last_name );
		System.out.println("Their ID: "+ id );
	}

	public void insertOrderer() throws SQLException {
		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
		PreparedStatement p = db.prepareStatement("INSERT INTO Orderer(id,first_name,last_name) VALUES (?,?,?)");
        p.setInt(1,this.id);
		p.setString(2,this.first_name);
		p.setString(3,this.last_name);

        p.executeUpdate();
		System.out.println("Added the following:");
		this.printOrderer();
	}

}