package digiteen3play.pvm;


import com.fasterxml.jackson.annotation.JsonProperty;
import digiteen3play.model.Person;
import digiteen3play.model.PersonInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class PersonPVM {

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private String role;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, required = true)
    private String password;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime joinTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String traceId;


    private PersonInfoPVM personInfoPVM;


    public static Person pvmToModel(PersonPVM personPVM){
        Person person = new Person();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setEmail(personPVM.getPersonInfoPVM().getEmail());
        personInfo.setLastName(personPVM.getPersonInfoPVM().getLastName());
        personInfo.setFirstName(personPVM.getPersonInfoPVM().getFirstName());
        personInfo.setPhoneNumber(personPVM.getPersonInfoPVM().getPhoneNumber());

        person.setRole("USER");
        person.setPersonInfo(personInfo);
        person.setPassword(personPVM.getPassword());
        return person;
    }
}
