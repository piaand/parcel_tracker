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

	public void askQuestionInt() {
		System.out.print(this.question);
		this.nb = this.input.nextInt();
	}

}