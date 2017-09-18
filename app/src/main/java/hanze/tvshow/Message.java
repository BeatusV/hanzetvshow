package hanze.tvshow;

/**
 * Created by Gebruiker on 18-9-2017.
 */

public class Message {
    private long id;
    private String name;
    private String imageUrl;
    private long score;


    public Message(long id, String name, String imageUrl, long score) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
