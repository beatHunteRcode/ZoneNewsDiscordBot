import org.junit.Assert;
import org.junit.Test;

public class Tests {

    NewsGenerator newsGenerator = new NewsGenerator();
    private int attempts = 100;

    @Test
    public void genStalkersNicknameTest() {
        for (int i = 0; i < attempts; i++) {
            StringBuilder name = newsGenerator.genName();
            switch (newsGenerator.getFaction()) {
                case "Долг":
                case "Военные":
                    Assert.assertTrue(Resources.getMilitaryRanks().contains(newsGenerator.getName()));
                    Assert.assertTrue(Resources.getMilitarySurnames().contains(newsGenerator.getSurname()));
                    break;
                case "Учёные":
                    Assert.assertTrue(Resources.getScientistNamesList().contains(newsGenerator.getName()));
                    Assert.assertTrue(Resources.getMilitarySurnames().contains(newsGenerator.getSurname()));
                    break;
                default:
                    Assert.assertTrue(Resources.getStalkerNamesList().contains(newsGenerator.getName()));
                    Assert.assertTrue(Resources.getStalkerSurnamesList().contains(newsGenerator.getSurname()));
                    break;
            }
        }
    }

    @Test
    public void genZombieNewsTest() {
        for (int i = 0; i < attempts; i++) {
            newsGenerator.generateNews();
            if (newsGenerator.getFaction().equals("Зомбированные"))
                Assert.assertTrue(newsGenerator.getNewsType() == 2 || newsGenerator.getNewsType() == 7);
        }
    }

    @Test
    public void genTraderNameTest() {
        for (int i = 0; i < attempts; i++) {
            StringBuilder name = newsGenerator.genTraderName();
            Assert.assertEquals(Resources.getTradersFactionsMap().get(newsGenerator.getName()),newsGenerator.getFaction());
        }
    }

    @Test
    public void noMemesExceptionTest() {
        for (int i = 0; i < attempts; i++) {
            String news = newsGenerator.generateNews();
            if (newsGenerator.getNewsType() == 13) {
                Assert.assertEquals(Resources.getNoMemePhrase(), news.split(":")[1]);
            }
        }
    }

    @Test
    public void generateNewsTest() {
        for (int i = 0; i < attempts; i++) {
            Assert.assertNotNull(newsGenerator.generateNews());
        }
    }
}
