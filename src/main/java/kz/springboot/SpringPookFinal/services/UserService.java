package kz.springboot.SpringPookFinal.services;

import kz.springboot.SpringPookFinal.entities.Roles;
import kz.springboot.SpringPookFinal.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    Users getUserById(Long id);
    void addUser(Users user);
    List<Users> getUsers();
    void deleteUser(Long id);
    void saveUser(Users user);

    void deleteRole(Long id);
    List<Roles> getRoles();
    Roles getRoleById(Long id);
    void addRole(Roles role);
    void saveRole(Roles role);
}
