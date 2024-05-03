import user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static constants.Constants.API_AUTH;
import static constants.Constants.HOME_URL;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class UserLoginTest {
    private User userOne;
    private String bearerToken;
    private String accessToken;

    @Before
    public void setUp() {
        userOne = User.getRandomData();
    }

    @Step("Create user")
    public ValidatableResponse createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .body(user)
                .when()
                .post(API_AUTH + "register")
                .then();
    }

    @Step("User login")
    public ValidatableResponse login(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .body(user)
                .when()
                .post(API_AUTH + "login")
                .then();
    }

    @Step("Delete user")
    public static void deleteUser(String bearerToken) {
        given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(bearerToken)
                .when()
                .delete(API_AUTH + "user")
                .then();
    }

    @Test
    @DisplayName("User authorisation")
    @Description("User authorisation is successful and return 200 ")
    public void testUserAuthorisation() {
        createUser(userOne);
        ValidatableResponse response = login(userOne);
        accessToken = response.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        int statusCodeSuccessfulLogin = response.extract().statusCode();

        assertThat("User access token isn't correct", bearerToken, is(not("")));
        assertThat(statusCodeSuccessfulLogin, equalTo(200));
    }

    @Test
    @DisplayName("Login with wrong credentials")
    @Description("User login with wrong credentials should fail and return 401")
    public void testLoginWithEmptyPasswordValidation() {
        User wrongUser = User.getCustomerWithEmptyPassword();
        createUser(wrongUser);
        ValidatableResponse wrongResponse = login(wrongUser);
        int ActualStatusCode = wrongResponse.extract().statusCode();
        String actualMessage = wrongResponse.extract().path("message");

        assertThat(ActualStatusCode, equalTo(401));
        assertEquals("Status code isn't correct", 401, ActualStatusCode);
        assertEquals("User access token isn't correct", "email or password are incorrect", actualMessage);
    }

    @After
    public void tearDown() {
        if (bearerToken != null) {
            deleteUser(bearerToken);
        }
    }
}