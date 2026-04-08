import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private App.AuthService authService;

    @BeforeEach
    public void setup() {
        authService = new App.AuthService();
    }

    @Test
    public void testSuccessfulAuthentication() {
        // Valid password -> true
        assertTrue(authService.verifyPasswordAndGenerateOtp("testuser", "securepass123"), 
                   "Password verification should succeed for valid credentials");
        
        // Valid OTP -> true
        assertTrue(authService.verifyOtp("testuser", "123456"), 
                   "OTP verification should succeed for the valid generated OTP");
    }

    @Test
    public void testUnauthorizedAccessWrongPassword() {
        // Invalid password -> false
        assertFalse(authService.verifyPasswordAndGenerateOtp("testuser", "wrongpassword"), 
                    "Password verification should fail for invalid password");
                    
        // Subsequent OTP check should fail because OTP was never generated
        assertFalse(authService.verifyOtp("testuser", "123456"), 
                    "OTP verification should fail since password was wrong");
    }

    @Test
    public void testUnauthorizedAccessWrongOtp() {
        // Valid password -> true
        assertTrue(authService.verifyPasswordAndGenerateOtp("testuser", "securepass123"));
        
        // Invalid OTP -> false
        assertFalse(authService.verifyOtp("testuser", "999999"), 
                    "OTP verification should fail for invalid OTP");
    }

    @Test
    public void testUnknownUser() {
        // Unknown user -> false
        assertFalse(authService.verifyPasswordAndGenerateOtp("unknown", "anypassword"));
    }

    @Test
    public void testVerifyOtpWithoutPasswordFirst() {
        // Attempting to verify OTP for a valid user, but without generating it first via password verification
        assertFalse(authService.verifyOtp("testuser", "123456"), 
                    "OTP verification should fail if OTP was never generated for the user");
    }
}
