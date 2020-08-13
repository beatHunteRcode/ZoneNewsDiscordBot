import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NewsGenerator {

    private int rndNumber = -1;
    private Resources resources;

    public NewsGenerator(int rndNumber) {
        this.rndNumber = rndNumber;
        this.resources = new Resources();
    }

    public NewsGenerator() {};


        public String generateNewsXML() {
        String returnNews = "";
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new File("./input/st_dynamic_news.xml"));
//
//            Element element = document.getDocumentElement();
//            printElements(element.getChildNodes());
//
//        }
//        catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }
        return returnNews;
    }

//    public void testMethod() {
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new File("./input/st_dynamic_news.xml"));
//
//            Element element = document.getDocumentElement();
////            printElements(element.getChildNodes());
//            printElements(element.getChildNodes().item(7).getChildNodes().item(7).getChildNodes());
//
//        }
//        catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public void printElements(NodeList nodeList) {
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node node = nodeList.item(i);
//            if (node.getNodeName().equals("string")) {
//                System.out.println(i + " " + node.getChildNodes().item(0).getTextContent());
//            }
////            System.out.println(node.getTextContent().trim());
//        }
//    }

    public String generateNewsJSON() {
        StringBuilder newsBuilder = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder();

        String name = null;
        String surname = null;
        String group = Resources.getGroupsList().get(getRndIntInRange(0, Resources.getGroupsList().size() - 1));

        if (group.equals("Военные") || group.equals("Долг")) {
            name = Resources.getMilitaryRanks().get(getRndIntInRange(0, Resources.getMilitaryRanks().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else if (group.equals("Учёные")) {
            name = Resources.getScientistNamesList().get(getRndIntInRange(0, Resources.getScientistNamesList().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else {
            name = Resources.getStalkerNamesList().get(getRndIntInRange(0, Resources.getStalkerNamesList().size() - 1));
            surname = Resources.getStalkerSurnamesList().get(getRndIntInRange(0, Resources.getStalkerSurnamesList().size() - 1));
        }


        nameBuilder.append(name).append(" ");
        nameBuilder.append(surname).append(" ");
        nameBuilder.append("(").append(group).append(")");
        nameBuilder.append(":").append("\n");

        newsBuilder.append(nameBuilder);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

            newsBuilder.append(node.findValue("mutants").findValues("text").get(getRndIntInRange(0, 307)).asText()).
                        append(" ").
                        append(node.findValue("direction").findValues("text").get(getRndIntInRange(0, 9)).asText()).
                        append(" ").
                        append(node.findValue("daleko").findValues("text").get(getRndIntInRange(0, 153)).asText()).
                        append(" ");

//            System.out.println(newsBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsBuilder.toString();
    }

    private int getRndIntInRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

}
