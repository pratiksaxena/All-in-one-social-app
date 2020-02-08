package messenger.people.messenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.StringJoiner;

import messenger.people.messenger.R;
import messenger.people.messenger.model.SocialApp;

public class UsageAppAdapter extends RecyclerView.Adapter<UsageAppAdapter.UsageAppViewHolder> {

    List<SocialApp> appList = null;

    public UsageAppAdapter(List<SocialApp> appList) {
        this.appList = appList;
    }

    @NonNull
    @Override
    public UsageAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usgae_stats_item, parent, false);

        return new UsageAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageAppViewHolder holder, int position) {

        final SocialApp socialApp = appList.get(position);

        holder.iconDisplayName.setText(socialApp.displayName);

        holder.iconImage.setImageDrawable(socialApp.imageDrawable);

        holder.useCount.setText(socialApp.useCount + " times");

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

        holder.usageDuration.setText(joiner.toString());

        }

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class UsageAppViewHolder extends RecyclerView.ViewHolder {

        TextView iconDisplayName;
        TextView useCount;
        ImageView iconImage;
        TextView usageDuration;

        public UsageAppViewHolder(View itemView) {
            super(itemView);
            iconDisplayName = itemView.findViewById(R.id.usage_app_name);
            iconImage = itemView.findViewById(R.id.usage_icon);
            useCount = itemView.findViewById(R.id.usage_count);
            usageDuration = itemView.findViewById(R.id.usage_duration);
        }
    }

}
