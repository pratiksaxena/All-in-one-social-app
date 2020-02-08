package messenger.people.messenger.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.calldorado.Calldorado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import messenger.people.messenger.R;

public class CalldoRadoActivity extends AppCompatActivity {

    private int permissionRequestCode = 356;

    private boolean callPhoneGranted = false;
    private boolean phoneStateGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calldo_rado);

        TextView getStarted = findViewById(R.id.getStartedTV);

        TextView bottomText = findViewById(R.id.bottomNav);

        SpannableString ss = new SpannableString("By clicking \'Get Started\' you agree to the Caller ID End User License Aggrement and Privacy Policy");
        ss.setSpan(new myClickableSpan(1),53, 79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new myClickableSpan(2),84, 98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        bottomText.setText(ss);
        bottomText.setMovementMethod(LinkMovementMethod.getInstance());
        bottomText.setHighlightColor(Color.TRANSPARENT);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Calldorado.Condition, Boolean> conditionsMap = new HashMap<>();
                conditionsMap.put(Calldorado.Condition.EULA, true);
                conditionsMap.put(Calldorado.Condition.PRIVACY_POLICY, true);
                Calldorado.acceptConditions(getBaseContext(), conditionsMap);
                onBoardingSuccess();
            }
        });

    }


    private void onBoardingSuccess() {
        Calldorado.startCalldorado(this);
        requestCdoPermissions();
    }


    private void requestCdoPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.READ_PHONE_STATE);
        permissionList.add(Manifest.permission.CALL_PHONE);
        permissionList.add(Manifest.permission.READ_CONTACTS);
        permissionList.add(Manifest.permission.READ_CALL_LOG);
        permissionList.add(Manifest.permission.ANSWER_PHONE_CALLS);
        permissionList.add(Manifest.permission.WRITE_CONTACTS);
        permissionList.add(Manifest.permission.READ_CONTACTS);
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionRequestCode){

            int index = 0;

            for(String permission : permissions){

                if(permission.equals(Manifest.permission.CALL_PHONE) && grantResults[index] == PackageManager.PERMISSION_GRANTED){
                    callPhoneGranted = true;
                }

                if(permission.equals(Manifest.permission.READ_PHONE_STATE) && grantResults[index] == PackageManager.PERMISSION_GRANTED){
                    phoneStateGranted = true;
                }

                index++;
            }

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }

    public class myClickableSpan extends ClickableSpan {

        int pos;
        public myClickableSpan(int position){
            this.pos=position;
        }

        @Override
        public void onClick(View widget) {
                Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webUrl", "https://legal.calldorado.com/usage-and-privacy-terms/");
                intent.putExtra("webName", "Caller ID - EULA");
                startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.white));
            ds.setUnderlineText(true);
        }

    }

}

