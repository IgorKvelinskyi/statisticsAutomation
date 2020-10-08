package com.kvelinskyi.ua.statisticsAutomation.controllers;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.FormatTheDate;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.HeaderTableATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.OwiATOCreationForm;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.SetOwiATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocxATO.DomEditXML;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocxATO.FormGeneratingTableData;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocxATO.XmlFileToDOCX;
import com.kvelinskyi.ua.statisticsAutomation.repository.OwiATORepository;
import com.kvelinskyi.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import com.kvelinskyi.ua.statisticsAutomation.repository.ReportingWeekVPORepository;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private final OwiATORepository owiATORepository;
    private final ReportingWeekATORepository reportingWeekATORepository;
    @Qualifier("deletingTemporaryFiles")
    private final CommandLineRunner deletingTemporaryFiles;
    private final FormGeneratingTableData formGeneratingTableData;
    private final ReportingWeekVPORepository reportingWeekVPORepository;

    public UserController(OwiATORepository owiATORepository, ReportingWeekATORepository reportingWeekATORepository, CommandLineRunner deletingTemporaryFiles, FormGeneratingTableData formGeneratingTableData, ReportingWeekVPORepository reportingWeekVPORepository) {
        this.owiATORepository = owiATORepository;
        this.reportingWeekATORepository = reportingWeekATORepository;
        this.deletingTemporaryFiles = deletingTemporaryFiles;
        this.formGeneratingTableData = formGeneratingTableData;
        this.reportingWeekVPORepository = reportingWeekVPORepository;
    }

    //Table ATO Start
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
        owiATOForm.setOwiATOList(owiATOForm.listSort(formGeneratingTableData.generatingTableData(reportingWeekATO.getOwiATOSet(),
                reportingWeekATO.getDateEnd(), reportingWeekATORepository)));
        String message = "Введіть дані в таблицю";
        mod.addObject("tableHeader", HeaderTableATO.createTableHeaderATO());
        mod.addObject("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        mod.addObject("timeInterval", FormatTheDate.timeIntervalConvert(reportingWeekATO));
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
        //save current data
        reportingWeekATO.setOwiATOSet(owiATOForm.getOwiATOList());
        reportingWeekATORepository.save(reportingWeekATO);
        //recalculation of the table with new data and recording.
        List<OwiATO> owiATOList = formGeneratingTableData.generatingTableData(reportingWeekATO.getOwiATOSet(),
                reportingWeekATO.getDateEnd(), reportingWeekATORepository);
        reportingWeekATO.setOwiATOSet(owiATOList);
        reportingWeekATORepository.save(reportingWeekATO);
        reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        owiATOForm.setOwiATOList(owiATOForm.listSort(reportingWeekATO.getOwiATOSet()));
        String message = "Дані введені, перевірте результат.";
        model.addAttribute("tableHeader", HeaderTableATO.createTableHeaderATO());
        model.addAttribute("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        model.addAttribute("timeInterval", FormatTheDate.timeIntervalConvert(reportingWeekATO));
        model.addAttribute("owiATOForm", owiATOForm);
        model.addAttribute("reportingWeekATO", reportingWeekATO);
        model.addAttribute("idReportingWeek", idReportingWeek);
        model.addAttribute("message", message);
        return "user/atoTable";
    }


    @RequestMapping("/saveWordDocument")
    public ModelAndView saveWordDocument(@RequestParam("idReportingWeek") Long idReportingWeek,
                                         RedirectAttributes redirectAttributes) throws Docx4JException {
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

    @RequestMapping("/deletingTemporaryFiles")
    public ModelAndView doDeletingTemporaryFiles() {
        ModelAndView mod = new ModelAndView();
        try {
            deletingTemporaryFiles.run("storage");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartAsc());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }
    //Table ATO End

    //Table VPO Start
    @RequestMapping(value = "/vpoInfo")
    public ModelAndView doVPOInfo() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartAsc());
        mod.setViewName("user/vpoInfo");
        return mod;
    }

}
