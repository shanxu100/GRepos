package luluteam.bath.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guan
 * @date Created on 2017/12/29
 */
public class JsonUtil {

    /**
     * 从Http请求中，读取json数据
     *
     * @param request
     * @return
     */
    public static String readJsonFromHttpReq(HttpServletRequest request) {
        StringBuffer json = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return json.toString();
    }

    /**
     * 工具类不允许实例化
     */
    private JsonUtil() {
        throw new RuntimeException();
    }

    /**
     * 对象转JSONObject
     */
    public static JSONObject toJSONObject(Object object) {
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * 从JSONObject中获取ArrayList
     *
     * @param jsonObject 需要获取的JSONObject
     * @param key        需要获取ArrayList的key
     * @param classOfT   ArrayList的元素类型
     */
    public static <T> List<T> getArrayList(JSONObject jsonObject, String key, Class<T> classOfT) {
        List<T> list = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(key);
        for (Object object : jsonArray) {
            list.add(new Gson().fromJson(toJSONString(object), classOfT));
        }
        return list;
    }

    /**
     * 将JSONArray转换为ArrayList
     *
     * @param jsonArray 需要转换的JSONArray
     * @param classOfT  ArrayList的元素类型
     * @return
     */
    public static <T> List<T> parseArrayList(JSONArray jsonArray, Class<T> classOfT) {
        List<T> list = new ArrayList<>();
        for (Object object : jsonArray) {
            list.add(new Gson().fromJson(toJSONString(object), classOfT));
        }
        return list;
    }

    /**
     * JSONObject转JavaBean
     */
    public static <T> T toJavaBean(JSONObject jsonObject, Class<T> classOfT) {
        return new Gson().fromJson(jsonObject.toJSONString(), classOfT);
    }

    /**
     * JSON字符串转JavaBean
     */
    public static <T> T toJavaBean(String jsonString, Class<T> classOfT) {
        return new Gson().fromJson(jsonString, classOfT);
    }
}
