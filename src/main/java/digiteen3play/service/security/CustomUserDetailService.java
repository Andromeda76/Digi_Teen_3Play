package digiteen3play.service.security;

import digiteen3play.model.Person;
import digiteen3play.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {


    private final PersonService personService;


    @Override
    public UserDetails loadUserByUsername(String email) {
        Person person = personService.findPersonByEmail(email);

        return new User(
                person.getPersonInfo().getEmail(),
                person.getPassword(),
                List.of(AuthorityUtils.createAuthorityList("ROLE_USER")
                        .toArray(new GrantedAuthority[0])));
    }
}
