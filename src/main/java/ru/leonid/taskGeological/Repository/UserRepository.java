package ru.leonid.taskGeological.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.leonid.taskGeological.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
