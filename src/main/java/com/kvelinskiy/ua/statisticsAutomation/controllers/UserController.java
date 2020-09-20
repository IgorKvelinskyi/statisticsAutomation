package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import com.kvelinskiy.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskiy.ua.statisticsAutomation.entity.User;
import com.kvelinskiy.ua.statisticsAutomation.helper.SetOwiATO;
import com.kvelinskiy.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.OwiATORepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private final MessageRepository messageRepository;
    private final OwiATORepository owiATORepository;
    private final ReportingWeekATORepository reportingWeekATORepository;

    public UserController(MessageRepository messageRepository, OwiATORepository owiATORepository, ReportingWeekATORepository reportingWeekATORepository) {
        this.messageRepository = messageRepository;
        this.owiATORepository = owiATORepository;
        this.reportingWeekATORepository = reportingWeekATORepository;
    }

    @RequestMapping(value = "/getMessage")
    public ModelAndView doPrintMessage(@AuthenticationPrincipal User userSpring) {
        ModelAndView mod = new ModelAndView();
//        log.info("class AdminController - RequestMapping usersEditData");
        Iterable<Message> listMessage = messageRepository.findAll();
        mod.addObject("listMessage", listMessage);
        mod.setViewName("user/getMessage");
        return mod;
    }

    @RequestMapping(value = "/setMessage")
    public ModelAndView setMessage() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        modelAndView.setViewName("user/setMessage");
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

    @RequestMapping(value = "/atoInfo")
    public ModelAndView doATOInfo() {
        ModelAndView mod = new ModelAndView();
        //TODO delete (List<ReportingWeekATO> reportingWeekATOList)
        List<ReportingWeekATO> reportingWeekATOList = (List<ReportingWeekATO>) reportingWeekATORepository.findAll();
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeek/timeInterval")
    public ModelAndView setDatesReportingWeekATO(@RequestParam("dateStart") java.sql.Date dateStart,
                                                 @RequestParam("dateEnd") java.sql.Date dateEnd) {
//        log.info("class AdminController - View Form2100/1 Data Of All Doctor");
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = new ReportingWeekATO();
        reportingWeekATO.setDateStart(dateStart);
        reportingWeekATO.setDateEnd(dateEnd);
        reportingWeekATORepository.save(reportingWeekATO);
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeek/openTableATO")
    public ModelAndView doEditFormATO(@RequestParam("idReportingWeek") Long idReportingWeek){
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        if (reportingWeekATO.getOwiATOSet() == null) {
            SetOwiATO setOwiATO = new SetOwiATO();
            reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        }
        SetOwiATO setOwiATO = new SetOwiATO();
        reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
        reportingWeekATORepository.save(reportingWeekATO);
        mod.setViewName("user/atoInfo");
        return mod;
    }

}
