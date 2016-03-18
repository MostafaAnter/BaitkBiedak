package mostafa_anter.baitkbiedak.models;

/**
 * Created by mostafa on 11/03/16.
 */
public class FeedPOJO {
    private String  title,
                    timeStamp,
                    content,
                    linkAttachedWithContent,
                    imageUrl;
    private boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkAttachedWithContent() {
        return linkAttachedWithContent;
    }

    public void setLinkAttachedWithContent(String linkAttachedWithContent) {
        this.linkAttachedWithContent = linkAttachedWithContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
