import java.util.*; 

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
}