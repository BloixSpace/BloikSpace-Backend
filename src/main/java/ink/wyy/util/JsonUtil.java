package ink.wyy.util;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class JsonUtil {

    public static HashMap<String, String> jsonToMap(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        String reqStr = "";
        String line;
        while ((line = reader.readLine()) != null){
            reqStr = reqStr.concat(line);
        }

        Gson gson = new Gson();
        System.out.println(reqStr);
        HashMap<String, String> res = null;
        try {
            res = gson.fromJson(reqStr, HashMap.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        return res;
    }
}
