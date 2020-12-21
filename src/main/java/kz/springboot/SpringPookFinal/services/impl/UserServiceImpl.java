package kz.springboot.SpringPookFinal.services.impl;

import kz.springboot.SpringPookFinal.entities.Roles;
import kz.springboot.SpringPookFinal.entities.Users;
import kz.springboot.SpringPookFinal.repositories.RoleRepository;
import kz.springboot.SpringPookFinal.repositories.UserRepository;
import kz.springboot.SpringPookFinal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<Roles> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users myUser = userRepository.findByEmail(s);

        if(myUser!=null){
            User secUser = new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;
        }
        throw new UsernameNotFoundException("User Not Found!");
    }

    @Override
    public void addUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public Roles getRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public void addRole(Roles role) {
        roleRepository.save(role);
    }

    @Override
    public void saveRole(Roles role) {
        roleRepository.save(role);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
