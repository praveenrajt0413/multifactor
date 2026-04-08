import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    public static class User {
        String username;
        String password;
        String currentOtp;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class AuthService {
        Map<String, User> userDatabase = new HashMap<>();

        public AuthService() {
            userDatabase.put("testuser", new User("testuser", "securepass123"));
        }

        public boolean verifyPasswordAndGenerateOtp(String username, String password) {
            User user = userDatabase.get(username);
            if (user != null && user.password.equals(password)) {
                user.currentOtp = "123456"; 
                return true;
            }
            return false;
        }

        public boolean verifyOtp(String username, String otp) {
            User user = userDatabase.get(username);
            if (user != null && user.currentOtp != null && user.currentOtp.equals(otp)) {
                user.currentOtp = null; 
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Auth Service Started");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authService.verifyPasswordAndGenerateOtp(username, password)) {
            System.out.println("Password Verified. OTP Sent.");
            System.out.print("Enter OTP: ");
            String otp = scanner.nextLine();
            
            if (authService.verifyOtp(username, otp)) {
                System.out.println("Authentication fully successful. Access Granted.");
            } else {
                System.out.println("Invalid OTP.");
            }
        } else {
            System.out.println("Invalid Username or Password.");
        }
        
        scanner.close();
    }
}
