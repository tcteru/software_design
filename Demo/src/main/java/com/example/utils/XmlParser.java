package com.example.utils;

import com.example.model.DataPoint;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlParser {
    private static final String opengisurl = "http://www.opengis.net/wfs/2.0";
    private static final String xmlurl = "http://xml.fmi.fi/schema/wfs/2.0";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static List<DataPoint> parseWeatherXml(String xmlString) throws Exception {

        // Luo DocumentBuilderin XML parsimista varten
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // ottaa XML namespacet huomioon
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        // Parsii XML gocument URL:stä
        Document doc = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
        doc.getDocumentElement().normalize();

        // Hakee kaikki 'member' elementit OpenGIS namespacesta
        NodeList tvpList = doc.getElementsByTagNameNS(opengisurl, "member");

        List<DataPoint> dataList = new ArrayList<>();

        // Käydään läpi kaikki löydetyt elementit
        for (int i = 0; i < tvpList.getLength(); i++) {
            Element tvp = (Element) tvpList.item(i);

            // Haetaan BsWfsElement-nimiavaruudessa oleva elementti (sisältää säätiedot)
            Element elem = (Element) tvp.getElementsByTagNameNS(xmlurl, "BsWfsElement").item(0);
            if (elem == null) continue; // jos elementtiä ei löydy, siirrytään seuraavaan

            // Haetaan aikaleima ja muutetaan YearMonth-objektiksi
            String timeStr = tvp.getElementsByTagNameNS(xmlurl, "Time").item(0).getTextContent();
            YearMonth ym = YearMonth.from(TIME_FORMAT.parse(timeStr));

            // Haetaan parametrin nimi (esim. "rrmon" tai "tmon")
            String paramName = elem.getElementsByTagNameNS(xmlurl, "ParameterName").item(0).getTextContent();

            // Haetaan parametrin arvo merkkijonona
            String valueStr = elem.getElementsByTagNameNS(xmlurl, "ParameterValue").item(0).getTextContent();

            double value;
            try {
                value = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                value = Double.NaN;
            }
            dataList.add(new DataPoint(ym, value, paramName));
        }

        return dataList;
    }
}