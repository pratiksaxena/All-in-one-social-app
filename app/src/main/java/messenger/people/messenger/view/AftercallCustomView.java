package messenger.people.messenger.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.calldorado.android.ui.views.custom.CalldoradoCustomView;

import messenger.people.messenger.R;

public class AftercallCustomView extends CalldoradoCustomView {

    private Context context;
    private LinearLayout ll;

    public AftercallCustomView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View getRootView() {
        Log.d(TAG, "onCreateView() 1");
        ll = (LinearLayout) inflate(context, R.layout.aftercall_native_layout,
                getLinearViewGroup());
        ll.findViewById(R.id.aftercall_fb).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://m.facebook.com/home.php");
                intent.putExtra("webName", "Facebook");
                context.startActivity(intent);

            }
        });

        ll.findViewById(R.id.aftercall_messenger).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://m.facebook.com/home.php");
                intent.putExtra("webName", "Facebook");
                context.startActivity(intent);
            }
        });

        ll.findViewById(R.id.aftercall_instagram).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://www.instagram.com/");
                intent.putExtra("webName", "Instagram");
                context.startActivity(intent);
            }
        });

        ll.findViewById(R.id.aftercall_whatsapp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(launchIntent);
            }
        });
        return ll;
    }
}
