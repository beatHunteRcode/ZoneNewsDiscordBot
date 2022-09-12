package Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public BlockingQueue<AudioTrack> tracksQueue;
    public final short trackPageSize = 10;

    private boolean isLoop = false;

    public TrackScheduler(AudioPlayer player) {
        this.audioPlayer = player;
        this.tracksQueue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!this.audioPlayer.startTrack(track, true)) {
            this.tracksQueue.offer(track);
        }
    }


    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        player.setPaused(false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
        player.startTrack(track, true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (isLoop) {
                if (player.getPlayingTrack() != null) queue(player.getPlayingTrack().makeClone());
                else queue(track);
            }
            nextTrack();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The audioPlayer was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    private void nextTrack() {
        this.audioPlayer.startTrack(this.tracksQueue.poll(), false);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        if (isLoop) {
            if (player.getPlayingTrack() != null) queue(player.getPlayingTrack().makeClone());
            else queue(track);
        }
        nextTrack();
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }
}