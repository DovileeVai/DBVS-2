import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

public class AutoNuoma {

    public static void loadDriver()
    {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Couldn't find driver class!");
            cnfe.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        Connection postGresConn = null;
        try {
            postGresConn = DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "dova7961", "615.p.Aulina");
        }
        catch (SQLException sqle) {
            System.out.println("Couldn't connect to database!");
            sqle.printStackTrace();
            return null;
        }
        System.out.println("Successfully connected to Postgres Database");

        return postGresConn;
    }

    public static void searchByPrice (Connection con, double price) {
        String searchSQL = "SELECT * FROM dova7961.Automobilis WHERE Kaina_uz_diena = ?";
        try (PreparedStatement pstmt = con.prepareStatement(searchSQL)) {
            pstmt.setBigDecimal(1, BigDecimal.valueOf(price));
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

    public static void registerNewClient (Connection con, String asmensKodas, String vardas, String pavarde, String telNr) {
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

    public static void updateContractEndDate (Connection con, int contractId, String newEndDate) {
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

    public static void deleteClient (Connection con, int clientId) {
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

    public static void registerNewRentalWithInsurance (Connection con, int carId, int clientId,
                                                       String startDate, String endDate, int insuranceCompanyCode,
                                                       String insuranceType, double insurancePrice) {
        String insertRentalSQL = "INSERT INTO dova7961.Nuomos_sutartis (Automobilis, Klientas, Pradzia, Pabaiga) VALUES (?, ?, ?, ?) RETURNING Sutarties_nr";
        String insertInsuranceSQL = "INSERT INTO dova7961.Draudimas (Sutartis, Draudejas, Tipas, Kaina) VALUES (?, ?, ?, ?) RETURNING Draudimo_nr";

        try {
            con.setAutoCommit(false);

            //int contractId = -1;
            //int insuranceId = -1;

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

    public static void printClientsAtributes (Connection con) {
        String selectSQL = "SELECT Kliento_nr, Asmens_kodas, Tel_nr FROM dova7961.Klientas";
        int clientId;
        String personalCode;
        String phoneNumber;
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

    public static void printInsuranceCompaniesAtributes (Connection con) {
        String selectSQL = "SELECT Imones_kodas, Imone FROM dova7961.Draudejas";
        int insuranceCompanyCode;
        String company;
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

    public static void main(String[] args)
    {
        loadDriver();
        Connection con = getConnection();
        if (con != null) {
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("\nPasirinkite veiksma:\n");
                System.out.println("1: Ieskoti automobiliu pagal pasirinkta kaina");
                System.out.println("2: Uzregistruoti nauja klienta");
                System.out.println("3: Atnaujinti sutarties pabaigos data");
                System.out.println("4: Pasalinti klienta");
                System.out.println("5: Uzregistruoti nauja sutarti su draudimu");
                System.out.println("0: Atsijungti");
                System.out.println("Iveskite norimo atlikti veiksmo numeri:");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Iveskite automobilio kaina: ");
                        double price = scanner.nextDouble();
                        searchByPrice(con, price);
                        break;
                    case 2:
                        //System.out.println("")
                        printClientsAtributes(con);
                        System.out.println("Iveskite naujo kliento asmens koda: ");
                        String personalCode = scanner.nextLine();
                        System.out.println("Iveskite naujo kliento varda: ");
                        String name = scanner.nextLine();
                        System.out.println("Iveskite naujo kliento pavarde: ");
                        String surname = scanner.nextLine();
                        System.out.println("Iveskit enaujo kliento telefono numeri: ");
                        String phoneNumber = scanner.nextLine();
                        registerNewClient(con, personalCode, name, surname, phoneNumber);
                        break;
                    case 3:
                        printContractsAtributes(con);
                        System.out.println("Iveskite sutarties, kurios pabaigos data norite atnaujinti, numeri: ");
                        int contractId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Iveskite nauja pabaigos data (YYYY-MM-DD): ");
                        String newEndDate = scanner.nextLine();
                        updateContractEndDate(con, contractId, newEndDate);
                        break;
                    case 4:
                        printClientsAtributes(con);
                        System.out.println("Iveskite kliento, kuri norite pasalinti, numeri: ");
                        int clientId = scanner.nextInt();
                        deleteClient(con, clientId);
                        break;
                    case 5:
                        printCarsAtributes(con);
                        System.out.println("Iveskite automobilio ID: ");
                        int carId = scanner.nextInt();
                        printClientsAtributes(con);
                        System.out.println("Ivekite kliento numeri: ");
                        int newClientId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Iveskite sutarties pradzios data (YYYY-MM-DD): ");
                        String startDate = scanner.nextLine();
                        System.out.println("Iveskite sutarties pabaigos data (YYYY-MM-DD): ");
                        String endDate = scanner.nextLine();
                        System.out.println("Galimos draudimo imones: ");
                        printInsuranceCompaniesAtributes(con);
                        System.out.println("Iveskite draudimo imones koda: ");
                        int insuranceCompanyCode = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Iveskite draudimo tipa: ");
                        String insuranceType = scanner.nextLine();
                        System.out.println("Iveskite draudimo kaina: ");
                        double insurancePrice = scanner.nextDouble();
                        registerNewRentalWithInsurance(con, carId, newClientId, startDate, endDate, insuranceCompanyCode, insuranceType, insurancePrice);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Netinkamas pasirinkimas. Bandykite dar karta.");
                }
            }scanner.close();
        }
        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException exp) {
                System.out.println("Can not close connection!");
                exp.printStackTrace();
            }
        }
    }
}
