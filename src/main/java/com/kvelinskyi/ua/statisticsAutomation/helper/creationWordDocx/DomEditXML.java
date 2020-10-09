package com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocx;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.OwiVPO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekVPO;
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
    public String changeDataAtoFileXML(String fileInputXML, String fileOutPutXML
            , ReportingWeekATO reportingWeekATO) {
        // create a new DocumentBuilderFactory
        try {
            File fXmlFile = new File(fileInputXML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            changeDataAto(doc, fileOutPutXML, reportingWeekATO);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutPutXML;
    }

    public String changeDataVpoFileXML(String fileInputXML, String fileOutPutXML, ReportingWeekVPO reportingWeekVPO) {
        try {
            File fXmlFile = new File(fileInputXML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            changeDataVpo(doc, fileOutPutXML, reportingWeekVPO);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutPutXML;
    }

    private void changeDataVpo(Document doc, String fileOutPutXML, ReportingWeekVPO reportingWeekVPO) {
        NodeList nList = doc.getElementsByTagName("w:r");
        List<OwiVPO> vpoList = listSortOwiVPO(reportingWeekVPO.getOwiVPOList());
        //Title date (start-end)
        Node nNodeTitle = nList.item(6);
        if (nNodeTitle.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeTitle;
            changeTagContent(eElement, periodEntryInTitleVpo(reportingWeekVPO));
        }
        //Footer date (end)
        Node nNodeFooter = nList.item(38);
        if (nNodeFooter.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeFooter;
            changeTagContent(eElement, footerDateEndVpo(reportingWeekVPO));
        }
        writeDocument(doc, fileOutPutXML);
    }

    private void changeDataAto(Document doc, String fileOutPutXML, ReportingWeekATO reportingWeekATO) {
        NodeList nList = doc.getElementsByTagName("w:r");
        List<OwiATO> listATO = listSortOwiATO(reportingWeekATO.getOwiATOList());
        //Title date (start-end)
        Node nNodeTitle = nList.item(8);
        if (nNodeTitle.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeTitle;
            changeTagContent(eElement, periodEntryInTitleAto(reportingWeekATO));
        }
        //Footer date (end)
        Node nNodeFooter = nList.item(124);
        if (nNodeFooter.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNodeFooter;
            changeTagContent(eElement, footerDateEndAto(reportingWeekATO));
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

    private List<OwiVPO> listSortOwiVPO(List<OwiVPO> vpoList) {
        List<OwiVPO> list = vpoList;
        Collections.sort(list, new Comparator<OwiVPO>() {
            public int compare(OwiVPO o1, OwiVPO o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return list;
    }
    //TODO method for creat list data for change xml
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

    private String periodEntryInTitleAto(ReportingWeekATO reportingWeekATO) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMMM", ukrainianDateFormatSymbols);
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd MMMM yyyy", ukrainianDateFormatSymbols);
        return " з " + dateFormatStart.format(reportingWeekATO.getDateStart())
                + " по " + dateFormatEnd.format(reportingWeekATO.getDateEnd()) + " року";
    }

    private String periodEntryInTitleVpo(ReportingWeekVPO reportingWeekVPO) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMMM", ukrainianDateFormatSymbols);
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd MMMM yyyy", ukrainianDateFormatSymbols);
        return " з " + dateFormatStart.format(reportingWeekVPO.getDateStart())
                + " по " + dateFormatEnd.format(reportingWeekVPO.getDateEnd()) + " року";
    }

    private static final DateFormatSymbols ukrainianDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"січня", "лютого", "березня", "квітня", "травня", "червня", "липня",
                    "серпня", "вересня", "жовтня", "листопада", "грудня"};
        }

    };

    private String footerDateEndAto(ReportingWeekATO reportingWeekATO) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatStart.format(reportingWeekATO.getDateEnd()) + "р.";
    }

    private String footerDateEndVpo(ReportingWeekVPO reportingWeekVPO) {
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatStart.format(reportingWeekVPO.getDateEnd()) + "р.";
    }
}
