package tiengduc123.com.derdiedas.internet;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by Dell on 1/2/2016.
 */
public class qInternet {

    public void qInternet() {
    }


    public String readFileFromInternet(String word){
        URLConnection feedUrl;
        String str = "a";

        Random rd = new Random();
        int n1 = rd.nextInt();
        int m1 = rd.nextInt();

        // insert tu dong vao db
        String url = "http://tiengduc123.com/app/Translate.php?m=" + m1 + "&n=" + n1 + "&word=" + word;

        try {
            feedUrl = new URL(url).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();
            str = sb.toString();

        } catch(Exception e) {
            Log.e("ERROR", "readFileFromInternet: " + e );
        }
        return str;
    }
}
