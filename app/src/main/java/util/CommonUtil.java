package util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

public class CommonUtil {

    public static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String getJson(Object obj){
        return gson.toJson(obj);
    }

    public static void loadImageWithGlide(String url, Context context, ImageView imageView){
        Glide.with(context).load(url).into(imageView);
    }

}
