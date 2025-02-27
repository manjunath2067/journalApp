package com.learning.journalApp.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.journalApp.entity.User;
import com.learning.journalApp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER","ADMIN"));
        userRepository.save(user);
    }

    // This method is for saving user at journal time, so password is not encoded again
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

}
