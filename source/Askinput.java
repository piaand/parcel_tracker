import java.util.*;

public class Askinput {
	String question;
	Scanner input;
	String text;
	int nb;

	public Askinput(String question) {
		this.input = new Scanner(System.in);
		this.question = question;
	}

	public void askQuestionText() {
		System.out.print(this.question);
		this.text = this.input.nextLine();
	}

	public void askQuestionInt() {
		System.out.print(this.question);
		this.nb = this.input.nextInt();
	}

	/*public static String askTable() {
		String table;
		Scanner input = new Scanner(System.in);
	
		System.out.print("Enter the name of the table you want to query: ");
		table = input.nextLine();
		return (table);
	}*/
}
