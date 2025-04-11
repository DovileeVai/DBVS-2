import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientService {

    // Duomenu ivedimas
    public static void registerNewClient (Connection con, String asmensKodas, String vardas, String pavarde, String telNr) {
        // Patikriname asmens kodo formata
        if (!Validation.validatePersonalCode(asmensKodas)) {
            System.out.println("Netinkamas asmens kodo formatas! Iveskite 11 skaitmenu koda.");
            return;
        }

        // Patikriname telefono numerio formata
        if (!Validation.validatePhoneNumber(telNr)) {
            System.out.println("Netinkamas telefono numerio formatas! Iveskite telefono numeri formato +370XXXXXXXX.");
            return;
        }

        // Patikriname varda ir pavarde
        if (!Validation.validateName(vardas) || !Validation.validateName(pavarde)) {
            System.out.println("Netinkamas vardo arba pavardes formatas! Iveskite tik raides.");
            return;
        }

        String insertSQL = "INSERT INTO dova7961.Klientas (Asmens_kodas, Vardas, Pavarde, Tel_nr) VALUES (?, ?, ?, ?) RETURNING Kliento_nr";
        try (PreparedStatement pstmt = con.prepareStatement(insertSQL)) {
            pstmt.setString(1, asmensKodas);
            pstmt.setString(2, vardas);
            pstmt.setString(3, pavarde);
            pstmt.setString(4, telNr);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                int generatedKey = rs.getInt(1);
                System.out.println("Klientas uzregistruotas sekmingai! (Kliento_nr: " + generatedKey + ")");
            }

        }
        catch (SQLException e) {
            System.out.println("Klaida uzregistruojant klienta!");
            e.printStackTrace();
        }
    }

    // Duomenu trynimas (salinimas)
    public static void deleteClient (Connection con, int clientId) {
        // Patikriname ivesties ilgi
        if (!Validation.validateIntLength(clientId, 3)) {
            System.out.println("Ivestis virsija leistina simboliu skaiciu! Kliento numeri sudaro 3 skaitmenys.");
            return;
        }

        String deleteSQL = "DELETE FROM dova7961.Klientas WHERE Kliento_nr = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, clientId);
            pstmt.executeUpdate();
            System.out.println("Klientas pasalintas sekmingai!");
        }
        catch (SQLException e) {
            System.out.println("Klaida salinant klienta!");
            e.printStackTrace();
        }
    }

    // Duomenu atnaujinimas (keitimas)
    public static void updateContractEndDate (Connection con, int contractId, String newEndDate) {
        // Patikriname ivesties ilgi
        if (!Validation.validateIntLength(contractId, 2)) {
            System.out.println("Netinkamas sutarties numeris! Sutarties numeri sudaro 2 skaitmenys.");
            return;
        }
        if (!Validation.validateStringLength(newEndDate, 10))  {
            System.out.println("Datos ivestis virsija leistina leistina ilgi (ivedamu simboliu skaiciu)!");
        }

        String updateSQL = "UPDATE dova7961.Nuomos_sutartis SET Pabaiga = ? WHERE Sutarties_nr = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateSQL)) {
            pstmt.setDate(1, java.sql.Date.valueOf(newEndDate));
            pstmt.setInt(2, contractId);
            pstmt.executeUpdate();
            System.out.println("Sutarties pabaigos data atnaujinta sekmingai!");
        }
        catch (SQLException e) {
            System.out.println("Klaida atnaujinant sutarties pabaigos data!");
            e.printStackTrace();
        }
    }

    public static void printClientsAtributes (Connection con) {
        String selectSQL = "SELECT Kliento_nr, Asmens_kodas, Tel_nr FROM dova7961.Klientas";
        int clientId;
        String personalCode;
        String phoneNumber;
        System.out.println("Kliento_nr | Asmens_kodas | Telefono_numeris ");
        System.out.println("-------------------------------------------");
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    clientId = rs.getInt("Kliento_nr");
                    personalCode = rs.getString("Asmens_kodas");
                    phoneNumber = rs.getString("Tel_nr");
                    System.out.println(clientId + " " + personalCode + " " + phoneNumber);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Klaida atspausdinant uzregistruotu klientu atributus!");
            e.printStackTrace();
        }
    }

    public static void printContractsAtributes (Connection con) {
        String selectSQL = "SELECT Sutarties_nr, Pradzia, Pabaiga FROM dova7961.Nuomos_sutartis";
        int contractId;
        String startDate;
        String endDate;
        System.out.println("Sutarties_nr | Pradzia | Pabaiga ");
        System.out.println("---------------------------------");
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    contractId = rs.getInt("Sutarties_nr");
                    startDate = rs.getString("Pradzia");
                    endDate = rs.getString("Pabaiga");
                    System.out.println(contractId + " " + startDate + " " + endDate);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Klaida atspausdinant nuomu sutarciu atributus!");
            e.printStackTrace();
        }
    }

    public static void printInsuranceTypes () {
        System.out.println("Galimi draudimu tipai: Standartinis, Papildomas, Kasko.");
    }

    public static void printPriceRange () {
        System.out.println("Draudimo kaina iki 500!");
    }
}
 
 
 
 
 
 
 
 