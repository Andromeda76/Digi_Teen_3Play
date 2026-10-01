package digiteen3play.service;


import digiteen3play.model.Person;
import digiteen3play.repository.PersonIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PersonService {


    private final PasswordEncoder passwordEncoder;
    private final PersonIRepository userRepository;


    public Person savePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return userRepository.save(person);
    }

    public Person findByPersonInfo_LastName(String lastName) {
        return userRepository.findByPersonInfo_LastName(lastName).orElse(null);
    }

    public Person findPersonByEmail(String email) {
        return userRepository.findByPersonInfo_Email(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }


}
