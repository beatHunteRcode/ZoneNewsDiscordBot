import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.io.*;
import java.util.Date;
import java.util.List;

public class NewsGenerator {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }

    private String name;
    private String surname;
    private String faction;
    private int newsType;

    public NewsGenerator() {

    }

    private StringBuilder genFactionNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        List<JsonNode> phrasesList;
        switch (faction) {
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
                newsBuilder.append(genLocation(node).toString());
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
                newsBuilder.append(genLocation(node).toString());
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
                newsBuilder.append(genLocation(node).toString());
                break;
        }

        return newsBuilder;
    }

    private StringBuilder genSystemKilledNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        newsBuilder.append("Общий канал:\n");
        newsBuilder.append(genNameKilled()).append(". ").append(genLocation(node));
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

        List<JsonNode> factJokesList = node.findValue("faction_jokes").findValue("joke").findValue(faction).findValues("text");
        newsBuilder.append(factJokesList.get(getRndIntInRange(0, factJokesList.size() - 1)).asText());

        return newsBuilder;
    }

    private StringBuilder generateStartOfJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (faction.equals("Бандиты") || faction.equals("Ренегаты")) {
            faction = "Бандиты";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Свобода")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Долг")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Военные")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные"
            faction = "Одиночки";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }

        return builder;
    }

    private StringBuilder generateResponseToJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (faction.equals("Бандиты") || faction.equals("Ренегаты")) {
            faction = "Бандиты";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Свобода")) {
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные", "Долг", "Военные"
            faction = "Одиночки";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }

        return builder;

    }

    public String generateNews() {
        String strNews = null;
        StringBuilder newsBuilder = new StringBuilder();
        StringBuilder nameBuilder = genName().append(":\n");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

            /*
              начинаем составлять новость
              определяем тип новости:
              1 - о помощи,
              2 - специальная новость,
              3 - о выбросе
              4 - новости фракций
              5 - новости о нахождении артефактов
              6 - немедленные сообщения от сталкеров увидивших/услышивших что-то
              7 - системная новость о гибели сталкера/военного
              8 - анекдот
              9 - о времени суток
              10 - о активности кого-то рядом со сталкером
              11 - о торговце и товаре
              12 - просьба перевести сталкера из точки А в точку Б
              13 - мем
             */
            newsType = getRndIntInRange(1, 13);
            if (newsType != 7) {
                newsBuilder.append(nameBuilder);
                if (faction.equals("Зомбированные")) newsType = 2;
            }

            switch (newsType) {
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
                case 6:
//                    System.out.println("INSTANT_NEWS");
                    newsBuilder.append(genInstantNews(node));
                    break;
                case 7:
//                    System.out.println("SYSTEM_KILLED_NEWS");
                    newsBuilder.append(genSystemKilledNews(node));
                    break;
                case 8:
//                    System.out.println("JOKE_NEWS");
                    newsBuilder.append(genJokeNews(node));
                    break;
                case 9:
//                    System.out.println("TIME_NEWS");
                    newsBuilder.append(genTimeNews(node));
                    break;
                case 10:
//                    System.out.println("NEARBY_ENEMY_ACTIVITY_NEWS");
                    newsBuilder.append(genNearbyEnemyActivityNews(node));
                    break;
                case 11:
//                    System.out.println("TRADERS_AND_GOODS_NEWS");
                    newsBuilder.append(genTradersAndGoodsNews(node));
                    break;
                case 12:
//                    System.out.println("CONDUCT_NEWS");
                    newsBuilder.append(genConductNews(node));
                    break;
                case 13:
//                    System.out.println("MEME_NEWS");
                    newsBuilder.append(genMemeNews());
            }

            strNews = replaceTemplates(newsBuilder, node);

//            System.out.println(newsNumb + " " + newsBuilder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strNews;
    }


    public StringBuilder genName() {
        StringBuilder nameBuilder = new StringBuilder();

        faction = Resources.getFactionsList().get(getRndIntInRange(0, Resources.getFactionsList().size() - 1));

        if (faction.equals("Военные") || faction.equals("Долг")) {
            name = Resources.getMilitaryRanks().get(getRndIntInRange(0, Resources.getMilitaryRanks().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else if (faction.equals("Учёные")) {
            name = Resources.getScientistNamesList().get(getRndIntInRange(0, Resources.getScientistNamesList().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else {
            name = Resources.getStalkerNamesList().get(getRndIntInRange(0, Resources.getStalkerNamesList().size() - 1));
            surname = Resources.getStalkerSurnamesList().get(getRndIntInRange(0, Resources.getStalkerSurnamesList().size() - 1));
        }


        nameBuilder.append(name).append(" ");
        nameBuilder.append(surname).append(" ");
        nameBuilder.append("(").append(faction).append(")");

        return nameBuilder;
    }
    private StringBuilder genNameKilled() {
        StringBuilder nameBuilder = new StringBuilder();

        faction = Resources.getFactionsListKilled().get(getRndIntInRange(0, Resources.getFactionsListKilled().size() - 1));

        if (faction.equals("Военный") || faction.equals("Долговец") || faction.equals("\"Долг\"")) {
            name = Resources.getMilitaryRanks().get(getRndIntInRange(0, Resources.getMilitaryRanks().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else if (faction.equals("Учёный")) {
            name = Resources.getScientistNamesList().get(getRndIntInRange(0, Resources.getScientistNamesList().size() - 1));
            surname = Resources.getMilitarySurnames().get(getRndIntInRange(0, Resources.getMilitarySurnames().size() - 1));
        }
        else {
            name = Resources.getStalkerNamesList().get(getRndIntInRange(0, Resources.getStalkerNamesList().size() - 1));
            surname = Resources.getStalkerSurnamesList().get(getRndIntInRange(0, Resources.getStalkerSurnamesList().size() - 1));
        }

        nameBuilder.append("    Погиб ");
        if (faction.equals("Военный")) {
            nameBuilder.append("военный: ");
            nameBuilder.append(name).append(" ").append(surname);
        }
        else {
            nameBuilder.append("сталкер: ");
            nameBuilder.append(name).append(" ").append(surname);
            nameBuilder.append(". ").append(faction);
        }

        return nameBuilder;
    }
    public StringBuilder genTraderName() {
        StringBuilder nameBuilder = new StringBuilder();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);
            List<JsonNode> tradersNamesList = node.findValue("traders").findValue("traders_names").findValues("text");
            name = tradersNamesList.get(getRndIntInRange(0, tradersNamesList.size() - 1)).asText();
            if (name.equals("Сахаров") || name.equals("Герман")) {
                faction = "Профессор " + Resources.getTradersFactionsMap().get(name);
            }
            else {
                faction = Resources.getTradersFactionsMap().get(name);
            }
            nameBuilder.append(name).append(" (").append(faction).append(") ");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameBuilder;
    }

    private StringBuilder genLocation(JsonNode node) {
        StringBuilder locationBuilder = new StringBuilder();
        List<JsonNode> locsList = node.findValue("level_description").findValues("location");
        JsonNode locNode  = locsList.get(getRndIntInRange(0, locsList.size() - 1));
        List<JsonNode> blizkoPlaces = locNode.findValue("blizko").findValues("text");
        List<JsonNode> dalekoPlaces = locNode.findValue("daleko").findValues("text");
        String place = null;
        if (blizkoPlaces.size() > 1 || dalekoPlaces.size() > 1) {
            switch (getRndIntInRange(1, 2)) {
                case 1:
                    place = locNode.findValue("blizko").findValues("text")
                            .get(getRndIntInRange(0, blizkoPlaces.size() - 1)).asText();
                    break;
                case 2:
                    List<JsonNode> directionsList = node.findValue("utilities").findValue("direction").findValues("text");
                    place = directionsList.get(getRndIntInRange(0, directionsList.size() - 1)).asText() + " " +
                            locNode.findValue("daleko").findValues("text")
                                    .get(getRndIntInRange(0, dalekoPlaces.size() - 1)).asText();
                    break;
            }
            locationBuilder.append(locNode.findValue("loc_name").asText()).append(", ").append(place).append(". ");
        }
        else if (blizkoPlaces.size() == 1 || dalekoPlaces.size() == 1) {
            place = locNode.findValue("blizko").findValues("text")
                    .get(getRndIntInRange(0, blizkoPlaces.size() - 1)).asText();
            locationBuilder.append(locNode.findValue("loc_name").asText()).append(", ").append(place).append(". ");
        }
        else {
            locationBuilder.append(locNode.findValue("loc_name").asText()).append(". ");
        }

        return locationBuilder;
    }



    private StringBuilder genHelpNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        int newsNumb = getRndIntInRange(1,2); //определяем зовут ли на помощь: 1 - зовут, 2 - не зовут
        if (newsNumb == 1) {
            List<JsonNode> phrasesList;
            switch (faction) {
                case "Одиночки":
                    phrasesList = node.findValue("SOS").findValue("loner").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Монолит":
                    phrasesList = node.findValue("SOS").findValue("monolith").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Бандиты":
                    phrasesList = node.findValue("SOS").findValue("bandit").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Ренегаты":
                    phrasesList = node.findValue("SOS").findValue("bandit").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Долг":
                    phrasesList = node.findValue("SOS").findValue("dolg").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Учёные":
                    phrasesList = node.findValue("SOS").findValue("ecolog").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Чистое Небо":
                    phrasesList = node.findValue("SOS").findValue("clear_sky").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Военные":
                    phrasesList = node.findValue("SOS").findValue("army").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Наёмники":
                    phrasesList = node.findValue("SOS").findValue("mercenary").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
                case "Свобода":
                    phrasesList = node.findValue("SOS").findValue("freedom").findValues("text");
                    newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
                    break;
            }
        }

        List<JsonNode> mutantsList = node.findValue("mutants").findValues("text");
        newsBuilder.append(mutantsList.get(getRndIntInRange(0, mutantsList.size() - 1)).asText());
        newsBuilder.append(". ");
        newsBuilder.append(genLocation(node).toString());

        return newsBuilder;
    }

    private StringBuilder genSpecialNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        if (faction.equals("Зомбированные")) {
            List<JsonNode> phrasesList = node.findValue("zombie_news").findValue("dumb_zombies").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
        }
        else {
            List<JsonNode> phrasesList = node.findValue("special_news").findValues("text");
            newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
        }
        return newsBuilder;
    }

    private StringBuilder genSurgeNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        int newsNumb = getRndIntInRange(1, 2); //1 - случайная новость, 2 - составление новости фракции из кусков
        if (newsNumb == 1) {
            List<JsonNode> templatesList = node.findValue("surge_template").findValues("text");
            newsBuilder.append(templatesList.get(getRndIntInRange(0, templatesList.size() - 1)).asText()).append(" ");
        }
        else {
            List<JsonNode> startsList;
            List<JsonNode> midsList;
            List<JsonNode> endsList;
            switch (faction) {
                case "Одиночки":
                    startsList = node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("loner")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Монолит":
                    startsList = node.findValue("surge_builder_by_faction").findValue("monolith")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("monolith")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("monolith")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Бандиты":
                    startsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Ренегаты":
                    startsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("bandit")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Долг":
                    startsList = node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("dolg")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Учёные":
                    startsList = node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("scientist")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Чистое Небо":
                    startsList = node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("clear_sky")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Военные":
                    startsList = node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("army")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Наёмники":
                    startsList = node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("mercenary")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
                case "Свобода":
                    startsList = node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("start").findValues("text");
                    midsList = node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("mid").findValues("text");
                    endsList = node.findValue("surge_builder_by_faction").findValue("freedom")
                            .findValue("end").findValues("text");

                    newsBuilder.append(startsList.get(getRndIntInRange(0, startsList.size() - 1)).asText()).append(" ");
                    newsBuilder.append(midsList.get(getRndIntInRange(0, midsList.size() - 1)).asText()).append(". ");
                    newsBuilder.append(endsList.get(getRndIntInRange(0, endsList.size() - 1)).asText()).append(" ");
                    break;
            }
        }
        return newsBuilder;
    }


    private StringBuilder genStartOfJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (faction.equals("Бандиты") || faction.equals("Ренегаты")) {
            faction = "Бандиты";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Свобода")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Долг")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Военные")) {
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные"
            faction = "Одиночки";
            List<JsonNode> list = node.findValue("faction_jokes").findValue("joke_start").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }

        return builder;
    }
    private StringBuilder genResponseToJoke(JsonNode node) {
        StringBuilder builder = new StringBuilder();

        if (faction.equals("Бандиты") || faction.equals("Ренегаты")) {
            faction = "Бандиты";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else if (faction.equals("Свобода")) {
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        else { //"Одиночки", "Монолит", "Чистое Небо", "Наёмники", "Учёные", "Долг", "Военные"
            faction = "Одиночки";
            List<JsonNode> list = node.findValue("responses_jokes").findValue(faction).findValues("text");
            builder.append(list.get(getRndIntInRange(0, list.size() - 1)).asText());
        }
        return builder;
    }

    private StringBuilder genNearbyEnemyActivityNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        List<JsonNode> phrasesList = node.findValue("nearby_enemy_activity_news").findValue("messages").findValues("text");
        newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
        newsBuilder.append(genLocation(node));
        return newsBuilder;
    }
    private StringBuilder genNearbyEnemyActivityResponse(JsonNode node) {
        StringBuilder responseBuilder = new StringBuilder();
        List<JsonNode> phrasesList = node.findValue("nearby_enemy_activity_news").findValue("responses").findValues("text");
        responseBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText()).append(" ");
        return responseBuilder;
    }

    private StringBuilder genSystemKilledResponse(JsonNode node) {
        StringBuilder responseBuilder = new StringBuilder();

        List<JsonNode> phrasesList = node.findValue("reports_and_responses").findValue("responses").
                                            findValue("response_death_by_stalker").findValues("text");
        responseBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        return responseBuilder;
    }

    private StringBuilder genTradersAndGoodsNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();

        List<JsonNode> phrasesList = node.findValue("reports_and_responses").findValue("reports").
                                            findValue("buy gossip").findValues("text");
        newsBuilder.append(phrasesList.get(getRndIntInRange(0, phrasesList.size() - 1)).asText());
        return newsBuilder;
    }

    private StringBuilder genConductNews(JsonNode node) {
        StringBuilder newsBuilder = new StringBuilder();
        List<JsonNode> conductLocsList = node.findValue("utilities").findValue("level_description").findValues("loc_name_for_conduct");
        newsBuilder.append("Ищется опытный сталкер, который сможет провести в указанное место: ")
                    .append(conductLocsList.get(getRndIntInRange(0, conductLocsList.size() - 1)).asText()).append("\n")
                    .append("Моё текущее местоположение: ").append(genLocation(node));

        return newsBuilder;
    }

    public StringBuilder genMemeNews() {
        StringBuilder newsBuilder = new StringBuilder();
        if (Main.urlsList.size() == 0) {
            newsBuilder.append(Resources.getNoMemePhrase());
            newsType = 0;
        }
        else newsBuilder.append(Main.urlsList.get(getRndIntInRange(0, Main.urlsList.size())));
        return newsBuilder;
    }

    private String replaceTemplates(StringBuilder newsBuilder, JsonNode node) {
        String strNews = newsBuilder.toString();

        if (strNews.contains("$who")) {
            List<JsonNode> mutantsList = node.findValue("who_mutant").findValues("text");
            String replacement = mutantsList.get(getRndIntInRange(0, mutantsList.size() - 1)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$who", replacement);
        }

        if (strNews.contains("$surge") && newsBuilder.toString().contains("$when")) {
            List<JsonNode> surgesList = node.findValue("surge_type").findValues("text");
            String replacement = surgesList.get(getRndIntInRange(0, surgesList.size() - 1)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$surge", replacement);


            //определяем текущее время
            //date.toString() -> Sat Aug 22 21:52:58 MSK 2020
            //date.toString().split(" ")[3] -> 21:52:58
            //date.toString().split(" ")[3].split(":")[0] -> 21
            //получаем текущий час (24-часовой формат времени)
            Date date = new Date();
            int hoursNow = Integer.parseInt(date.toString().split(" ")[3].split(":")[0]);
            if (hoursNow >= 0 && hoursNow <= 5) { //сейчас ночь, выброс - утром
                replacement = "утром";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 6 && hoursNow <= 11) { //сейчас утро, выброс - днём
                replacement = "днём";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 12 && hoursNow <= 14) { //сейчас день, выброс - после обеда
                replacement = "после обеда";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 15 && hoursNow <= 17) { //сейчас после обеда, выброс - вечером
                replacement = "вечером";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else { //hoursNow >= 18 && hoursNow <= 23      сейчас вечер, выброс - ночью
                replacement = "ночью";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
        }
        else if (strNews.contains("$when")) {
            String replacement = "";
            //определяем текущее время
            //определяем текущее время
            //date.toString() -> Sat Aug 22 21:52:58 MSK 2020
            //date.toString().split(" ")[3] -> 21:52:58
            //date.toString().split(" ")[3].split(":")[0] -> 21
            //получаем текущий час (24-часовой формат времени)
            Date date = new Date();
            int hoursNow = Integer.parseInt(date.toString().split(" ")[3].split(":")[0]);
            if (hoursNow >= 0 && hoursNow <= 5) { //сейчас ночь, выброс - утром
                replacement = "утром";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 6 && hoursNow <= 11) { //сейчас утро, выброс - днём
                replacement = "днём";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 12 && hoursNow <= 14) { //сейчас день, выброс - после обеда
                replacement = "после обеда";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else if (hoursNow >= 15 && hoursNow <= 17) { //сейчас после обеда, выброс - вечером
                replacement = "вечером";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
            else { //hoursNow >= 18 && hoursNow <= 23      сейчас вечер, выброс - ночью
                replacement = "ночью";
                strNews = strNews.replaceAll("\\$when", replacement);
            }
        }

        if (strNews.contains("$artefact")) {
            List<String> artefactsList = Resources.getArtefactsList();
            String replacement = artefactsList.get(getRndIntInRange(0, artefactsList.size() - 1));
            strNews = newsBuilder.toString().replaceAll("\\$artefact", replacement);
        }

        if (strNews.contains("$stalker")) {
            List<JsonNode> stalkersList = node.findValue("utilities").findValue("stalker").findValues("text");
            String replacement = stalkersList.get(getRndIntInRange(0, stalkersList.size() - 1)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$stalker", replacement);
        }

        if (strNews.contains("$mutant")) {
            List<JsonNode> mutantsList = node.findValue("utilities").findValue("mutant").findValues("text");
            String replacement = mutantsList.get(getRndIntInRange(0, mutantsList.size() - 1)).asText();
            strNews = newsBuilder.toString().replaceAll("\\$mutant", replacement);
        }

        if (strNews.contains("$goods") && strNews.contains("$trader")) {
            List<JsonNode> tradersList = node.findValue("traders").findValue("traders_names").findValues("text");
            String trader = tradersList.get(getRndIntInRange(0, tradersList.size() - 1)).asText();
            List<JsonNode> goodsList = node.findValue("traders").findValue("traders_goods").findValue(trader).findValues("text");
            String goods = goodsList.get(getRndIntInRange(0, goodsList.size() - 1)).asText();
            strNews = strNews.replaceAll("\\$trader", trader);
            strNews = strNews.replaceAll("\\$goods", goods);
        }
        else if (strNews.contains("$goods") && !strNews.contains("$trader")) {
            List<JsonNode> goodsList = node.findValue("traders").findValue("traders_goods").findValues("text");
            String goods = goodsList.get(getRndIntInRange(0, goodsList.size() - 1)).asText();
            strNews = strNews.replaceAll("\\$goods", goods);
        }
        else if (!strNews.contains("$goods") && strNews.contains("$trader")) {
            List<JsonNode> tradersList = node.findValue("traders").findValue("traders_names").findValues("text");
            String trader = tradersList.get(getRndIntInRange(0, tradersList.size() - 1)).asText();
            strNews = strNews.replaceAll("\\$trader", trader);
        }
        return strNews;
    }

    //Генерация ответов на новости
    //  1 - реакция на зомби
    //  2 - реакция на анекдот
    //  3 - реакция на врага рядом
    //  4 - реакция на некролог
    public String generateResponse(double responseType, MessageReceivedEvent event) {
        StringBuilder responseBuilder = new StringBuilder();
        StringBuilder nameBuilder = genName();
        if (faction.equals("Зомбированные")) {
            while (faction.equals("Зомбированные")) {
                nameBuilder = genName();
            }
        }
        responseBuilder.append(nameBuilder).append(":\n");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(new File("./input/dynamic_news.json"), JsonNode.class);

            if (responseType == 1) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Main.mapOfThreads.get(event.getChannel().getName()).interrupt();
                }
                List<JsonNode> responsesList = node.findValue("response_dumb_zombies").findValues("text");
                responseBuilder.append(responsesList.get(getRndIntInRange(0, responsesList.size() - 1)).asText()).append(" ");
            }
            if (responseType == 2) {
                try {
                    Thread.sleep(getRndIntInRange(1000, 5 * 1000));
                } catch (InterruptedException e) {
                    Main.mapOfThreads.get(event.getChannel().getName()).interrupt();
                }
                responseBuilder.append(genResponseToJoke(node));
            }
            if (responseType == 3) {
                try {
                    Thread.sleep(getRndIntInRange(3 * 1000, 5 * 1000));
                } catch (InterruptedException e) {
                    Main.mapOfThreads.get(event.getChannel().getName()).interrupt();
                }
                responseBuilder.append(genNearbyEnemyActivityResponse(node));
            }
            if (responseType == 4) {
                try {
                    Thread.sleep(getRndIntInRange(3 * 1000, 5 * 1000));
                } catch (InterruptedException e) {
                    Main.mapOfThreads.get(event.getChannel().getName()).interrupt();
                }
                responseBuilder.append(genSystemKilledResponse(node));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }



    public int getNewsType() {
        return newsType;
    }
    public String getFaction() {
        return faction;
    }
    public int getRndIntInRange(int min, int max){
        return (int) (Math.random()*((max-min)+1))+min;
    }

}
