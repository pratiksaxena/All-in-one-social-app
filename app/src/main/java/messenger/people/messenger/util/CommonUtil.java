package messenger.people.messenger.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

public class CommonUtil {

    public static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type)  throws JsonSyntaxException {
        return gson.fromJson(json, type);
    }

    public static String getJson(Object obj){
        return gson.toJson(obj);
    }

    public static void loadImageWithGlide(String url, Context context, ImageView imageView){
        Glide.with(context).load(url).into(imageView);
    }

}
