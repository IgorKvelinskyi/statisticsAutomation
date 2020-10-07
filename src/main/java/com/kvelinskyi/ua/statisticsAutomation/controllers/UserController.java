package com.kvelinskyi.ua.statisticsAutomation.controllers;

import com.kvelinskyi.ua.statisticsAutomation.entity.Message;
import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.User;
import com.kvelinskyi.ua.statisticsAutomation.helper.FormatTheDate;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.HeaderTableATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.OwiATOCreationForm;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.SetOwiATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.workWordDOCX.DomEditXML;
import com.kvelinskyi.ua.statisticsAutomation.helper.workWordDOCX.XmlFileToDOCX;
import com.kvelinskyi.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskyi.ua.statisticsAutomation.repository.OwiATORepository;
import com.kvelinskyi.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.sql.Date;
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
    @Qualifier("deleteTimeFiles")
    private CommandLineRunner commandLineRunner;

    public UserController(MessageRepository messageRepository, OwiATORepository owiATORepository, ReportingWeekATORepository reportingWeekATORepository, CommandLineRunner commandLineRunner) {
        this.messageRepository = messageRepository;
        this.owiATORepository = owiATORepository;
        this.reportingWeekATORepository = reportingWeekATORepository;
        this.commandLineRunner = commandLineRunner;
    }

    //TODO delete
    @RequestMapping(value = "/getMessage")
    public ModelAndView doPrintMessage(@AuthenticationPrincipal User userSpring) {
        ModelAndView mod = new ModelAndView();
//        log.info("class AdminController - RequestMapping usersEditData");
        Iterable<Message> listMessage = messageRepository.findAll();
        mod.addObject("listMessage", listMessage);
        mod.setViewName("user/getMessage");
        return mod;
    }

    //TODO delete
    @RequestMapping(value = "/setMessage")
    public ModelAndView setMessage() {
        ModelAndView modelAndView = new ModelAndView();
        //log.info("class LoginController - IndexController(/index) has started !");
        modelAndView.setViewName("user/setMessage");
        return modelAndView;
    }

    //TODO delete
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
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeek/newTimeInterval")
    public ModelAndView newReportingWeekATO(@RequestParam("dateStart") java.sql.Date dateStart,
                                            @RequestParam("dateEnd") java.sql.Date dateEnd) {
//        log.info("class UserController - View /reportingWeek/newTimeInterval");
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd);
        if (reportingWeekATORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd) == null || reportingWeekATO.getOwiATOSet().size() == 0) {
            reportingWeekATO = new ReportingWeekATO();
            reportingWeekATO.setDateStart(dateStart);
            reportingWeekATO.setDateEnd(dateEnd);
            SetOwiATO setOwiATO = new SetOwiATO();
            reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        } else {
            mod.addObject("msg", "Такий звіт ( з "
                    + dateStart + " по " + dateEnd + " ) існуе");
        }
        mod.addObject("message", "Новий період додано");
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/atoTable")
    public ModelAndView doFormATO(@RequestParam("idReportingWeek") Long idReportingWeek, RedirectAttributes redirectAttributes) {
        ModelAndView mod = new ModelAndView();
        if (idReportingWeek == 0) {
            redirectAttributes.addFlashAttribute("msg", "Виберіть період");
            mod.setViewName("redirect:/user/atoInfo");
            return mod;
        }
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        //TODO check last table if true add { reportingWeekATORepository.save(reportingWeekATO); }
        if (reportingWeekATO.getOwiATOSet() == null || reportingWeekATO.getOwiATOSet().size() == 0) {
            SetOwiATO setOwiATO = new SetOwiATO();
            reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        }
        OwiATOCreationForm owiATOForm = new OwiATOCreationForm();
        owiATOForm.setOwiATOList(owiATOForm.listSort(generatingTableData(reportingWeekATO.getOwiATOSet(), reportingWeekATO.getDateEnd())));
        String message = "Введіть дані в таблицю";
        mod.addObject("tableHeader", HeaderTableATO.createTableHeaderATO());
        mod.addObject("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        mod.addObject("timeInterval", timeIntervalConvert(reportingWeekATO));
        mod.addObject("owiATOForm", owiATOForm);
        mod.addObject("reportingWeekATO", reportingWeekATO);
        mod.addObject("idReportingWeek", idReportingWeek);
        mod.addObject("message", message);
        mod.setViewName("user/atoTable");
        return mod;
    }

    @RequestMapping("/editAtoTable/{idReportingWeek}")
    public String doEditFormATO(@PathVariable Long idReportingWeek, OwiATOCreationForm owiATOForm, Model model) {
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        reportingWeekATO.setOwiATOSet(owiATOForm.getOwiATOList());
        reportingWeekATORepository.save(reportingWeekATO);
        List<OwiATO> owiATOList = generatingTableData(owiATOForm.getOwiATOList(), reportingWeekATO.getDateEnd());
        reportingWeekATO.setOwiATOSet(owiATOList);
        reportingWeekATORepository.save(reportingWeekATO);
        reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        owiATOForm.setOwiATOList(owiATOForm.listSort(reportingWeekATO.getOwiATOSet()));
        String message = "Дані введені, перевірте результат.";
        model.addAttribute("tableHeader", HeaderTableATO.createTableHeaderATO());
        model.addAttribute("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        model.addAttribute("timeInterval", timeIntervalConvert(reportingWeekATO));
        model.addAttribute("owiATOForm", owiATOForm);
        model.addAttribute("reportingWeekATO", reportingWeekATO);
        model.addAttribute("idReportingWeek", idReportingWeek);
        model.addAttribute("message", message);
        return "user/atoTable";
    }

    @RequestMapping("/saveWordDocument")
    public ModelAndView saveWordDocument(@RequestParam("idReportingWeek") Long idReportingWeek, RedirectAttributes redirectAttributes) throws Docx4JException {
        ModelAndView mod = new ModelAndView();
        if (idReportingWeek == 0) {
            mod.addObject("msg", "Виберіть період для звіта");
            mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
            //mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
           // mod.setViewName("redirect:/user/saveWordDocument");
            return mod;
        }
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        XmlFileToDOCX xmlFileToDOCX = new XmlFileToDOCX();
        DomEditXML domEditXML = new DomEditXML();
        String nameFileDOCX = "АТО_оперативна_інформація_за_тиждень_з_"
                + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateStart()) + "_по_" + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateEnd())
                + "_КНП_Клінічна_лікарня_ПСИХІАТРІЯ.docx";
        File fileDocx = new File(nameFileDOCX);
        //TODO create ENUM (fileXmlPath)
        String fileXmlPath = "xml-dir\\";
        try {
            fileDocx = xmlFileToDOCX.saveDocumentWord(domEditXML.changeDataFileXML
                            (fileXmlPath + "formDOCXato.xml", fileXmlPath + "formDOCXatoOutput.xml", reportingWeekATO),
                    nameFileDOCX);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
        mod.addObject("fileAbsolutePath", fileDocx.getAbsolutePath());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    @RequestMapping("/saveWordDocumentPage")
    public ModelAndView doWordDocumentPage() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    private List<OwiATO> generatingTableData(List<OwiATO> owiATOSet, Date dateEnd) {
        List<ReportingWeekATO> owiATOList = reportingWeekATORepository.findByDateStartBefore(dateEnd);
        if (owiATOList == null || owiATOList.size() == 0) {
            return generatingTableDataTotal(owiATOSet);
        }
        //OwiATO owiATO = new OwiATO();
        owiATOSet = resetTotalOwiATO(owiATOSet);
        for (OwiATO owiATO : owiATOSet
        ) {
            for (ReportingWeekATO reportingWeekATO : owiATOList
            ) {
                List<OwiATO> currentOwiATOSet = reportingWeekATO.getOwiATOSet();
                for (OwiATO owiATO1 : currentOwiATOSet
                ) {
                    if (owiATO.getName().equals(owiATO1.getName())) {
                        owiATO.setWholePeriodCivilian(owiATO.getWholePeriodCivilian() + owiATO1.getReportingWeekCivilian());
                        owiATO.setWholePeriodSoldiers(owiATO.getWholePeriodSoldiers() + owiATO1.getReportingWeekSoldiers());
                        owiATO.setWholePeriodDemobilized(owiATO.getWholePeriodDemobilized() + owiATO1.getReportingWeekDemobilized());
                        owiATO.setWholePeriodWomen(owiATO.getWholePeriodWomen() + owiATO1.getReportingWeekWomen());
                        owiATO.setWholePeriodChildren(owiATO.getWholePeriodChildren() + owiATO1.getReportingWeekChildren());
                    }
                }
            }
        }
        return generatingTableDataTotal(owiATOSet);
    }

    private List<OwiATO> resetTotalOwiATO(List<OwiATO> owiATOSet) {
        for (OwiATO owiATO : owiATOSet
        ) {
            owiATO.setWholePeriodCivilian(0);
            owiATO.setWholePeriodSoldiers(0);
            owiATO.setWholePeriodDemobilized(0);
            owiATO.setWholePeriodWomen(0);
            owiATO.setWholePeriodChildren(0);
        }
        return owiATOSet;
    }

    private List<OwiATO> generatingTableDataTotal(List<OwiATO> owiATOSet) {
        for (OwiATO owiATO : owiATOSet
        ) {
            owiATO.setWholePeriodTotal(owiATO.getWholePeriodCivilian() + owiATO.getWholePeriodSoldiers()
                    + owiATO.getWholePeriodDemobilized() + owiATO.getWholePeriodWomen() + owiATO.getWholePeriodChildren());
            owiATO.setReportingWeekTotal(owiATO.getReportingWeekCivilian() + owiATO.getReportingWeekSoldiers()
                    + owiATO.getReportingWeekDemobilized() + owiATO.getReportingWeekWomen() + owiATO.getReportingWeekChildren());
        }
        return owiATOSet;
    }

    private String timeIntervalConvert(ReportingWeekATO reportingWeekATO) {
        return "з " + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateStart()) +
                " по " + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateEnd());
    }

    /*@RequestMapping(value = "/uploadFile")
    public ModelAndView  doUploadFile() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("filename", fileToUpload.getName());
        mod.setViewName("user/uploadForm");
                return mod ;
        }*/

    //TODO check mistake(delete method)
    @RequestMapping("/divisionByZero")
    public ModelAndView doDivisionByZero() {
        ModelAndView mod = new ModelAndView();
        try {
            commandLineRunner.run("storage");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // int a = 1 / 0;
        mod.setViewName("index");
        return mod;

    }

}
