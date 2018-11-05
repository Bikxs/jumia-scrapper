package home.bikxs.jumiascrapper.models;

import java.util.ArrayList;
import java.util.List;

public class Notification {
    class Attachment {
        public String title;
        public String text;

        public Attachment(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }

    public Notification(String text, String attachementTitle, String attachementText) {
        this.text = text;
        this.attachments = new ArrayList<>();
        this.attachments.add(new Attachment(attachementTitle,attachementText));
    }
    public String text;
    public List<Attachment> attachments = null;

}