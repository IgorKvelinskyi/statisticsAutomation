package com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX;

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
    public String saveDocumentWord(String fileInputXML, String fileOutPutDOCX) throws IOException, JAXBException, Docx4JException {
        File xmlFile = new File(fileInputXML);
        byte[] data;
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            data = new byte[(int) xmlFile.length()];
            fis.read(data);
        }
        String openXML = new String(data, "UTF-8");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.setContents(dRectangleViaXML(openXML));
        wordMLPackage.save(new File(fileOutPutDOCX));
        File docxFile = new File(fileOutPutDOCX);
        return docxFile!=null ? docxFile.getAbsolutePath() : "FILE NOT FOUND";
    }

    private Document dRectangleViaXML(String openXML) throws JAXBException {
        return (Document) XmlUtils.unmarshalString(openXML);
    }
}
