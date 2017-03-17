package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
                    JSONObject event = response.get(i);
                    // Get event type
                    String type = event.getString("type");
                    JSONObject payload = event.getJSONObject("payload");
                    JSONArray commit= payload.getJSONArray("commits");
                    String commitHash;
                    String commitMessage;
                    if(!commit.isNull(0)) {
                        JSONObject commitFields = commit.getJSONObject(0);
                        commitHash = commitFields.getString("sha");
                        commitHash = commitHash.substring(0, Math.min(commitHash.length(), 6));
                        commitMessage = commitFields.getString("message");
                    }
                    else{
                        commitHash = "No Sha";
                        commitMessage = "No message";
                    }
                    // Get created_at date, and format it in a more pleasant style
                    String creationDate = event.getString("created_at");
                    SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = inFormat.parse(creationDate);
                    String formatted = outFormat.format(date);

                    // Add type of event as header
                    sb.append("<h3 class=\"type\">");
                    sb.append(i + 1 + ". " + type);
                    sb.append("</h3>");
                    sb.append("<h3 class=\"hash\">");
                    sb.append( commitHash);
                    sb.append("</h3>");
                    sb.append("<h3 class=\"message\">");
                    sb.append(commitMessage);
                    sb.append("</h3>");

                    // Add formatted date
                    sb.append(" on ");
                    sb.append(formatted);
                    sb.append("<br />");
                    // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
                    sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
                    sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
                    sb.append(event.toString());
                    sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        int pageNumber = 1;
        boolean isNull = false;

        while (eventList.size() != 10 && !isNull){
            String url = BASE_URL + user + "/events?page=" + pageNumber;
            //System.out.println(url);
            JSONObject json = Util.queryAPI(new URL(url));
            //System.out.println(json);
            JSONArray events = json.getJSONArray("root");
        //for (int j = 0; j < events.length(); j++){
                for (int i = 0; i < events.length() && eventList.size() < 10; i++) {
                    if(!events.isNull(i)){
                        JSONObject fields = events.getJSONObject(i);
                        String fieldType = fields.getString("type");

                        if(fieldType.equals("PushEvent")){
                            eventList.add(events.getJSONObject(i));
                           // String commits = fields.getString("commits");
                        }
                    }
                }
            pageNumber++;
            String urlTest = BASE_URL + user + "/events?page=" + pageNumber;
            //System.out.println(url);
            JSONObject json2 = Util.queryAPI(new URL(urlTest));
            //System.out.println(json);
            JSONArray events2 = json.getJSONArray("root");
            if(events2.isNull(0)){
                isNull = true;
            }
        }
        //S}
        return eventList;
    }
}