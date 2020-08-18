import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    public String news;
    private NewsGenerator newsGenerator;

    public MyTimerTask(NewsGenerator newsGenerator) {
        this.newsGenerator = newsGenerator;
    }
    public void run() {
        news = Main.genNews(newsGenerator);
    }

}