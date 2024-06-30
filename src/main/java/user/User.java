package user;

import com.github.javafaker.Faker;

public class User {
    public String name;
    public String email;
    public String password;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandomData() {
        Faker faker = new Faker();
        final String email = faker.internet().emailAddress();
        final String password = faker.internet().password();
        final String name = faker.name().fullName();
        return new User(email, password, name);
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public static User getCustomerWithEmptyName() {
        Faker faker = new Faker();
        return new User()
                .setName("")
                .setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password());
    }

    public static User getCustomerWithEmptyPassword() {
        Faker faker = new Faker();
        return new User()
                .setPassword("")
                .setEmail(faker.internet().emailAddress())
                .setName(faker.name().fullName());
    }

    public static User getCustomerWithEmptyEmail() {
        Faker faker = new Faker();
        return new User()
                .setEmail("")
                .setPassword(faker.internet().password())
                .setName(faker.name().fullName());
    }

    public static User getCustomerWithoutName() {
        Faker faker = new Faker();
        return new User()
                .setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password());
    }

    public static User getCustomerWithoutPassword() {
        Faker faker = new Faker();
        return new User()
                .setEmail(faker.internet().emailAddress())
                .setName(faker.name().fullName());
    }

    public static User getCustomerWithoutEmail() {
        Faker faker = new Faker();
        return new User()
                .setPassword(faker.internet().password())
                .setName(faker.name().fullName());
    }
}