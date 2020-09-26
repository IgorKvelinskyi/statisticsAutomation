package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import com.kvelinskiy.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskiy.ua.statisticsAutomation.entity.User;
import com.kvelinskiy.ua.statisticsAutomation.helper.*;
import com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX.DomEditXML;
import com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX.XmlFileToDOCX;
import com.kvelinskiy.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.OwiATORepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
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

    @RequestMapping("/atoTable")
    public ModelAndView doFormATO(@RequestParam("idReportingWeek") Long idReportingWeek){
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        //TODO check last table if true add (reportingWeekATORepository.save(reportingWeekATO);)
        if (reportingWeekATO.getOwiATOSet() == null || reportingWeekATO.getOwiATOSet().size() == 0) {
            SetOwiATO setOwiATO = new SetOwiATO();
            reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        }
        String timeInterval = "З: " + reportingWeekATO.getDateStart() + " По: " + reportingWeekATO.getDateEnd();
        List<String> tableHeader = HeaderTableATO.createTableHeaderATO();
        OwiATOCreationForm owiATOForm = new OwiATOCreationForm();
        owiATOForm.setOwiATOList(owiATOForm.listSort(reportingWeekATO.getOwiATOSet()));
        mod.addObject("tableHeader", tableHeader);
        mod.addObject("timeInterval", timeInterval);
        mod.addObject("owiATOForm", owiATOForm);
        mod.addObject("reportingWeekATO", reportingWeekATO);
        mod.addObject("idReportingWeek", idReportingWeek);
        mod.setViewName("user/atoTable");
        return mod;
    }

    @RequestMapping("/editAtoTable/{idReportingWeek}")
    public String doEditFormATO(@PathVariable Long idReportingWeek, OwiATOCreationForm owiATOForm, Model model){
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        reportingWeekATO.setOwiATOSet(owiATOForm.getOwiATOList());
        reportingWeekATORepository.save(reportingWeekATO);
        reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        List<String> tableHeader = HeaderTableATO.createTableHeaderATO();
        String timeInterval = "З: " + reportingWeekATO.getDateStart() + " По: " + reportingWeekATO.getDateEnd();
        owiATOForm.setOwiATOList(owiATOForm.listSort(reportingWeekATO.getOwiATOSet()));
        model.addAttribute("tableHeader", tableHeader);
        model.addAttribute("timeInterval", timeInterval);
        model.addAttribute("owiATOForm", owiATOForm);
        model.addAttribute("reportingWeekATO", reportingWeekATO);
        model.addAttribute("idReportingWeek", idReportingWeek);
        return "user/atoTable";
    }

    @RequestMapping("/saveWordDocument")
    public ModelAndView doFormATO() throws Docx4JException {
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findById((long) 17).orElseThrow(EntityNotFoundException::new);
        XmlFileToDOCX xmlFileToDOCX = new XmlFileToDOCX();
        DomEditXML domEditXML = new DomEditXML();
        try {
            xmlFileToDOCX.saveDocumentWord(domEditXML
                    .changeDataFileXML("formDOCXato.xml", "formDOCXatoOutput.xml", reportingWeekATO),
                    "formATO.docx");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        mod.setViewName("/index");
        return mod;
    }

}
