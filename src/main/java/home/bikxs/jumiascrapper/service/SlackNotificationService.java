package home.bikxs.jumiascrapper.service;


import home.bikxs.jumiascrapper.models.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Service
public class SlackNotificationService {
    private String errorWebHook;
    private String tradingWebHook;
    private Long errorCount = 0L;
    private boolean allowSlackNotification;

    public SlackNotificationService(@Value("${slack.notifications.enabled}") boolean allowSlackNotification,
                                    @Value("${slack.notifications.error.webhook}") String errorWebHook,
                                    @Value("${slack.notifications.trading.webhook}") String tradingWebHook) {
        this.errorWebHook = errorWebHook;
        this.tradingWebHook = tradingWebHook;
        this.allowSlackNotification = allowSlackNotification;
    }

    public void postStackMessage(String title, String message) {
        if (!allowSlackNotification) {
            return;
        }
        ;
        Notification notification = new Notification(title, message, null);

        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(notification, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(tradingWebHook, HttpMethod.POST, entity, String.class);


    }

    public void postStackError(String message, Throwable ex) {
        System.err.println(message);
        errorCount++;
        if (!allowSlackNotification) {

            ex.printStackTrace();
            return;
        }
        ;
        Notification notification = new Notification(":exclamation:" + ":exclamation:" + ":exclamation:" + message, ex.getMessage(), getStackTrace(ex));

        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

//set your entity to send
        HttpEntity entity = new HttpEntity(notification, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(errorWebHook, HttpMethod.POST, entity, String.class);

    }

    private static String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public Long getErrorsCount() {
        return errorCount;
    }

}
