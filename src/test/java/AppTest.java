import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AppTest {

    App.AuthService authService;

    @BeforeEach
    public void setup() {
        authService = new App.AuthService();
    }

    @Test
    public void testSuccessfulAuthentication() {
        assertTrue(authService.verifyPasswordAndGenerateOtp("testuser", "securepass123"));
        assertTrue(authService.verifyOtp("testuser", "123456"));
    }

    @Test
    public void testUnauthorizedAccessWrongPassword() {
        assertFalse(authService.verifyPasswordAndGenerateOtp("testuser", "wrongpassword"));
        assertFalse(authService.verifyOtp("testuser", "123456"));
    }

    @Test
    public void testUnauthorizedAccessWrongOtp() {
        assertTrue(authService.verifyPasswordAndGenerateOtp("testuser", "securepass123"));
        assertFalse(authService.verifyOtp("testuser", "999999"));
    }

    @Test
    public void testUnknownUser() {
        assertFalse(authService.verifyPasswordAndGenerateOtp("unknown", "anypassword"));
    }

    @Test
    public void testVerifyOtpWithoutPasswordFirst() {
        assertFalse(authService.verifyOtp("testuser", "123456"));
    }
}
