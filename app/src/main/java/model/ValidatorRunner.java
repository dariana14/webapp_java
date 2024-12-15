package model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;


public class ValidatorRunner {

    public static void main(String[] args) {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        Order post = new Order(1L, "", null);

        var violations = validator.validate(post);

        System.out.println("here");

        for (ConstraintViolation<Order> violation : violations) {
            System.out.println(violation.getMessage());
        }

    }


}
