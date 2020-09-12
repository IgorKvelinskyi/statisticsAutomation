package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import com.kvelinskiy.ua.statisticsAutomation.entity.User;
import com.kvelinskiy.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;

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
        mod.addObject("listAllUsers", userRepository.findAll());
//        log.info("class AdminController - RequestMapping usersEditData");
        mod.setViewName("admin/usersData");
        return mod;
    }

    @RequestMapping("/user/onOff/{id}")
    public String doIsActiveUser(@PathVariable Long id, Model model) {
        User userFromDb = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userFromDb.setActive(userFromDb.isActive()? false : true);
        userRepository.save(userFromDb);
        model.addAttribute("listAllUsers", userRepository.findAll());
        return "admin/usersData";
    }

    @RequestMapping("/user/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
//        log.info("class AdminController - edit user id= " + id);
        model.addAttribute("user", userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        return "admin/userEditData";
    }

    @RequestMapping(value = "/setMessage")
    public ModelAndView setMessage() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        modelAndView.setViewName("admin/setMessage");
        return modelAndView;
    }

    @RequestMapping(value = "/message/add", method = RequestMethod.GET)
    public ModelAndView doAddMessage(@RequestParam("text") String text) {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        Message newMessage = new Message();
        newMessage.setText(text);
        messageRepository.save(newMessage);
        Iterable<Message> listMessage = messageRepository.findAll();
        modelAndView.addObject("listMessage", listMessage);
        modelAndView.setViewName("user/getMessage");
        return modelAndView;
    }
}
