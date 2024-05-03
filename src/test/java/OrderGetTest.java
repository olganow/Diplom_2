import io.qameta.allure.junit4.DisplayName;
import user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static constants.Constants.*;
import static constants.Constants.ORDER_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class OrderGetTest {
    private User customer;
    private String accessToken;
    private String bearerToken;

    @Before
    public void setUp() {
        customer = User.getRandomData();

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
    public static ValidatableResponse deleteUser(String bearerToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(bearerToken)
                .when()
                .delete(API_AUTH + "user")
                .then();
    }

    @Step("Order list")
    public ValidatableResponse orderList() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .when()
                .get(ORDER_PATH + "/all")
                .then();
    }

    @Step("Order list")
    public ValidatableResponse userOrderInfo(String token) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(token.replace("Bearer ", ""))
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Test
    @DisplayName("Get order list of auth user")
    @Description("Get order list of auth user and return 200")
    public void orderInfoGetAuth() {
        createUser(customer);

        ValidatableResponse login = login(customer);
        accessToken = login.extract().path("accessToken");
        bearerToken = accessToken.substring(7);

        ValidatableResponse orderList = userOrderInfo(bearerToken);
        int statusCode = orderList.extract().statusCode();
        boolean orderCreated = orderList.extract().path("success");
        List<Map<String, Object>> ordersList = orderList.extract().path("orders");

        assertThat("Status code isn't valid", statusCode, equalTo(200));
        assertThat("There are not any information about this order", orderCreated, is(true));
        assertThat("Orders list empty", ordersList, is(not(0)));
    }

    @Test
    @DisplayName("Get order list")
    @Description("Order List and return 200")
    public void getOrderList() {
        bearerToken = "";

        ValidatableResponse orderList = orderList();
        int statusCode = orderList.extract().statusCode();
        boolean orderInfoGet = orderList.extract().path("success");
        List<Map<String, Object>> ordersList = orderList.extract().path("orders");

        assertThat("Status code isn't correct", statusCode, equalTo(200));
        assertThat("There are not any information about this order", orderInfoGet, is(true));
        assertThat("Orders list is  empty", ordersList, is(not(0)));
    }

    @Test
    @DisplayName("Get order list of not auth user")
    @Description("Get order list of not auth user and return 401")
    public void testGetOrderListWithoutAuth() {
        bearerToken = "";

        ValidatableResponse orderList = userOrderInfo(bearerToken);
        int statusCode = orderList.extract().statusCode();
        boolean orderInfoNotGet = orderList.extract().path("success");
        String errorMessage = orderList.extract().path("message");

        assertThat("Status code isn't correct", statusCode, equalTo(401));
        assertThat("IThere are not any information about this order", orderInfoNotGet, is(false));
        assertEquals("The error message isn't correct", "You should be authorised", errorMessage);
    }

    @After
    public void tearDown() {
        deleteUser(bearerToken);
    }
}