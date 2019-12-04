package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.allinonesocialapp.android.R;
import com.allinonesocialapp.android.view.WebViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import model.SocialApp;
import util.CommonUtil;

public class SocialAppsAdapter extends RecyclerView.Adapter<SocialAppsAdapter.SocialAppViewHolder> {

    List<SocialApp> appList = new ArrayList<>();

    List<SocialApp> lessList = null;

    List<SocialApp> moreList = null;

    int totalUseCount = 0;

    Context context = null;

    public SocialAppsAdapter(List<SocialApp> appList, Context context) {
        this.context = context;

        if(appList.size() > 12){
            this.lessList = getItems(appList, 11);
            this.appList.clear();

            // Add load more button
            SocialApp loadMore = new SocialApp();
            loadMore.displayName = "More";
            loadMore.type = "MORE";
            loadMore.imageDrawable = context.getDrawable(R.drawable.more);
            this.lessList.add(loadMore);

            this.appList.addAll(this.lessList);

            this.moreList = appList;

            // Add show less button
            SocialApp loadLess = new SocialApp();
            loadLess.displayName = "Less";
            loadLess.type = "LESS";
            loadLess.imageDrawable = context.getDrawable(R.drawable.less);
            this.moreList.add(loadLess);

        }else{
            this.appList = appList;
        }

        for(SocialApp app : appList){
            totalUseCount += app.useCount;
        }


    }

    public List<SocialApp> getItems(List<SocialApp> list, int n){

        List<SocialApp> arrayList = new ArrayList<>();

        for(int i=0; i < n ; i++){
            arrayList.add(list.get(i));
        }

        return arrayList;
    }

    @NonNull
    @Override
    public SocialAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social_app, parent, false);

        return new SocialAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialAppViewHolder holder, int position) {

        final SocialApp socialApp = appList.get(position);

        holder.iconDisplayName.setText(socialApp.displayName);

        if(socialApp.imageUrl != null)
            CommonUtil.loadImageWithGlide(socialApp.imageUrl, context, holder.iconImage);
        else if(socialApp.imageDrawable != null){
            holder.iconImage.setImageDrawable(socialApp.imageDrawable);
        }else{
            holder.iconImage.setImageResource(0);
        }
            //holder.iconImage.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.fb, null));

        if(totalUseCount > 0 && socialApp.type == null){
            double percent = ((socialApp.useCount * 100.0))/totalUseCount;

            holder.useCount.setText(socialApp.useCount + "X (" + (int) Math.round(percent) + "%)");

            StringJoiner joiner = new StringJoiner(" ");

            if(socialApp.duration != 0){

                int noOfSec = (int)socialApp.duration/1000;
                int mins = noOfSec/60;
                int hours = mins/60;
                int remaingSec = noOfSec%60;
                int remaingMin = mins%60;
                int remaingHour = hours%24;

                if(hours != 0){
                    if(hours == 1){
                        joiner.add("1 hour");
                    }else{
                        joiner.add( hours + " hours");
                    }
                }

                if(remaingMin != 0){
                    if(remaingMin == 1){
                        joiner.add("1 min");
                    }else{
                        joiner.add( remaingMin + " mins");
                    }
                }

                if(remaingSec != 0){
                    if(remaingSec == 1){
                        joiner.add("1 sec");
                    }else{
                        joiner.add( remaingSec + " secs");
                    }
                }

                holder.useDuration.setText(joiner.toString());
                holder.useDuration.setVisibility(View.VISIBLE);

            }else{
                holder.useDuration.setVisibility(View.GONE);
            }

        }else{
            holder.useCount.setVisibility(View.GONE);
            holder.useDuration.setVisibility(View.GONE);
        }



        holder.appItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(socialApp.type != null){

                    if(socialApp.type.equals("LESS")){
                        appList = lessList;
//                        appList.clear();
//                        appList.addAll(lessList);
                    }else if(socialApp.type.equals("MORE")){
//                        appList.clear();
//                        appList.addAll(moreList);
                        appList = moreList;
                    }
                    notifyDataSetChanged();
                }else{
                    if (socialApp.isInstalled){
                        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(socialApp.packageName);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(launchIntent);
                    }else{
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("webUrl", socialApp.webUrl);
                        intent.putExtra("webName", socialApp.displayName);
                        context.startActivity(intent);
                    }

                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class SocialAppViewHolder extends RecyclerView.ViewHolder {
        TextView iconDisplayName;
        ImageView iconImage;
        LinearLayout appItem;
        TextView useCount;
        TextView useDuration;
        public SocialAppViewHolder(View itemView) {
            super(itemView);
            iconDisplayName = itemView.findViewById(R.id.app_display_name);
            iconImage = itemView.findViewById(R.id.app_icon);
            appItem = itemView.findViewById(R.id.app_item);
            useCount = itemView.findViewById(R.id.use_count);
            useDuration = itemView.findViewById(R.id.use_duration);
        }
    }
}
