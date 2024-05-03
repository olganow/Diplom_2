import goods.Burger;
import io.qameta.allure.junit4.DisplayName;
import user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static constants.Constants.*;
import static constants.Constants.ORDER_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class OrderCreateTest {
    private User user;
    private Burger ingredients;
    private String accessToken;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.getRandomData();
        ingredients = getRandomBurger();
    }

    public static Burger getRandomBurger() {
        ValidatableResponse response = given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .statusCode(200);

        ArrayList<Object> ingredients = new ArrayList<>();

        ingredients.add(Burger.getRandomIngredient(response, "bun"));
        ingredients.add(Burger.getRandomIngredient(response, "main"));
        ingredients.add(Burger.getRandomIngredient(response, "sauce"));

        return new Burger(ingredients);
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


    @Step("Create order")
    public ValidatableResponse createOrder(String token, Burger ingredients) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOME_URL)
                .auth().oauth2(token.replace("Bearer ", ""))
                .body(ingredients)
                .when()
                .post(ORDER_PATH)
                .then();
    }


    @Test
    @DisplayName("Create order for auth user")
    @Description("Create order and return 200")
    public void testCreatedOrderWithAuthUser() {
        ValidatableResponse customerResponse = createUser(user);

        accessToken = customerResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);

        ValidatableResponse orderResponse = createOrder(bearerToken, ingredients);
        boolean orderCreated = orderResponse.extract().path("success");
        int statusCode = orderResponse.extract().statusCode();
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("Status code isn't correct", statusCode, equalTo(200));
        assertThat("The order hasn't been created", orderCreated, is(true));
        assertThat("The order number is empty", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Create order for non auth user")
    @Description("Create order for non auth user and return 200")
    public void testCreatedOrderWitNonAuthUser() {
        bearerToken = "";
        ValidatableResponse orderResponse = createOrder(bearerToken, ingredients);
        boolean orderCreated = orderResponse.extract().path("success");
        int statusCode = orderResponse.extract().statusCode();
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("Status code is not correct", statusCode, equalTo(200));
        assertThat("The order has not been created", orderCreated, is(true));
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Create order for non auth user")
    @Description("Create order without ingredients  and return 400")
    public void testCreatedOrderWithOutIngredientsValidation() {
        ValidatableResponse customerResponse = createUser(user);
        accessToken = customerResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);

        ValidatableResponse orderResponse = createOrder(bearerToken, Burger.getNullIngredients());
        boolean orderNotCreated = orderResponse.extract().path("success");
        int statusCode = orderResponse.extract().statusCode();
        String errorMessage = orderResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(400));
        assertThat("The order has not been created", orderNotCreated, is(false));
        assertEquals("The error message i correct", "Ingredient ids must be provided", errorMessage);
    }

    @Test
    @DisplayName("Create order wit non existed ingredients")
    @Description("Create order wit non existed ingredients  and return 500")
    public void testCreateOrderWithIncorrectIngredientsValidation() {
        ValidatableResponse customerResponse = createUser(user);

        accessToken = customerResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);

        ValidatableResponse orderResponse = createOrder(bearerToken, Burger.getIncorrectIngredients());
        int statusCode = orderResponse.extract().statusCode();
        assertThat("Status code is not correct", statusCode, equalTo(500));
    }

    @After
    public void tearDown() {
        if (bearerToken != null) {
            deleteUser(bearerToken);
        }
    }
}