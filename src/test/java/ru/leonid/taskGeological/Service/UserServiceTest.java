package ru.leonid.taskGeological.Service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.leonid.taskGeological.Model.User;
import ru.leonid.taskGeological.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    void loadUserByUsernameTest_whenUserNotExist() {
        Mockito.doReturn(null).when(userRepository).findByUsername("Testuser");
        try {
            userService.loadUserByUsername("Testuser");
        }catch (UsernameNotFoundException exception){
            Assertions.assertEquals(exception.getMessage(), "Пользователь с именем Testuser не найден");
        }
    }
    @Test
    void loadUserByUsernameTest_returnExistUserFromRepository(){
        User user = new User();
        user.setId(123L);
        user.setUsername("Testuser");
        user.setPassword("1234");
        Mockito.doReturn(user).when(userRepository).findByUsername("Testuser");

        UserDetails userDetails = userService.loadUserByUsername("Testuser");
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
        Assertions.assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void saveUser_saveNewUserInRepository() {
        User user = new User();
        user.setUsername("Testruser");
        user.setPassword("123");
        Mockito.doReturn(null).when(userRepository).findByUsername(user.getUsername());

        boolean statusOfSave = userService.saveUser(user);

        Assertions.assertTrue(statusOfSave);
    }

    @Test
    void saveUser_wherUserAlreadyExist(){
        User user = new User();
        user.setUsername("Testuser");
        user.setPassword("123");
        Mockito.doReturn(user).when(userRepository).findByUsername("Testuser");

        boolean statusOfSave = userService.saveUser(user);

        Assertions.assertFalse(statusOfSave);
    }
}