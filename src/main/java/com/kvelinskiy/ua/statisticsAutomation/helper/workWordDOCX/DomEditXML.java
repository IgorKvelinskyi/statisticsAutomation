package com.kvelinskiy.ua.statisticsAutomation.helper.workWordDOCX;

import com.kvelinskiy.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskiy.ua.statisticsAutomation.entity.ReportingWeekATO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public class DomEditXML {
    public String changeDataFileXML(String fileInputXML, String fileOutPutXML
            , ReportingWeekATO reportingWeekATO) {
        // create a new DocumentBuilderFactory
        try {
            File fXmlFile = new File(fileInputXML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            changeData(doc, fileOutPutXML, reportingWeekATO);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutPutXML;
    }

    private void changeData(Document doc, String fileOutPutXML, ReportingWeekATO reportingWeekATO) {
        NodeList nList = doc.getElementsByTagName("w:r");
        List<OwiATO> listATO = listSortOwiATO(reportingWeekATO.getOwiATOSet());
        //Title date (start-end)
        Node nNodeTitle = nList.item(8);
        if (nNodeTitle.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeTitle;
            changeTagContent(eElement, periodEntryInTitle(reportingWeekATO));
        }
        //Footer date (end)
        Node nNodeFooter = nList.item(124);
        if (nNodeFooter.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeFooter;
            changeTagContent(eElement, footerDateEnd(reportingWeekATO));
        }
        //Table data
        int i = 41;
        for (OwiATO owiATO : listATO) {
            int[] dataForTable = {owiATO.getWholePeriodTotal(), owiATO.getWholePeriodCivilian(), owiATO.getWholePeriodSoldiers(),
                    owiATO.getWholePeriodDemobilized(), owiATO.getWholePeriodWomen(), owiATO.getWholePeriodChildren(),
                    owiATO.getReportingWeekTotal(), owiATO.getReportingWeekCivilian(), owiATO.getReportingWeekSoldiers(),
                    owiATO.getReportingWeekDemobilized(), owiATO.getReportingWeekWomen(), owiATO.getReportingWeekChildren(),};
            for (int j = 0; j < 12; j++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    int value = dataForTable[j];
                    if (value == 0) {
                        changeTagContent(eElement, "-");
                    } else {
                        changeTagContent(eElement, String.valueOf(value));
                    }
                }
                i++;
            }
            i += 2;
        }
        writeDocument(doc, fileOutPutXML);
    }

    // write DOM to file
    private void writeDocument(Document document, String fileOutPutXML) throws TransformerFactoryConfigurationError {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(fileOutPutXML);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }

    private void changeTagContent(Element element, String content) {
        element.getElementsByTagName("w:t").item(0).setTextContent(content);
    }

    private List<OwiATO> listSortOwiATO(List<OwiATO> owiATOList) {
        List<OwiATO> list = owiATOList;
        Collections.sort(list, new Comparator<OwiATO>() {
            public int compare(OwiATO o1, OwiATO o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return list;
    }

    private void showAllProps(OwiATO owiATO) {
        Field[] fields = owiATO.getClass().getFields();
        for (Field field : fields) {
            if (field.getType().isPrimitive()) {
                try {
                    System.out.println(field.getName() + ": " + field.get(owiATO));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String periodEntryInTitle (ReportingWeekATO reportingWeekATO){
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMMM", ukrainianDateFormatSymbols);
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd MMMM yyyy", ukrainianDateFormatSymbols);
        return " з " + dateFormatStart.format(reportingWeekATO.getDateStart())
                + " по " + dateFormatEnd.format(reportingWeekATO.getDateEnd()) + " року";
    }

    private static DateFormatSymbols ukrainianDateFormatSymbols = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"січня", "лютого", "березня", "квітня", "травня", "червня", "липня",
                    "серпня", "вересня", "жовтня", "листопада", "грудня"};
        }

    };

    private String footerDateEnd(ReportingWeekATO reportingWeekATO) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatStart.format(reportingWeekATO.getDateEnd()) + "р.";
    }

}
