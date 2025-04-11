
public class Validation {
    public static boolean validatePersonalCode(String personalCode) {
        // Tikriname, ar asmens kodas yra 11 skaitmenu ilgio
        return personalCode.matches("\\d{11}");
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        // Tikriname, ar telefono numeris atitinka Lietuvos telefono numerio fromata
        return phoneNumber.matches("\\+370\\d{8}");
    }

    public static boolean validateName(String name) {
        // Tikriname, ar vardo/pavardes laukas neitraukia skauciu ar specialiu simboliu
        return name.matches("[a-zA-Z]+");
    }

    public static boolean validateStringLength(String input, int maxLength) {
        // Tikriname, ar ivestis nevirsija maksimalaus leistino ilgio
        return input.length() <= maxLength;
    }

    public static boolean validateIntLength (int number, int maxLength) {
        // Tikriname, ar ivestas skaicius nevirsija masimalaus leistino simboliu skaiciaus
        String numStr = String.valueOf(number);
        return numStr.length() <= maxLength;
    }
}