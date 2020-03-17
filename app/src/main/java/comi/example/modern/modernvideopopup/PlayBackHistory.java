package comi.example.modern.modernvideopopup;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class PlayBackHistory {
    String isFinished,lastPosition,LastPlaybackTime;

    public PlayBackHistory(String isFinished, String lastPosition, String lastPlaybackTime) {
        this.isFinished = isFinished;
        this.lastPosition = lastPosition;
        LastPlaybackTime = lastPlaybackTime;
    }

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public String getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(String lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getLastPlaybackTime() {
        return LastPlaybackTime;
    }

    public void setLastPlaybackTime(String lastPlaybackTime) {
        LastPlaybackTime = lastPlaybackTime;
    }
}
