package com.example.zero.user;

import com.example.dto.ChangePasswordDto;
import com.example.zero.entity.ConfirmationToken;
import com.example.zero.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/sign-in")
    String signIn() {
        return "/sign-in";
    }

    @GetMapping("/sign-in-error")
    String signInError(Model model) {
        model.addAttribute("signInError", true);
        return "sign-in";
    }

    @GetMapping("/sign-up")
    String signUp(Model model) {

        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    String signUp(@ModelAttribute User user, Model model) {
        if (user.getPassword().equals(user.getRetypePassword())) {
            userService.signUpUser(user);
            return "successful-registration";
        } else{
            model.addAttribute("signUpError", true);
            return "sign-up";
        }
    }


    @GetMapping("/sign-up/confirm")
    String confirmMail(@RequestParam("token") String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.findConfirmationTokenByToken(token);
        confirmationToken.ifPresent(userService::confirmUser);
        return "account-verified";
    }

    @GetMapping("/account")
    String account(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/account/changeFields")
    String accountEdit(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("changePasswordDto",new ChangePasswordDto());
        return "account-change";
    }

    @PostMapping("/account/changeFields")
    @Transactional
    String accountEditFields(@ModelAttribute User user, @AuthenticationPrincipal User currentUser) {
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setAge(user.getAge());
        userRepository.save(currentUser);
        return "redirect:/account";
    }

    @PostMapping("/account/changePassword")
    @Transactional
    String changeUserPassword(Model model,
                              @AuthenticationPrincipal User user,
                              @ModelAttribute ChangePasswordDto changePasswordDto) {
        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getRepeatNewPassword())
                && bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return "redirect:/account";
        } else {
            model.addAttribute("changePasswordError", true);
            return accountEdit(model, user);
        }
    }

    @PostMapping("account/deleteUser")
    @Transactional
    String deleteCurrentUser(@AuthenticationPrincipal User currentUser) {
        currentUser.setDeleted(true);
        userRepository.save(currentUser);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "successfully-deleted";
    }

    @GetMapping("/deleteUserSuccess")
    String deleteUserSuccess() {
        return "successfully-deleted";
    }

}