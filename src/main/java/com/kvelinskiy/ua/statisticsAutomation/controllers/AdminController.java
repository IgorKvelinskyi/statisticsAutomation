package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import com.kvelinskiy.ua.statisticsAutomation.entity.Role;
import com.kvelinskiy.ua.statisticsAutomation.entity.User;
import com.kvelinskiy.ua.statisticsAutomation.helper.UserRole;
import com.kvelinskiy.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public AdminController(MessageRepository messageRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @RequestMapping(value = "/usersData")
    public ModelAndView doUsersData() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("listAllUsers", userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
//        log.info("class AdminController - RequestMapping usersEditData");
        mod.setViewName("admin/usersData");
        return mod;
    }

    @RequestMapping("/user/onOff/{id}")
    public String doIsActiveUser(@PathVariable Long id, Model model) {
        User userFromDb = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userFromDb.setActive(userFromDb.isActive() ? false : true);
        userRepository.save(userFromDb);
        model.addAttribute("listAllUsers", userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        return "admin/usersData";
    }

    @RequestMapping("/user/edit/{id}")
    public String doEditUser(@PathVariable Long id, Model model) {
//        log.info("class AdminController - edit user id= " + id);
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        user.setPassword("");
        List<UserRole> userRoles = setUserRolesForUser(user);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("user", user);
        return "admin/userEditData";
    }

    @RequestMapping(value = "/userUpdateData", method = RequestMethod.POST)
    public String doUserUpdateData
            (@Validated User user, Model model) {
        Set<Role> roleSet = user.getRoles();
        if (!user.getPassword().isEmpty()){
           user.setPassword(new BCryptPasswordEncoder(8).encode(user.getPassword()));
           user.setActive(true);
        }else{
            user = userRepository.findById(user.getId()).orElseThrow(EntityNotFoundException::new);
        }
        user.setRoles(roleSet);
        userRepository.save(user);
        user.setPassword("");
        model.addAttribute("userRoles", setUserRolesForUser(user));
        model.addAttribute("user", user);
        return "admin/userEditData";
    }

    private List<UserRole> setUserRolesForUser(User user) {
        List<UserRole> userRoles = Arrays.asList(
                new UserRole(Role.ROLE_ADMIN, false),
                new UserRole(Role.ROLE_USER, false),
                new UserRole(Role.ROLE_INCOGNITO, false)
        );
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            switch (role) {
                case ROLE_USER -> statusActivationForUserRole(userRoles, Role.ROLE_USER);
                case ROLE_ADMIN -> statusActivationForUserRole(userRoles, Role.ROLE_ADMIN);
                default -> statusActivationForUserRole(userRoles, Role.ROLE_INCOGNITO);
            }
        }
        return userRoles;
    }

    private List<UserRole> statusActivationForUserRole(List<UserRole> userRoles, Enum nameRole) {
        for (UserRole role : userRoles) {
            if (nameRole.equals(role.getName())) {
                role.setStatus(true);
            }
        }
        return userRoles;
    }

}

