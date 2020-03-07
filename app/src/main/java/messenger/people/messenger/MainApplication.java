package messenger.people.messenger;

import android.app.Application;

import messenger.people.messenger.manager.PreferenceKeeper;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceKeeper.setContext(getApplicationContext());
    }
}
