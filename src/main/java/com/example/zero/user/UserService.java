package com.example.zero.user;

import com.example.zero.entity.ConfirmationToken;
import com.example.zero.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private final EmailSenderService emailSenderService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        final Optional<User> optionalUser = userRepository.findByEmailAndDeletedFalse(email);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UsernameNotFoundException(MessageFormat.format("User with email {0} cannot be found.", email));
        }
    }

    @Transactional
    void signUpUser(User user) {

        boolean exists = userRepository.existsByEmailAndDeletedFalse(user.getEmail());

        if (exists) {
            throw new RuntimeException("User with such email is already exists");
        }

            final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());

            user.setPassword(encryptedPassword);

            final User createdUser = userRepository.save(user);

            final ConfirmationToken confirmationToken = new ConfirmationToken(createdUser);

            confirmationTokenService.saveConfirmationToken(confirmationToken);

            sendConfirmationMail(user.getEmail(), confirmationToken.getConfirmationToken());

    }

    @Transactional
    void confirmUser(ConfirmationToken confirmationToken) {

        final User user = confirmationToken.getUser();

        user.setEnabled(true);

        user.setDeleted(false);

        userRepository.save(user);

        confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());

    }

    @Transactional
    void sendConfirmationMail(String userMail, String token) {

        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userMail);
        mailMessage.setSubject("Mail Confirmation Link!");
        mailMessage.setFrom("andrewlazerko@gmail.com");
        mailMessage.setText(
                "Thank you for registering. Please click on the link to activate your account. " + "http://localhost:8080/sign-up/confirm?token="
                        + token);

        emailSenderService.sendEmail(mailMessage);
    }

}