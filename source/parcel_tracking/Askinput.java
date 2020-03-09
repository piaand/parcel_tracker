package parcel_tracking;
import java.util.*;

public class Askinput {
	String question;
	Scanner input;
	String text;
	int nb;

	public Askinput(String question) {
		this.input = new Scanner(System.in);
		this.question = question;
		this.text = null;
		this.nb = 0;
	}

	public void askQuestionText() {
		System.out.print(this.question);
		this.text = this.input.nextLine();
	}

	public boolean askQuestionInt() {
		try {
			System.out.print(this.question);
			this.nb = this.input.nextInt();
			return (true);	
		} catch (InputMismatchException e) {
			System.out.println("Please insert numbers.");
			this.nb = 0;
			return (false);
		}
	}

}
