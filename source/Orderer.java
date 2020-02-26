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
			return (-1);
		} else if (amount == 1) {
			return(getOrdererID(););
		} else {
			System.out.println("Several orderers were found with this name.");
			System.out.println("Please insert the orderer's id: ");
			this.id = input.nextInt();
			found = checkOrdererID();
			if (found > 0)
			{
				return (found);
			} else {
				System.out.println("Please enter a unique orderer name or orderer name with representative id.");
				return (-1);
			}
		}
	}

	public int getOrdererID() throws SQLException {
		int db_id;

		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
        Scanner input = new Scanner(System.in);

        PreparedStatement p = db.prepareStatement("SELECT id FROM Orderer WHERE first_name=? AND last_name=?");
		p.setString(1,this.first_name);
		p.setString(2,this.last_name);

        ResultSet r = p.executeQuery();
		db_id = r.getInt("id");
		return (db_id);
	}

	public int checkOrdererID() throws SQLException {
		String db_fname;
		String db_lname;

		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
        Scanner input = new Scanner(System.in);

        PreparedStatement p = db.prepareStatement("SELECT * FROM Orderer WHERE id=?");
		p.setString(1,this.id);

        ResultSet r = p.executeQuery();
        if (r.next()) {
			System.out.println("ID "+r.getInt("id")+" was found!");
			db_fname = r.getString("first_name");
			db_lname = r.getString("last_name");
			if (this.first_name == db_fname && this.last_name == db_lname) {
				System.out.println("The orderer "+this.first_name+" "+this.last_name)+" exists in the database.";
				return(this.id);
			}
			else {
				System.out.println("However, this id does not belong to the orderer "+this.first_name+" "+this.last_name);
				return(-1);
			}
        } else {
			System.out.println("This ID represents no orderer in the database.");
			return(-1);
        }
	}

	public int getMatchingOrdererAmount() throws SQLException {
		int row_nb;

		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
        Scanner input = new Scanner(System.in);

        PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS rowcount FROM Orderer WHERE first_name=? AND last_name=?");
		p.setString(1,this.first_name);
		p.setString(2,this.last_name);

        ResultSet r = p.executeQuery();
		row_nb = r.getInt("rowcount");
		return (row_nb);
	}

	public void insertOrderer() throws SQLException {
		Connection db = DriverManager.getConnection("jdbc:sqlite:parcels.db");
		PreparedStatement p = db.prepareStatement("INSERT INTO Place(id,first_name,last_name) VALUES (?,?,?)");
        p.setInt(1,this.id);
		p.setString(2,this.first_name);
		p.setString(3,this.last_name);

        p.executeUpdate();
		System.out.println("Added the following:");
		this.printOrderer();
		count++;
	}

}