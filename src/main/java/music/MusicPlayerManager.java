package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlayerManager {

    private static MusicPlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagersMap;
    private final AudioPlayerManager audioPlayerManager;

    public MusicPlayerManager() {
        this.musicManagersMap = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static MusicPlayerManager getINSTANCE() {

        if (INSTANCE == null) INSTANCE = new MusicPlayerManager();

        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagersMap.computeIfAbsent(guild.getIdLong(), (guildId) -> {
           final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
           guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioPlayerSendHandler());
           return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL) {
        final GuildMusicManager guildMusicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.trackScheduler.queue(track);

                textChannel.sendMessage("Добавлено в очередь: **`" + track.getInfo().title + " - " + track.getInfo().author + "`**")
                .queue();

                guildMusicManager.trackScheduler.onTrackStart(audioPlayerManager.createPlayer(), track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> trackList = playlist.getTracks();
                if (!trackList.isEmpty()) {
                    for (AudioTrack track : trackList) {
                        guildMusicManager.trackScheduler.queue(track);
                    }
                    textChannel.sendMessage(
                                    "Добавлено в очередь **" +
                                        Integer.toString(trackList.size()) +
                                        " треков** из плейлиста **" + playlist.getName() + "**")
                                .queue();
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Ничего не найдено.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage("Ошибка загрузки!").queue();
            }
        });
    }
}













