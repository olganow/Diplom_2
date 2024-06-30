import user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static constants.Constants.API_AUTH;
import static constants.Constants.HOME_URL;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserCreateValidationTest {
    private final User user;
    private final int expectedCode;
    private final String messageError;

    public UserCreateValidationTest(User user, int expectedCode, String messageError) {
        this.user = user;
        this.expectedCode = expectedCode;
        this.messageError = messageError;
    }

    @Step("Create User")
    public ValidatableResponse createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .body(user)
                .when()
                .post(API_AUTH + "register")
                .then();
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {User.getCustomerWithEmptyName(), 403, "Email, password and name are required fields"},
                {User.getCustomerWithEmptyPassword(), 403, "Email, password and name are required fields"},
                {User.getCustomerWithEmptyEmail(), 403, "Email, password and name are required fields"},
                {User.getCustomerWithoutName(), 403, "Email, password and name are required fields"},
                {User.getCustomerWithoutPassword(), 403, "Email, password and name are required fields"},
                {User.getCustomerWithoutEmail(), 403, "Email, password and name are required fields"}
        };
    }

    @Test
    @DisplayName("Create user with empty fields")
    @Description("Create user with empty fields and get 403")
    public void testCreateUserWithEmptyFields() {
        ValidatableResponse response = createUser(user);
        String actualMessage = response.extract().path("message");
        int actualCode = response.extract().statusCode();

        assertEquals(messageError, actualMessage);
        assertEquals(expectedCode, actualCode);
    }
}