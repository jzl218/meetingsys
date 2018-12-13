package api.utils;

import net.sf.json.JSONObject;

public class JsonUtils {
    public static String getAccount(String jwt){
        String newUser=jwt.substring(4,jwt.length());
        JSONObject jsonobject = JSONObject.fromObject(newUser);
        String email=jsonobject.getString("sub");
        return email;
    }




}
