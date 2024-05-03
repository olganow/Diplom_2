package goods;


import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class Burger {

    public ArrayList<Object> ingredients;

    public Burger(ArrayList<Object> ingredients) {
        this.ingredients = ingredients;
    }

    public static Object getRandomIngredient(ValidatableResponse response, String type) {
        List<Object> ingredients = response.extract().jsonPath().getList("data.findAll{it.type == '" + type + "'}._id");
        int index = nextInt(0, ingredients.size() - 1);
        return ingredients.get(index);
    }

    public static Burger getNullIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();

        return new Burger(ingredients);
    }

    public static Burger getIncorrectIngredients() {
        ArrayList<Object> ingredients = new ArrayList<>();
        ingredients.add("incorrectIngredientOne");
        ingredients.add("incorrectIngredientTwo");
        ingredients.add("incorrectIngredientThree");

        return new Burger(ingredients);
    }
}