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


    private String name;
    private String surname;
    private String group;

    public NewsGenerator() {

    }

    public String generateNews() {
        String strNews = null;
        StringBuilder newsBuilder = new StringBuilder();
        StringBuilder nameBuilder = createName();

        newsBuilder.append(nameBuilder);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

            //начинаем составлять новость
            //определяем тип новости:
            //  1 - о помощи,
            //  2 - специальная новость,
            //  3 - о выбросе
            //  4 - новости фракций
            //  5 - новости о нахождении артефактов
            int newsNumb = getRndIntInRange(1, 5);

            if (group.equals("Зомбированные")) newsNumb = 2;

            switch (newsNumb) {
                case 1:
//                    System.out.println("HELP_NEWS");
                    newsBuilder.append(genHelpNews(node));
                    break;
                case 2:
//                    System.out.println("SPECIAL_NEWS");
                    newsBuilder.append(genSpecialNews(node));
                    break;
                case 3:
//                    System.out.println("SURGE_NEWS");
                    newsBuilder.append(genSurgeNews(node));
                    break;
                case 4:
//                    System.out.println("FACTION_NEWS");
                    newsBuilder.append(genFactionNews(node));
                    break;
                case 5:
//                    System.out.println("ARTEFACT_NEWS");
                    newsBuilder.append(genArtefactNews(node));
                    break;
            }

            strNews = replaceTemplates(newsBuilder, node);

//            System.out.println(newsNumb + " " + newsBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strNews;
    }


    private StringBuilder genHelpNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        int newsNumb = getRndIntInRange(1,2); //определяем зовут ли на помощь: 1 - зовут, 2 - не зовут
        if (newsNumb == 1) {
            switch (group) {
                case "Одиночки":
                    newsBuilder.append(node.findValue("SOS").findValue("loner").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Монолит":
                    newsBuilder.append(node.findValue("SOS").findValue("monolith").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Бандиты":
                    newsBuilder.append(node.findValue("SOS").findValue("bandit").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Ренегаты":
                    newsBuilder.append(node.findValue("SOS").findValue("bandit").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Долг":
                    newsBuilder.append(node.findValue("SOS").findValue("dolg").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Учёные":
                    newsBuilder.append(node.findValue("SOS").findValue("ecolog").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Чистое Небо":
                    newsBuilder.append(node.findValue("SOS").findValue("clear_sky").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Военные":
                    newsBuilder.append(node.findValue("SOS").findValue("army").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Наёмники":
                    newsBuilder.append(node.findValue("SOS").findValue("mercenary").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
                case "Свобода":
                    newsBuilder.append(node.findValue("SOS").findValue("freedom").findValues("text")
                            .get(getRndIntInRange(0, 3)).asText()).append(" ");
                    break;
            }
        }
        newsBuilder.append(node.findValue("mutants").findValues("text").get(getRndIntInRange(0, 105)).asText()).
                append(" ").
                append(node.findValue("direction").findValues("text").get(getRndIntInRange(0, 9)).asText()).
                append(" ");
        switch (getRndIntInRange(1, 2)) {
            case 1:
                newsBuilder.append(node.findValue("blizko").findValues("text").get(getRndIntInRange(0, 153)).asText()).append(" ");
                break;
            case 2:
                newsBuilder.append(node.findValue("daleko").findValues("text").get(getRndIntInRange(0, 153)).asText()).append(" ");
                break;
        }

        return newsBuilder;
    }

    private StringBuilder genSpecialNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        if (group.equals("Зомбированные")) {
            newsBuilder.append(node.findValue("zombie_news").
                    findValue("dumb_zombies").
                    findValues("text").get(getRndIntInRange(0, 5)).asText()).
                    append(" ");
        }
        else {
            newsBuilder.append(node.findValue("special_news").findValues("text").get(getRndIntInRange(0, 175)).asText()).
                    append(" ");
        }
        return newsBuilder;
    }

    private StringBuilder genSurgeNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        int newsNumb = getRndIntInRange(1, 2); //1 - случайная новость, 2 - составление новости фракции из кусков
        if (newsNumb == 1) {
            newsBuilder.append(node.findValue("surge_template").findValues("text")
                    .get(getRndIntInRange(0, 13)).asText()).append(" ");
        }
        else {
            switch (group) {
                case "Одиночки":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Монолит":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("monolith")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Бандиты":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 4))
                            .asText()).append(" ");
                    break;
                case "Ренегаты":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 4))
                            .asText()).append(" ");
                    break;
                case "Долг":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Учёные":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Чистое Небо":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Военные":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Наёмники":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
                case "Свобода":
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("start").findValues("text").get(getRndIntInRange(0, 6))
                            .asText()).append(" ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("mid").findValues("text").get(getRndIntInRange(0, 5))
                            .asText()).append(". ");
                    newsBuilder.append(node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("end").findValues("text").get(getRndIntInRange(0, 3))
                            .asText()).append(" ");
                    break;
            }
        }
        return newsBuilder;
    }

    private StringBuilder genFactionNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        switch (group) {
            case "Одиночки":
                newsBuilder.append(node.findValue("faction_news").findValue("clear_sky").findValues("text")
                        .get(getRndIntInRange(0, 11)).asText()).append(" ");
                break;
            case "Монолит":
                newsBuilder.append(node.findValue("faction_news").findValue("monolith").findValues("text")
                        .get(getRndIntInRange(0, 23)).asText()).append(" ");
                break;
            case "Бандиты":
                newsBuilder.append(node.findValue("faction_news").findValue("bandit").findValues("text")
                        .get(getRndIntInRange(0, 16)).asText()).append(" ");
                break;
            case "Ренегаты":
                newsBuilder.append(node.findValue("faction_news").findValue("bandit").findValues("text")
                        .get(getRndIntInRange(0, 16)).asText()).append(" ");
                break;
            case "Долг":
                newsBuilder.append(node.findValue("faction_news").findValue("dolg").findValues("text")
                        .get(getRndIntInRange(0, 28)).asText()).append(" ");
                break;
            case "Учёные":
                newsBuilder.append(node.findValue("faction_news").findValue("scientist").findValues("text")
                        .get(getRndIntInRange(0, 10)).asText()).append(" ");
                break;
            case "Чистое Небо":
                newsBuilder.append(node.findValue("faction_news").findValue("clear_sky").findValues("text")
                        .get(getRndIntInRange(0, 11)).asText()).append(" ");
                break;
            case "Военные":
                newsBuilder.append(node.findValue("faction_news").findValue("army").findValues("text")
                        .get(getRndIntInRange(0, 16)).asText()).append(" ");
                break;
            case "Наёмники":
                newsBuilder.append(node.findValue("faction_news").findValue("mercenary").findValues("text")
                        .get(getRndIntInRange(0, 17)).asText()).append(" ");
                break;
            case "Свобода":
                newsBuilder.append(node.findValue("faction_news").findValue("freedom").findValues("text")
                        .get(getRndIntInRange(0, 31)).asText()).append(" ");
                break;
        }
        return newsBuilder;
    }

    private StringBuilder genArtefactNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        newsBuilder.append(node.findValue("found_artefacts").findValues("text")
                .get(getRndIntInRange(0, 3)).asText()).append(" ");

        return newsBuilder;
    }

    private StringBuilder createName() {
        StringBuilder nameBuilder = new StringBuilder();

        group = Resources.getGroupsList().get(getRndIntInRange(0, Resources.getGroupsList().size() - 1));

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

        return nameBuilder;
    }

    private String replaceTemplates(StringBuilder newsBuilder, JsonNode node) {
        String strNews = newsBuilder.toString();

        if (newsBuilder.toString().contains("$who")) {
            String replacement = node.findValue("who_mutant").findValues("text").get(getRndIntInRange(0, 43)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$who", replacement);
        }

        if (newsBuilder.toString().contains("$surge") && newsBuilder.toString().contains("$when")) {
            String replacement = node.findValue("surge_type").findValues("text").get(getRndIntInRange(0, 2)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$surge", replacement);
            replacement = node.findValue("utilities").findValue("time_phase").findValues("text").get(getRndIntInRange(0, 2)).asText();
            strNews = strNews.replaceAll("\\$when", replacement);
        }
        else if (newsBuilder.toString().contains("$when")) {
            String replacement = node.findValue("utilities").findValue("time_phase").findValues("text").get(getRndIntInRange(0, 2)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$when", replacement);
        }

        if (newsBuilder.toString().contains("$artefact")) {
            List<String> artefactsList = Resources.getArtefactsList();
            String replacement = artefactsList.get(getRndIntInRange(0, artefactsList.size() - 1));
            strNews = newsBuilder.toString().replaceAll("\\$artefact", replacement);
        }

        return strNews;
    }

    public String generateResponse(int responseType) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder responseBuilder = new StringBuilder();
        StringBuilder nameBuilder = createName();

        responseBuilder.append(nameBuilder);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

            if (responseType == 1) {
                responseBuilder.append(node.findValue("response_dumb_zombies").
                                findValues("text").get(getRndIntInRange(0, 6)).asText()).
                                append(" ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }

    private int getRndIntInRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

}
