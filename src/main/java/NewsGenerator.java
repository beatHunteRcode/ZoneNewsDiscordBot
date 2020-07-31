import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class NewsGenerator {

    private int rndNumber = -1;
    private Resources resources;

    public NewsGenerator(int rndNumber) {
        this.rndNumber = rndNumber;
        this.resources = new Resources();
    }

    public NewsGenerator() {};

    public String generateNews() {
        String str =    resources.getMilitaryRanks().get(2) + " " + resources.getMilitarySurnames().get(5) +
                        " застрелил " +
                        resources.getMilitaryRanks().get(4) + " " + resources.getMilitarySurnames().get(23);
        return str;
    }

    public void testMethod() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("./input/st_dynamic_news.xml"));

            Element element = document.getDocumentElement();
            printElements(element.getChildNodes());

        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void printElements(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("component")) {
                System.out.println(node.getAttributes().item(0).getTextContent());
            }
//            System.out.println(node.getTextContent().trim());
        }
    }
}
