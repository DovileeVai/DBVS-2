import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConnectionManager.loadDriver();
        Connection con = DatabaseConnectionManager.getConnection();
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
                        System.out.println("Iveskite ieskomu automobiliu kainu rezius: \nNuo: ");
                        double price1 = scanner.nextDouble();
                        System.out.println("\nIki: ");
                        double price2 = scanner.nextDouble();
                        CarRentalService.searchByPrice(con, price1, price2);
                        break;
                    case 2:
                        ClientService.printClientsAtributes(con);
                        System.out.println("Iveskite naujo kliento asmens koda: ");
                        String personalCode = scanner.nextLine();
                        System.out.println("Iveskite naujo kliento varda: ");
                        String name = scanner.nextLine();
                        System.out.println("Iveskite naujo kliento pavarde: ");
                        String surname = scanner.nextLine();
                        System.out.println("Iveskit enaujo kliento telefono numeri: ");
                        String phoneNumber = scanner.nextLine();
                        ClientService.registerNewClient(con, personalCode, name, surname, phoneNumber);
                        break;
                    case 3:
                        ClientService.printContractsAtributes(con);
                        System.out.println("Iveskite sutarties, kurios pabaigos data norite atnaujinti, numeri: ");
                        int contractId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Iveskite nauja pabaigos data (YYYY-MM-DD): ");
                        String newEndDate = scanner.nextLine();
                        ClientService.updateContractEndDate(con, contractId, newEndDate);
                        break;
                    case 4:
                        ClientService.printClientsAtributes(con);
                        System.out.println("Iveskite kliento, kuri norite pasalinti, numeri: ");
                        int clientId = scanner.nextInt();
                        ClientService.deleteClient(con, clientId);
                        break;
                    case 5:
                        CarRentalService.printCarsAtributes(con);
                        System.out.println("Iveskite automobilio ID: ");
                        int carId = scanner.nextInt();
                        ClientService.printClientsAtributes(con);
                        System.out.println("Ivekite kliento numeri: ");
                        int newClientId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Iveskite sutarties pradzios data (YYYY-MM-DD): ");
                        String startDate = scanner.nextLine();
                        System.out.println("Iveskite sutarties pabaigos data (YYYY-MM-DD): ");
                        String endDate = scanner.nextLine();
                        System.out.println("Galimos draudimo imones: ");
                        CarRentalService.printInsuranceCompaniesAtributes(con);
                        System.out.println("Iveskite draudimo imones koda: ");
                        int insuranceCompanyCode = scanner.nextInt();
                        scanner.nextLine();
                        ClientService.printInsuranceTypes();
                        System.out.println("Iveskite draudimo tipa: ");
                        String insuranceType = scanner.nextLine();
                        ClientService.printPriceRange();
                        System.out.println("Iveskite draudimo kaina: ");
                        double insurancePrice = scanner.nextDouble();
                        CarRentalService.registerNewRentalWithInsurance(con, carId, newClientId, startDate, endDate, insuranceCompanyCode, insuranceType, insurancePrice);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Netinkamas pasirinkimas. Bandykite dar karta.");
                }
            }
            scanner.close();
        }
        DatabaseConnectionManager.closeConnection(con);
    }
}