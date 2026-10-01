package digiteen3play.controller;


import digiteen3play.model.Person;
import digiteen3play.pvm.PersonPVM;
import digiteen3play.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;


    @PostMapping("/registration")
    public Person saveUser(@Valid @RequestBody PersonPVM personPVM) {
        return personService.savePerson(PersonPVM.pvmToModel(personPVM));
    }


    @GetMapping("/{lastName}")
    public Person findByEmail(@PathVariable("lastName") String lastName) {
        return personService.findByPersonInfo_LastName(lastName);
    }

}
