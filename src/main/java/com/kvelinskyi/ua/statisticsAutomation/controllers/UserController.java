package com.kvelinskyi.ua.statisticsAutomation.controllers;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekVPO;
import com.kvelinskyi.ua.statisticsAutomation.helper.FormatTheDate;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.HeaderTableATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.FormOwiATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.ListOwiATO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO.FormOwiVPO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO.HeaderTableVPO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO.ListOwiVPO;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocx.DomEditXML;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO.FormGeneratingTableData;
import com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocx.XmlFileToDOCX;
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
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.setViewName("user/atoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeekATO/newTimeInterval")
    public ModelAndView newReportingWeekATO(@RequestParam("dateStart") java.sql.Date dateStart,
                                            @RequestParam("dateEnd") java.sql.Date dateEnd) {
//        log.info("class UserController - View /reportingWeekATO/newTimeInterval");
        ModelAndView mod = new ModelAndView();
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd);
        if (reportingWeekATO == null || reportingWeekATO.getOwiATOList().size() == 0) {
            reportingWeekATO = new ReportingWeekATO();
            reportingWeekATO.setDateStart(dateStart);
            reportingWeekATO.setDateEnd(dateEnd);
            ListOwiATO listOwiATO = new ListOwiATO();
            reportingWeekATO.setOwiATOList(listOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
            mod.addObject("message", "Новий період додано");
        } else {
            mod.addObject("msg", "Такий звіт ( " +
                    FormatTheDate.timeIntervalATOConvert(reportingWeekATO) + " ) існуе");
        }
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
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
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findById(idReportingWeek)
                .orElseThrow(EntityNotFoundException::new);
        if (reportingWeekATO.getOwiATOList() == null || reportingWeekATO.getOwiATOList().isEmpty()) {
            ListOwiATO listOwiATO = new ListOwiATO();
            reportingWeekATO.setOwiATOList(listOwiATO.outEmptyTableOwiATO(reportingWeekATO));
            reportingWeekATORepository.save(reportingWeekATO);
        }
        FormOwiATO owiATOForm = new FormOwiATO();
        owiATOForm.setOwiATOList(owiATOForm.listSort(formGeneratingTableData.generatingTableData(reportingWeekATO.getOwiATOList(),
                reportingWeekATO.getDateEnd(), reportingWeekATORepository)));
        String message = "Введіть дані в таблицю";
        mod.addObject("tableHeader", HeaderTableATO.createTableHeaderATO());
        mod.addObject("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        mod.addObject("timeInterval", FormatTheDate.timeIntervalATOConvert(reportingWeekATO));
        mod.addObject("owiATOForm", owiATOForm);
        mod.addObject("reportingWeekATO", reportingWeekATO);
        mod.addObject("idReportingWeek", idReportingWeek);
        mod.addObject("message", message);
        mod.setViewName("user/atoTable");
        return mod;
    }

    @RequestMapping("/editAtoTable/{idReportingWeek}")
    public String doEditFormATO(@PathVariable Long idReportingWeek, FormOwiATO owiATOForm, Model model) {
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        //save current data
        reportingWeekATO.setOwiATOList(owiATOForm.getOwiATOList());
        reportingWeekATORepository.save(reportingWeekATO);
        //recalculation of the table with new data and recording.
        List<OwiATO> owiATOList = formGeneratingTableData.generatingTableData(reportingWeekATO.getOwiATOList(),
                reportingWeekATO.getDateEnd(), reportingWeekATORepository);
        reportingWeekATO.setOwiATOList(owiATOList);
        reportingWeekATORepository.save(reportingWeekATO);
        reportingWeekATO = reportingWeekATORepository
                .findById(idReportingWeek).orElseThrow(EntityNotFoundException::new);
        owiATOForm.setOwiATOList(owiATOForm.listSort(reportingWeekATO.getOwiATOList()));
        String message = "Дані введені, перевірте результат.";
        model.addAttribute("tableHeader", HeaderTableATO.createTableHeaderATO());
        model.addAttribute("tableHeaderNumbering", HeaderTableATO.createTableHeaderATONumbering());
        model.addAttribute("timeInterval", FormatTheDate.timeIntervalATOConvert(reportingWeekATO));
        model.addAttribute("owiATOForm", owiATOForm);
        model.addAttribute("reportingWeekATO", reportingWeekATO);
        model.addAttribute("idReportingWeek", idReportingWeek);
        model.addAttribute("message", message);
        return "user/atoTable";
    }
    //Table ATO End

    //Table VPO Start
        @RequestMapping(value = "/vpoInfo")
    public ModelAndView doVPOInfo() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        mod.setViewName("user/vpoInfo");
        return mod;
    }

    @RequestMapping("/reportingWeekVPO/newTimeInterval")
    public ModelAndView newReportingWeekVPO(@RequestParam("dateStart") java.sql.Date dateStart,
                                            @RequestParam("dateEnd") java.sql.Date dateEnd) {
        //        log.info("class UserController - View /reportingWeekATO/newTimeInterval");
        ModelAndView mod = new ModelAndView();
        ReportingWeekVPO reportingWeekVPO = reportingWeekVPORepository.findByDateStartAndAndDateEnd(dateStart, dateEnd);
        if (reportingWeekVPO == null || reportingWeekVPO.getOwiVPOList().isEmpty()) {
            reportingWeekVPO = new ReportingWeekVPO();
            reportingWeekVPO.setDateStart(dateStart);
            reportingWeekVPO.setDateEnd(dateEnd);
            ListOwiVPO listOwiVPO = new ListOwiVPO();
            reportingWeekVPO.setOwiVPOList(listOwiVPO.outEmptyTableOwiVPO(reportingWeekVPO));
            reportingWeekVPORepository.save(reportingWeekVPO);
            mod.addObject("message", "Новий період додано");
        } else {
            mod.addObject("msg", "Такий звіт ( " +
                    FormatTheDate.timeIntervalVPOConvert(reportingWeekVPO) + " ) існуе");
        }
        mod.setViewName("user/vpoInfo");
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        return mod;
    }

    @RequestMapping("/vpoTable")
    public ModelAndView doFormVPO(@RequestParam("idReportingWeek") Long idReportingWeek, RedirectAttributes redirectAttributes) {
        ModelAndView mod = new ModelAndView();
        if (idReportingWeek == 0) {
            redirectAttributes.addFlashAttribute("msg", "Виберіть період");
            mod.setViewName("redirect:/user/vpoInfo");
            return mod;
        }
        ReportingWeekVPO reportingWeekVPO = reportingWeekVPORepository.findById(idReportingWeek)
                .orElseThrow(EntityNotFoundException::new);
        if (reportingWeekVPO.getOwiVPOList() == null || reportingWeekVPO.getOwiVPOList().isEmpty()) {
            ListOwiVPO listOwiVPO = new ListOwiVPO();
            reportingWeekVPO.setOwiVPOList(listOwiVPO.outEmptyTableOwiVPO(reportingWeekVPO));
            reportingWeekVPORepository.save(reportingWeekVPO);
        }
        FormOwiVPO formOwiVPO = new FormOwiVPO();
        formOwiVPO.setOwiVPOList(formOwiVPO.listSort(reportingWeekVPO.getOwiVPOList()));
        String message = "Введіть дані в таблицю";
        mod.addObject("formOwiVPO", formOwiVPO);
        mod.addObject("idReportingWeek", idReportingWeek);
        mod.addObject("reportingWeekVPO", reportingWeekVPO);
        mod.addObject("timeInterval", FormatTheDate.timeIntervalVPOConvert(reportingWeekVPO));
        mod.addObject("mainHeaderVPO", HeaderTableVPO.createTableMainHeaderVPO());
        mod.addObject("secondHeaderVPO", HeaderTableVPO.createTableSecondHeaderVPO());
        mod.addObject("thirdHeaderVPO", HeaderTableVPO.createTableThirdHeaderVPO());
        mod.addObject("message", message);
        mod.setViewName("user/vpoTable");
        return mod;
    }

    @RequestMapping("/editVpoTable/{idReportingWeek}")
    public String doEditFormVPO(@PathVariable Long idReportingWeek, FormOwiVPO formOwiVPO, Model model) {
        ReportingWeekVPO reportingWeekVPO = reportingWeekVPORepository.findById(idReportingWeek)
                .orElseThrow(EntityNotFoundException::new);
        reportingWeekVPO.setOwiVPOList(formOwiVPO.getOwiVPOList());
        reportingWeekVPORepository.save(reportingWeekVPO);
        reportingWeekVPO = reportingWeekVPORepository.findById(idReportingWeek)
                .orElseThrow(EntityNotFoundException::new);
        formOwiVPO.setOwiVPOList(formOwiVPO.listSort(reportingWeekVPO.getOwiVPOList()));
        String message = "Дані введені, перевірте результат.";
        model.addAttribute("formOwiVPO", formOwiVPO);
        model.addAttribute("idReportingWeek", idReportingWeek);
        model.addAttribute("reportingWeekVPO", reportingWeekVPO);
        model.addAttribute("timeInterval", FormatTheDate.timeIntervalVPOConvert(reportingWeekVPO));
        model.addAttribute("mainHeaderVPO", HeaderTableVPO.createTableMainHeaderVPO());
        model.addAttribute("secondHeaderVPO", HeaderTableVPO.createTableSecondHeaderVPO());
        model.addAttribute("thirdHeaderVPO", HeaderTableVPO.createTableThirdHeaderVPO());
        model.addAttribute("message", message);
        return "user/vpoTable";
    }
    //Table VPO End

    @RequestMapping("/saveWordDocumentPage")
    public ModelAndView doWordDocumentPage() {
        ModelAndView mod = new ModelAndView();
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
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
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    //Save DOCX start
    @RequestMapping("/saveWordDocumentAto")
    public ModelAndView saveWordDocumentAto(@RequestParam("idReportingWeekAto") Long idReportingWeekAto) throws Docx4JException {
        ModelAndView mod = new ModelAndView();
        if (idReportingWeekAto == 0) {
            return isIdReportingWeek(mod);
        }
        ReportingWeekATO reportingWeekATO = reportingWeekATORepository.findById(idReportingWeekAto).orElseThrow(EntityNotFoundException::new);
        XmlFileToDOCX xmlFileToDOCX = new XmlFileToDOCX();
        DomEditXML domEditXML = new DomEditXML();
        String nameFileDOCX = "АТО_оперативна_інформація_за_тиждень_з_"
                + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateStart()) + "_по_" + FormatTheDate.formDdMmYyyy(reportingWeekATO.getDateEnd())
                + "_КНП_Клінічна_лікарня_ПСИХІАТРІЯ.docx";
        File fileDocx = new File(nameFileDOCX);
        //TODO create ENUM (fileXmlPath)
        //TODO file path dir for xml
        String fileXmlPath = "xml-dir/";
        try {
            fileDocx = xmlFileToDOCX.saveDocumentAtoToWord(domEditXML.changeDataAtoFileXML
                            (fileXmlPath + "formDOCXato.xml", fileXmlPath + "formDOCXatoOutput.xml", reportingWeekATO),
                    nameFileDOCX);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.addObject("fileAbsolutePathAto", fileDocx.getName());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    @RequestMapping("/saveWordDocumentVpo")
    public ModelAndView saveWordDocumentVpo(@RequestParam("idReportingWeekVpo") Long idReportingWeekVpo){
        ModelAndView mod = new ModelAndView();
        if (idReportingWeekVpo == 0) {
            return isIdReportingWeek(mod);
        }
        ReportingWeekVPO reportingWeekVPO = reportingWeekVPORepository.findById(idReportingWeekVpo).orElseThrow(EntityNotFoundException::new);
        String nameFileDOCX = "ВПО_оперативна_інформація_за_тиждень_з_"
                + FormatTheDate.formDdMmYyyy(reportingWeekVPO.getDateStart()) + "_по_" + FormatTheDate.formDdMmYyyy(reportingWeekVPO.getDateEnd())
                + "_КНП_Клінічна_лікарня_ПСИХІАТРІЯ.docx";
        File fileDocx = new File(nameFileDOCX);
        XmlFileToDOCX xmlFileToDOCX = new XmlFileToDOCX();
        DomEditXML domEditXML = new DomEditXML();
        //TODO file path dir for xml vpo
        String fileXmlPath = "xml-dir/";
        try {
            fileDocx = xmlFileToDOCX.saveDocumentVpoToWord(domEditXML.changeDataVpoFileXML
                            (fileXmlPath + "formDOCXVpo.xml", fileXmlPath + "formDOCXVpoOutput.xml", reportingWeekVPO),
                    nameFileDOCX);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.addObject("fileAbsolutePathVpo", fileDocx.getName());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }

    private ModelAndView isIdReportingWeek(ModelAndView mod){
        mod.addObject("msg", "Виберіть період для звіта");
        mod.addObject("reportingWeekVPOList", reportingWeekVPORepository.findByOrderByDateStartDesc());
        mod.addObject("reportingWeekATOList", reportingWeekATORepository.findByOrderByDateStartDesc());
        mod.setViewName("user/saveWordDocument");
        return mod;
    }
    //Save DOCX end

}
