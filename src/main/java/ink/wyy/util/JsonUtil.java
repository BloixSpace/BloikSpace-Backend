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
        return stringToMap(reqStr);
    }

    public static HashMap<String, String> stringToMap(String reqStr) throws IOException {
        Gson gson = new Gson();
//        System.out.println(reqStr);
        HashMap res = null;
        try {
            res = gson.fromJson(reqStr, HashMap.class);
            for (Object key : res.keySet()) {
                if (res.get(key) instanceof Number) {
                    res.put(key, String.valueOf(((Number) res.get(key)).intValue()));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return res;
    }
}
