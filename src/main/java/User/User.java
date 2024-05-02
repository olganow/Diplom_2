package User;

import org.apache.commons.lang3.RandomStringUtils;

public class User {
    public String name;
    public String email;
    public String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandomData() {
        final String email = RandomStringUtils.randomAlphabetic(7) + "@ya.ru";
        final String password = RandomStringUtils.randomAlphabetic(7);
        final String name = RandomStringUtils.randomAlphabetic(7);
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
        return new User()
                .setName("")
                .setEmail(RandomStringUtils.randomAlphabetic(7) + "@ya.ru")
                .setPassword(RandomStringUtils.randomAlphabetic(7));
    }

    public static User getCustomerWithEmptyPassword() {
        return new User()
                .setPassword("")
                .setEmail(RandomStringUtils.randomAlphabetic(7) + "@ya.ru")
                .setName(RandomStringUtils.randomAlphabetic(7));
    }

    public static User getCustomerWithEmptyEmail() {
        return new User()
                .setEmail("")
                .setPassword(RandomStringUtils.randomAlphabetic(7))
                .setName(RandomStringUtils.randomAlphabetic(7));
    }

    public static User getCustomerWithoutName() {
        return new User()
                .setEmail(RandomStringUtils.randomAlphabetic(7) + "@ya.ru")
                .setPassword(RandomStringUtils.randomAlphabetic(7));
    }

    public static User getCustomerWithoutPassword() {
        return new User()
                .setEmail(RandomStringUtils.randomAlphabetic(7) + "@ya.ru")
                .setName(RandomStringUtils.randomAlphabetic(7));
    }

    public static User getCustomerWithoutEmail() {
        return new User()
                .setPassword(RandomStringUtils.randomAlphabetic(7))
                .setName(RandomStringUtils.randomAlphabetic(7));
    }


}
