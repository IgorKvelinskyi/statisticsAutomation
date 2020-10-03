package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import com.kvelinskiy.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskiy.ua.statisticsAutomation.entity.User;
import com.kvelinskiy.ua.statisticsAutomation.helper.HeaderTableATO;
import com.kvelinskiy.ua.statisticsAutomation.helper.OwiATOCreationForm;
import com.kvelinskiy.ua.statisticsAutomation.helper.SetOwiATO;
import com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX.DomEditXML;
import com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX.XmlFileToDOCX;
import com.kvelinskiy.ua.statisticsAutomation.repository.MessageRepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.OwiATORepository;
import com.kvelinskiy.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
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
    private File fileToUpload;

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
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeek/newTimeInterval")
    public ModelAndView newReportingWeekATO(@RequestParam("dateStart") java.sql.Date dateStart,
                                                 @RequestParam("dateEnd") java.sql.Date dateEnd) {
//        log.info("class UserController - View reportingWeek/timeInterval");
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd);
        if (reportingWeekATORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd) == null || reportingWeekATO.getOwiATOSet().size() == 0) {
            reportingWeekATO = new ReportingWeekATO();
            reportingWeekATO.setDateStart(dateStart);
            reportingWeekATO.setDateEnd(dateEnd);
            SetOwiATO setOwiATO = new SetOwiATO();
            reportingWeekATO.setOwiATOSet(setOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        }else {
            mod.addObject("msg", "Такий звіт ( з "
                    + dateStart + " по " + dateEnd + " ) існуе");
        }
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/atoTable")
    public ModelAndView doFormATO(@RequestParam("idReportingWeek") Long idReportingWeek) {
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        //TODO check last table if true add { reportingWeekATORepository.save(reportingWeekATO); }
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
    public String doEditFormATO(@PathVariable Long idReportingWeek, OwiATOCreationForm owiATOForm, Model model) {
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
    public ModelAndView saveWordDocument(@RequestParam("idReportingWeek") Long idReportingWeek) throws Docx4JException {
        ModelAndView mod = new ModelAndView();
        if (idReportingWeek == 0){
            mod.addObject("msg", "Виберіть період для звіта");
            mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
            return mod;
        }
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        XmlFileToDOCX xmlFileToDOCX = new XmlFileToDOCX();
        DomEditXML domEditXML = new DomEditXML();
        String nameFileDOCX = "АТО_оперативна_інформація_за_тиждень_з_"
                + reportingWeekATO.getDateStart() + "_по_" + reportingWeekATO.getDateEnd()
                + "_КНП_Клінічна_ лікарня_ПСИХІАТРІЯ.docx";
        File fileDocx = new File(nameFileDOCX);
        try {
            fileDocx = xmlFileToDOCX.saveDocumentWord(domEditXML.changeDataFileXML
                            ("formDOCXato.xml", "formDOCXatoOutput.xml", reportingWeekATO),
                    nameFileDOCX);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        fileToUpload = fileDocx;
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.addObject("fileAbsolutePath", fileDocx.getAbsolutePath());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    @RequestMapping("/saveWordDocumentPage")
    public ModelAndView doWordDocumentPage() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findAll());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    @RequestMapping(value = "/uploadFile")
    public ModelAndView  doUploadFile() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("filename", fileToUpload.getName());
        mod.setViewName("user/uploadForm");
                return mod ;
        }

    //TODO check mistakes(delete method)
    @RequestMapping("/wordDoc")
    public ModelAndView doWordDoc() {
        ModelAndView mod = new ModelAndView();
        int a = 1 / 0;
        mod.setViewName("index");
        return mod;

    }

}
