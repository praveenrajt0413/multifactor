import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App {

    // Domain Model
    public static class User {
        private String username;
        private String password;
        private String currentOtp;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getCurrentOtp() { return currentOtp; }
        public void setCurrentOtp(String currentOtp) { this.currentOtp = currentOtp; }
    }

    // Logic
    public static class AuthService {
        private Map<String, User> userDatabase = new HashMap<>();

        public AuthService() {
            // Seed a test user
            userDatabase.put("testuser", new User("testuser", "securepass123"));
        }

        // Step 1: Verify Password and Generate OTP
        public boolean verifyPasswordAndGenerateOtp(String username, String password) {
            User user = userDatabase.get(username);
            if (user != null && user.getPassword().equals(password)) {
                user.setCurrentOtp("123456"); // Harcoded for simulation/tests
                return true;
            }
            return false;
        }

        // Step 2: Verify OTP
        public boolean verifyOtp(String username, String otp) {
            User user = userDatabase.get(username);
            if (user != null && user.getCurrentOtp() != null) {
                if (user.getCurrentOtp().equals(otp)) {
                    user.setCurrentOtp(null); // clear after use
                    return true;
                }
            }
            return false;
        }
    }

    // Main Method - Microservice Entry Point
    public static void main(String[] args) throws IOException {
        AuthService authService = new AuthService();
        
        // Start a basic HTTP server without heavy external frameworks
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Context for Login (Password)
        server.createContext("/api/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Expects simple body "username:password"
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    String[] parts = body.split(":");
                    
                    String response = "";
                    int statusCode = 401; // Unauthorized
                    
                    if (parts.length == 2 && authService.verifyPasswordAndGenerateOtp(parts[0], parts[1])) {
                        response = "Password Verified. OTP Sent.";
                        statusCode = 200; // Success
                    } else {
                        response = "Invalid Username or Password.";
                    }
                    
                    exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            }
        });

        // Context for OTP verification
        server.createContext("/api/verify-otp", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Expects simple body "username:otp"
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    String[] parts = body.split(":");
                    
                    String response = "";
                    int statusCode = 401; // Unauthorized
                    
                    if (parts.length == 2 && authService.verifyOtp(parts[0], parts[1])) {
                        response = "Authentication fully successful. Access Granted.";
                        statusCode = 200;
                    } else {
                        response = "Invalid OTP or User.";
                    }
                    
                    exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Auth Microservice listening on port 8080...");
    }
}
