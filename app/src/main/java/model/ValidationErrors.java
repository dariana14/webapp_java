package model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@NoArgsConstructor
public class ValidationErrors {
    private List<ValidationError> errors ;

    public List<ValidationError> getErrors() {
        return errors;
    }


    public void addErrorMessage(String message) {
        errors.add(new ValidationError(message));
    }

    public void addFieldError(FieldError fieldError) {
        if (errors == null) {
            errors = new ArrayList<>();
        }

        List<String> args = Stream.of(fieldError.getArguments())
                .filter(arg -> !(arg instanceof DefaultMessageSourceResolvable))
                .map(String::valueOf)
                .collect(Collectors.toList());

        errors.add(new ValidationError(fieldError.getCodes()[0], args));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
