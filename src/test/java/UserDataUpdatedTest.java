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

public class UserDataUpdatedTest {
    private User customer;
    private User updatedUser;
    private String accessToken;
    private String bearerToken;

    @Before
    public void setUp() {
        customer = User.getRandomData();
        updatedUser = User.getRandomData();

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

    @Step("Update user")
    public ValidatableResponse updateUser(User user, String bearerToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(bearerToken)
                .body(user)
                .when()
                .patch(API_AUTH + "user")
                .then();
    }


    @Step("Delete user")
    public static ValidatableResponse deleteUser(String bearerToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(bearerToken)
                .when()
                .delete(API_AUTH + "user")
                .then();
    }

    @Test
    @DisplayName("User authorization with user updated data")
    @Description("User data updated and request return 401")
    public void testUserDataUpdated() {
        ValidatableResponse response = createUser(customer);
        accessToken = response.extract().path("accessToken");

        if (accessToken != null && accessToken.length() > 7) {
            bearerToken = accessToken.substring(7);
        } else {
            throw new IllegalStateException("Access token is invalid");
        }
        ValidatableResponse response2 = updateUser(updatedUser, bearerToken);
        boolean isUserDataChanged = response2.extract().path("success");
        int statusCode = response2.extract().statusCode();

        assertThat("Status code isn't correct", statusCode, equalTo(200));
        assertThat("User access token  isn't correct", accessToken, is(not("")));
        assertThat("User data isn't updated", isUserDataChanged, is(true));
    }

    @Test
    @DisplayName("User data updated without authorization")
    @Description("User data updated without authorization and request return 401")
    public void testUserDataUpdatedWithoutAuthValidation() {
        createUser(customer);
        bearerToken = "";

        ValidatableResponse response = updateUser(updatedUser, bearerToken);
        boolean isUserDataUpdated = response.extract().path("success");
        int statusCode = response.extract().statusCode();

        assertThat("Status code is incorrect", statusCode, equalTo(401));
        assertThat("User data is not changed", isUserDataUpdated, is(false));

    }

    @After
    public void tearDown() {
        deleteUser(bearerToken);
    }
}


