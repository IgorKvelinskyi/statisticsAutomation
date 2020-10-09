package com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocx;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public class XmlFileToDOCX {
    public File saveDocumentAtoToWord(String fileInputXML, String fileOutPutDOCX) throws IOException, JAXBException, Docx4JException {
        File xmlFile = new File(fileInputXML);
        //TODO create ENUM for (fileDOCXPath)
        String filePath = "upload-dir\\" + fileOutPutDOCX;
        byte[] data;
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            data = new byte[(int) xmlFile.length()];
            fis.read(data);
        }
        String openXML = new String(data, "UTF-8");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.setContents(dRectangleViaXML(openXML));
        wordMLPackage.save(new File(filePath));
        File docxFile = new File(filePath);
        return docxFile!=null ? docxFile : null;
    }

    public File saveDocumentVpoToWord(String fileInputXML, String fileOutPutDOCX) throws IOException, JAXBException, Docx4JException {
        File xmlFile = new File(fileInputXML);
        //TODO create ENUM for (fileDOCXPath)
        String filePath = "upload-dir\\" + fileOutPutDOCX;
        byte[] data;
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            data = new byte[(int) xmlFile.length()];
            fis.read(data);
        }
        String openXML = new String(data, "UTF-8");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.setContents(dRectangleViaXML(openXML));
        wordMLPackage.save(new File(filePath));
        File docxFile = new File(filePath);
        return docxFile!=null ? docxFile : null;
    }

    private Document dRectangleViaXML(String openXML) throws JAXBException {
        return (Document) XmlUtils.unmarshalString(openXML);
    }
}
