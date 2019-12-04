package model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class SocialApp implements Serializable {

    public String imageUrl;
    public Drawable imageDrawable;
    public String displayName;
    public String webUrl;
    public String packageName;
    public int useCount;
    public long duration;
    public boolean isAppOnly;
    public String type;
    public boolean isInstalled;

    public SocialApp(String imageUrl, Drawable imageDrawable, String displayName, String webUrl, String packageName, boolean isAppOnly) {
        this.imageUrl = imageUrl;
        this.imageDrawable = imageDrawable;
        this.displayName = displayName;
        this.webUrl = webUrl;
        this.packageName = packageName;
        this.isAppOnly = isAppOnly;
    }


    public SocialApp() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this.packageName.equals(((SocialApp)obj).packageName);
    }


}
