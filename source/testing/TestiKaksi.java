import java.sql.*;
import java.util.*;

public class TestiKaksi {
    public static void main(String[] args) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:testi.db");

        Scanner input = new Scanner(System.in);
        System.out.println("Anna tuotteen nimi:");
        String nimi = input.nextLine();
        System.out.println("Anna tuotteen hinta:");
        int hinta = Integer.parseInt(input.nextLine());

        PreparedStatement p = db.prepareStatement("INSERT INTO Tuotteet(nimi,hinta) VALUES (?,?)",
                                                  Statement.RETURN_GENERATED_KEYS);
        p.setString(1,nimi);
        p.setInt(2,hinta);

        p.executeUpdate();
        ResultSet g = p.getGeneratedKeys();
        g.next();
        System.out.println("Tuote lisatty id:lla "+g.getInt(1));
    }
}