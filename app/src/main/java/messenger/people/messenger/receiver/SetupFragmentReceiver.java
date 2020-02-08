package messenger.people.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.calldorado.Calldorado;

import messenger.people.messenger.view.AftercallCustomView;

public class SetupFragmentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Calldorado.setAftercallCustomView(new AftercallCustomView(context));
        }
    }

}
