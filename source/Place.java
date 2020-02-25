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
		this.id = count++;
	}

	public void printPlace() {
		System.out.println("Name of the place:"+ name );
		System.out.println("ID:"+ id );
	}

	public void insertPlace() throws SQLException {
		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
		PreparedStatement p = db.prepareStatement("INSERT INTO Place(id,name) VALUES (?,?)");
        p.setInt(1,this.id);
        p.setString(2,this.name);

        p.executeUpdate();
		System.out.println("Added the following:");
		this.printPlace();
	}

}