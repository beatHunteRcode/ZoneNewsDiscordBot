package music;

import java.net.URL;

public class MusicQueriesHandler {

    public static String YOU_MUST_BE_IN_VOICE_CHANNEL_MESSAGE = "Ты должен быть в любом голосовом канале!";

    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
