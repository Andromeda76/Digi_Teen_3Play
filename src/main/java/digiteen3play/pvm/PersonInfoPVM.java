package digiteen3play.pvm;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PersonInfoPVM {

    @JsonProperty(required = true)
    private String phoneNumber;

    @JsonProperty(required = true)
    private String firstName;

    @JsonProperty(required = true)
    private String lastName;

    @Email(
            regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Please provide a valid email address"
    )
    @JsonProperty(required = true)
    private String email;

}
