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

<<<<<<< HEAD
    public String generateNews() {
        String str =    resources.getMilitaryRanks().get(2) + " " + resources.getMilitarySurnames().get(5) +
                        " застрелил " +
                        resources.getMilitaryRanks().get(4) + " " + resources.getMilitarySurnames().get(23);
        return str;
=======
    private StringBuilder genFactionNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        List<JsonNode> phrasesList;
        switch (group) {
            case "Одиночки":
                phrasesList = node.findValue("faction_news").findValue("clear_sky").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Монолит":
                phrasesList = node.findValue("faction_news").findValue("monolith").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Бандиты":
                phrasesList = node.findValue("faction_news").findValue("bandit").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Ренегаты":
                phrasesList = node.findValue("faction_news").findValue("bandit").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Долг":
                phrasesList = node.findValue("faction_news").findValue("dolg").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Учёные":
                phrasesList = node.findValue("faction_news").findValue("scientist").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Чистое Небо":
                phrasesList = node.findValue("faction_news").findValue("clear_sky").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Военные":
                phrasesList = node.findValue("faction_news").findValue("army").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Наёмники":
                phrasesList = node.findValue("faction_news").findValue("mercenary").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
            case "Свобода":
                phrasesList = node.findValue("faction_news").findValue("freedom").findValues("text");
                newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                break;
        }
        return newsBuilder;
    }

    private StringBuilder genArtefactNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        List<JsonNode> phrasesList = node.findValue("found_artefacts").findValues("text");
        newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");

        return newsBuilder;
    }

    private StringBuilder genInstantNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        int newsNumb = getRndIntInRange(1, 3);
        switch (newsNumb) {
            case 1: //новость об атаке кем-то
                List<JsonNode> attackStartsList = node.findValue("instant_news").findValue("start")
                                                    .findValue("attacked").findValues("text");
                List<JsonNode> mutantsList = node.findValue("utilities").findValue("who_mutant").findValues("text");
                newsBuilder.append(attackStartsList.get(getRndIntInRange(0, attackStartsList.size() - 1)).asText()).append(" ")
                            .append(mutantsList.get(getRndIntInRange(0, mutantsList.size() - 1)).asText()).append("!");
                newsBuilder.append(" ");
                newsBuilder.append(generateLocation(node).toString());
                break;
            case 2: //новость о том, что что-то услышали
                List<JsonNode> hearStartsList = node.findValue("instant_news").findValue("start")
                                                    .findValue("hear").findValues("text");
                List<JsonNode> hearMidsList = node.findValue("instant_news")
                                                    .findValue("hear_mid").findValues("text");
                List<JsonNode> hearEndsList = node.findValue("instant_news").findValue("end").findValues("text");

                newsBuilder.append(hearStartsList.get(getRndIntInRange(0, hearStartsList.size() - 1)).asText())
                            .append(" ").append(hearMidsList.get(getRndIntInRange(0, hearMidsList.size() - 1)).asText());
                newsBuilder.append(". ");
                newsBuilder.append(generateLocation(node).toString());
                newsBuilder.append(hearEndsList.get(getRndIntInRange(0, hearEndsList.size() - 1)).asText());
                break;
            case 3: //новость о том, что что-то увидели
                List<JsonNode> seeStartsList = node.findValue("instant_news").findValue("start")
                                                    .findValue("see").findValues("text");
                newsBuilder.append(seeStartsList.get(getRndIntInRange(0, seeStartsList.size() - 1)).asText()).append(" ");

                List<JsonNode> whosList;
                List<JsonNode> howKilledList;
                switch (getRndIntInRange(1, 2)) { //выбираем один человек убил или несколько
                    case 1:
                        whosList = node.findValue("who_human").findValue("who")
                                        .findValue("single").findValues("text");
                        howKilledList = node.findValue("how_killed").findValue("single")
                                            .findValues("text");
                        newsBuilder.append(whosList.get(getRndIntInRange(0, whosList.size() - 1)).asText()).append(" ")
                                    .append(howKilledList.get(getRndIntInRange(0, howKilledList.size() - 1)).asText()).append(" ");
                        break;
                    case 2:
                        whosList = node.findValue("who_human").findValue("who").findValue("multi").findValues("text");
                        howKilledList = node.findValue("how_killed").findValue("multi").findValues("text");
                        newsBuilder.append(whosList.get(getRndIntInRange(0, whosList.size() - 1)).asText()).append(" ")
                                .append(howKilledList.get(getRndIntInRange(0, howKilledList.size() - 1)).asText()).append(" ");
                        break;
                }

                List<JsonNode> whomsList = node.findValue("who_human").findValue("whom")
                                                .findValue("single").findValues("text");
                switch (getRndIntInRange(1, 2)) { //выбираем убили одного человека или группу
                    case 1:
                        newsBuilder.append(whomsList.get(getRndIntInRange(0, whomsList.size() - 1)).asText());
                        break;
                    case 2:
                        List<JsonNode> groupTypesList = node.findValue("who_human").findValue("whom")
                                                            .findValue("multi").findValue("group_type").findValues("text");
                        whomsList = node.findValue("who_human").findValue("whom").findValue("multi")
                                         .findValue("who").findValues("text");

                        newsBuilder.append(groupTypesList.get(getRndIntInRange(0, groupTypesList.size() - 1)).asText()).append(" ")
                                    .append(whomsList.get(getRndIntInRange(0, whomsList.size() - 1)).asText());
                }
                newsBuilder.append(". ");
                newsBuilder.append(generateLocation(node).toString());
                break;
        }

        return newsBuilder;
    }

    private StringBuilder genSystemKilledNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        newsBuilder.append("Общий канал:\n");
        newsBuilder.append(generateNameKilled()).append(". ").append(generateLocation(node));
        return newsBuilder;
    }

    private StringBuilder genTimeNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        Date date = new Date();
        //date.toString() -> Sat Aug 22 21:52:58 MSK 2020
        //date.toString().split(" ")[3] -> 21:52:58
        //date.toString().split(" ")[3].split(":")[0] -> 21
        //получаем текущий час (24-часовой формат времени)
        int hoursNow = Integer.parseInt(date.toString().split(" ")[3].split(":")[0]);

        if (hoursNow >= 0 && hoursNow <= 5) {           //night
            List<JsonNode> phrasesList = node.findValue("time_news").findValue("time_night").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        }
        else if (hoursNow >= 6 && hoursNow <= 11) {     //morning
            List<JsonNode> phrasesList = node.findValue("time_news").findValue("time_morning").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        }
        else if (hoursNow >= 12 && hoursNow <= 17) {    //noon
            List<JsonNode> phrasesList = node.findValue("time_news").findValue("time_noon").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        }
        else { //hoursNow >= 18 && hoursNow <= 23       //evening
            List<JsonNode> phrasesList = node.findValue("time_news").findValue("time_evening").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        }

        return newsBuilder;
    }

    public StringBuilder genJokeNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        switch (getRndIntInRange(1, 3)) {
            case 1:
                newsBuilder.append(generateStartOfJoke(node));
                break;
            case 2:
                newsBuilder.append(generateStartOfJoke(node));
                break;
        }

        List<JsonNode> factJokesList = node.findValue("faction_jokes").findValue("joke").findValue(group).findValues("text");
        newsBuilder.append(factJokesList.get(getRndIntInRange(0, factJokesList.size() - 1)).asText());

        return newsBuilder;
    }

    private StringBuilder generateStartOfJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (group.equals("Бандиты") || group.equals("Ренегаты")) {
            group = "Бандиты";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (group.equals("Свобода")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (group.equals("Долг")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (group.equals("Военные")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные"
            group = "Одиночки";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }

        return builder;
    }

    private StringBuilder generateResponseToJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (group.equals("Бандиты") || group.equals("Ренегаты")) {
            group = "Бандиты";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (group.equals("Свобода")) {
            List<JsonNode> list = node.findValue("responses_jokes").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные", "Долг", "Военные"
            group = "Одиночки";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(group).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }

        return builder;
>>>>>>> 42dc28a (fixed some stuff)
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
