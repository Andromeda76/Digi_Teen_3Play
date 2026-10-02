package digiteen3play.controller;


import digiteen3play.pvm.AuthenticationResponsePVM;
import digiteen3play.pvm.LoginPVM;
import digiteen3play.service.security.AccessTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {


    private final AccessTokenService accessTokenService;


    @PostMapping("/authentication")
    public AuthenticationResponsePVM authenticateUser(@Valid @RequestBody LoginPVM loginPVM) {
        return accessTokenService.authenticateUser(loginPVM);
    }

}
