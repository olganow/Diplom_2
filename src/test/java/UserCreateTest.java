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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UserCreateTest {
    private User user;
    private String accessToken;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.getRandomData();
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
    @DisplayName("Create user")
    @Description("Create user and get 200")
    public void testUserCreated() {
        ValidatableResponse response = createUser(user);
        int statusCode = response.extract().statusCode();
        boolean isUserCreated = response.extract().path("success");
        accessToken = login(user).extract().path("accessToken");
        bearerToken = accessToken.substring(7);

        assertThat("Status code isn't correct ", statusCode, equalTo(200));
        assertThat("User hasn't created", isUserCreated, is(true));
        assertThat("User access token isn't correct", accessToken, is(not("")));
    }

    @Test
    @DisplayName("Forbidden to create two equal users")
    @Description("Create two equal users and get 403")
    public void testCreateEqualUser() {
        createUser(user);
        ValidatableResponse response = createUser(user);
        accessToken = login(user).extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        int statusCode = response.extract().statusCode();
        String errorMessage = response.extract().path("message");

        assertThat("Status code isn't correct", statusCode, equalTo(403));
        assertThat("User already exists", errorMessage, equalTo("User already exists"));
    }

    @After
    public void tearDown() {
        deleteUser(bearerToken);
    }
}
