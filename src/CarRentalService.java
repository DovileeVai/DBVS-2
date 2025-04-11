import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CarRentalService {

    // Duomenu paieska
    public static void searchByPrice (Connection con, double price1, double price2) {
        String searchSQL = "SELECT * FROM dova7961.Automobilis WHERE Kaina_uz_diena BETWEEN  ? AND ?";
        try (PreparedStatement pstmt = con.prepareStatement(searchSQL)) {
            pstmt.setBigDecimal(1, BigDecimal.valueOf(price1));
            pstmt.setBigDecimal(2, BigDecimal.valueOf(price2));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Rastas automobilis: " + rs.getString("Marke") + " " +rs.getString("Modelis") +
                            ", Metai: " + rs.getInt("Metai") + ", Spalva: " +rs.getString("Spalva") +
                            ", Kaina uz diena: " + rs.getBigDecimal("Kaina_uz_diena"));
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Klaida vykdant automobilio paieska!");
            e.printStackTrace();
        }
    }

    // Duomenu ivedimas naudojant dvi tarpusavyje susijusias lenteles
    // Transakcija
    public static void registerNewRentalWithInsurance (Connection con, int carId, int clientId,
                                                       String startDate, String endDate, int insuranceCompanyCode,
                                                       String insuranceType, double insurancePrice) {
        // Patikriname ivestis
        if (!Validation.validateIntLength(carId, 4) || !Validation.validateIntLength(clientId, 3) || !Validation.validateIntLength(insuranceCompanyCode, 9) ||
                !Validation.validateStringLength(startDate, 10) || !Validation.validateStringLength(endDate, 10)) {
            System.out.println("Vienas arba keli duomenys perzenge leistina ilgi!");
            return;
        }

        String insertRentalSQL = "INSERT INTO dova7961.Nuomos_sutartis (Automobilis, Klientas, Pradzia, Pabaiga) VALUES (?, ?, ?, ?) RETURNING Sutarties_nr";
        String insertInsuranceSQL = "INSERT INTO dova7961.Draudimas (Sutartis, Draudejas, Tipas, Kaina) VALUES (?, ?, ?, ?) RETURNING Draudimo_nr";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement pstmt1 = con.prepareStatement(insertRentalSQL);
                 PreparedStatement pstmt2 = con.prepareStatement(insertInsuranceSQL)) {

                pstmt1.setInt(1, carId);
                pstmt1.setInt(2, clientId);
                pstmt1.setDate(3, java.sql.Date.valueOf(startDate));
                pstmt1.setDate(4, java.sql.Date.valueOf(endDate));

                ResultSet rs1 = pstmt1.executeQuery();
                if (rs1.next()) {
                    int contractId = rs1.getInt("Sutarties_nr");

                    pstmt2.setInt(1, contractId);
                    pstmt2.setInt(2, insuranceCompanyCode);
                    pstmt2.setString(3, insuranceType);
                    pstmt2.setBigDecimal(4, BigDecimal.valueOf(insurancePrice));

                    ResultSet rs2 = pstmt2.executeQuery();
                    if (rs2.next()) {
                        int insuranceId = rs2.getInt("Draudimo_nr");
                    }
                    con.commit();
                    System.out.println("Nuomos sutartis ir draudimas uzregistruoti sekmingai!");
                }
                else {
                    con.rollback();
                    System.out.println("Nepavyko uzregistuori nuomos sutarties ir draudimo!");
                }
            }
            catch (SQLException e) {
                con.rollback();
                System.out.println("Transakcijos klaida!");
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                con.setAutoCommit(true);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printCarsAtributes (Connection con) {
        String selectSQL = "SELECT * FROM dova7961.Automobilis";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL);) {
            System.out.println("Automobilio_id | Merke | Modelis | Metai | Spalva | Kaina_uz_diena");
            System.out.println("----------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("Automobilio_id");
                String marke = rs.getString("Marke");
                String modelis = rs.getString("Modelis");
                int metai = rs.getInt("Metai");
                String spalva = rs.getString("Spalva");
                double kaina = rs.getDouble("Kaina_uz_diena");
                System.out.println(id + " | " + marke + " | " + modelis + " | " + metai + " | " + spalva + " | " + kaina);
            }
        }
        catch (SQLException e) {
            System.out.println("Klaida atspausdinant automobiliu atributus!");
            e.printStackTrace();
        }
    }

    public static void printInsuranceCompaniesAtributes (Connection con) {
        String selectSQL = "SELECT Imones_kodas, Imone FROM dova7961.Draudejas";
        int insuranceCompanyCode;
        String company;
        System.out.println("Imones_kodas | Imones_pavadinimas ");
        System.out.println("---------------------------------");
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    insuranceCompanyCode = rs.getInt("Imones_kodas");
                    company = rs.getString("Imone");
                    System.out.println(insuranceCompanyCode + " " + company);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Klaida atspausdinant draudeju atributus!");
            e.printStackTrace();
        }
    }
}






