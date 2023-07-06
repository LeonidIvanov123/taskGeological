package ru.leonid.taskGeological.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.leonid.taskGeological.Model.User;
import ru.leonid.taskGeological.Repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;
    @Override
    @Cacheable(value = "user", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            log.error("Пользователь с именем "  + username + " не найден");
            throw new UsernameNotFoundException("Пользователь с именем "  + username + " не найден");
        }
        return user;
    }

    public synchronized boolean saveUser(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            log.error("Невозможно создать пользователя " + user.getUsername() + ". Он уже существует.");
            return false;
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        log.info("Created new User " + user.getUsername());
        return true;
    }
}
