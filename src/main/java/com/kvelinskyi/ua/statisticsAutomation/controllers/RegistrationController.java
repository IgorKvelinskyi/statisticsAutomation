package com.kvelinskyi.ua.statisticsAutomation.controllers;

import com.kvelinskyi.ua.statisticsAutomation.entity.Role;
import com.kvelinskyi.ua.statisticsAutomation.entity.User;
import com.kvelinskyi.ua.statisticsAutomation.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
@RequestMapping("/admin")
public class RegistrationController {
    private final UserRepository userRepository;

    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/toRegistration")
    public ModelAndView doRegistration() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        modelAndView.setViewName("admin/registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView addUser(@RequestParam("username") String username,
                                @RequestParam("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        User userFromDb = userRepository.findByUsername(username);
        if (userFromDb != null) {
            modelAndView.addObject("msg", "Такий користовач існуе");
            modelAndView.setViewName("admin/registration");
        } else {
            User newUser = new User();
            newUser.setActive(true);
            newUser.setUsername(username);
            newUser.setRoles(Collections.singleton(Role.ROLE_USER));
            newUser.setPassword(new BCryptPasswordEncoder(8).encode(password));
            userRepository.save(newUser);
            modelAndView.addObject("listAllUsers", userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
            modelAndView.setViewName("admin/usersData");
        }
        return modelAndView;
    }
}
