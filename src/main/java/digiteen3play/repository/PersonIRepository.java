package digiteen3play.repository;


import digiteen3play.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PersonIRepository extends JpaRepository<Person,Long> {

    Optional<Person> findByPersonInfo_Email(String email);

    Optional<Person> findByPersonInfo_LastName(String lastName);
}
