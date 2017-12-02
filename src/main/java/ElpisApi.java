import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElpisApi {

    static String URL = "http://www.ipu.ac.in/affiliation_branch.php";

    // a class to bundle the acquired data.
    public static class JSONBundle {

        private String date;
        private String notice;
        private String url;

        public String get_date() {

            return date;
        }

        public void set_date(String _date) {

            this.date = _date;
        }

        public String get_url() {

            return url;
        }

        public void set_url(String _url) {

            this.url = _url;
        }

        public String get_notice() {

            return notice;
        }

        public void set_notice(String _notice) {

            this.notice = _notice;
        }
    }


    // method to fetch url data.
    static String DownloadTask(String urls) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {

            url = new URL(urls);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream  = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while (data != -1) {

                char current = (char) data;
                result += current;
                data = inputStreamReader.read();

            }


        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    // method to extract essential information.
    static void essentialInfo(String essentialData){

        ArrayList<String> linkDates = new ArrayList<>();
        ArrayList<String> linkURLs = new ArrayList<>();
        ArrayList<String> linkNotices = new ArrayList<>();


        ArrayList<JSONBundle> jsonBundles = new ArrayList<>();

        // since the latest notice contains only 10 notices.
        JSONBundle[] jsonBundle = new JSONBundle[10];

        // initializing all the object for use.
        for (int i = 0; i < 10; i++){

            jsonBundle[i] = new JSONBundle();
        }


        // start of the required code from the source code.
        int start = essentialData.indexOf("<h3>");

        // end of the required code from the source code.
        int end = essentialData.indexOf("class=\"item-collapse\">");
        String splitResult = essentialData.substring(start, end);

        // regex to match dates.
        Pattern pattern = Pattern.compile("<td>([0-9]{2}[-|\\/]{1}[0-9]{2}[-|\\/]{1}[0-9]{4})</td>");
        Matcher matcher = pattern.matcher(splitResult);

        while (matcher.find()){

            linkDates.add(matcher.group(1));

        }

        for (int i = 0; i < 10; i++){

            jsonBundle[i].set_date(linkDates.get(i));
        }


        // regex to match link URLs.
        pattern = Pattern.compile("<td><a href=\"(.*?)\">");
        matcher = pattern.matcher(splitResult);

        while (matcher.find()){

            linkURLs.add(URL + matcher.group(1));

        }

        for (int i = 0; i < 10; i++){

            jsonBundle[i].set_url(linkURLs.get(i));
        }



        // regex to match link information.
        pattern = Pattern.compile("\">(.*?)</a>");
        matcher = pattern.matcher(splitResult);

        while (matcher.find()){

            linkNotices.add(matcher.group(1));

        }

        for (int i = 0; i < 10; i++){

            jsonBundle[i].set_notice(linkNotices.get(i));
        }


        // bundling all the acquired data into one ArrayList.
        for (int i = 0; i < 10; i++){

            jsonBundles.add(jsonBundle[i]);
        }



        // to convert the acquired data into JSON array for the API.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonBundles));

    }

    public static void main(String[] args){

        String result = null;

        try{

            result = DownloadTask(URL);

        } catch (Exception e) {

            e.printStackTrace();
        }

        essentialInfo(result);

    }

}
