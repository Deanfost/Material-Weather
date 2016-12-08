package dean.weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DeanF on 10/14/2016.
 */

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    private List<String> daySet;
    private List<String> conditionSet;
    private List<Integer> HISet;
    private List<Integer> LOSet;
    private List<Integer> precipSet;
    private Context context;
    Typeface robotoLight;
    //Set duration for fade anim
    private final int FADE_DURATION = 500;
    //Animate only first 5 items
    int animCount = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Make references to each view in the row
        public TextView dayView;
        public ImageView condIcon;
        public TextView precipView;
        public TextView HILOView;

        //Create public constructor that accepts entire row and finds each subview
        public ViewHolder(View itemView){
            super(itemView);
            dayView = (TextView) itemView.findViewById(R.id.dailyListItem1);
            HILOView = (TextView) itemView.findViewById(R.id.dailyListItem2);
            precipView = (TextView) itemView.findViewById(R.id.dailyListItem3);
            condIcon = (ImageView) itemView.findViewById(R.id.dailyListItem4);
        }
    }

    //Pass pulled data from API
    public DailyAdapter(Context pulledContext, List<String> pulledDays, List<String> pulledConditions, List<Integer> pulledHI, List<Integer> pulledLO, List<Integer> pulledPrecip){
        context = pulledContext;
        daySet = pulledDays;
        conditionSet = pulledConditions;
        HISet = pulledHI;
        LOSet = pulledLO;
        precipSet = pulledPrecip;
    }

    //Inflate the layout and return the holder
    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //Inflate the layout
        View rowView = layoutInflater.inflate(R.layout.daily_list_item, parent, false);

        //Return a new holder instance
        DailyAdapter.ViewHolder viewHolder = new DailyAdapter.ViewHolder(rowView);
        return viewHolder;
    }

    //Populate the data into the item through the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Setup typeface
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on position
        String pulledDay = daySet.get(position);
        String pulledCond = conditionSet.get(position);
        Log.i("pulledCondAdapter", pulledCond);
        int pulledHI = HISet.get(position);
        int pulledLO = LOSet.get(position);
        int pulledPrecip = precipSet.get(position);

        //Set item views to pulled data values
        TextView dayView = holder.dayView;
        dayView.setText(pulledDay);
        TextView HILOView = holder.HILOView;
        HILOView.setText(String.valueOf(pulledHI) + (char) 0x00B0 + "/" + String.valueOf(pulledLO)+ (char) 0x00B0);
        TextView precipView = holder.precipView;
        precipView.setText(String.valueOf(pulledPrecip)+ "%");
        ImageView condIcon = holder.condIcon;
        //Determine which condition and icon to set
        switch (pulledCond){
            case "clear-day":
                condIcon.setImageResource(R.drawable.ic_sunny_white);
                break;
            case "clear-night":
                condIcon.setImageResource(R.drawable.ic_clear_night_white);
                break;
            case "rain":
                condIcon.setImageResource(R.drawable.ic_rain_white);
                break;
            case "snow":
                condIcon.setImageResource(R.drawable.ic_snow_white);
                break;
            case "sleet":
                condIcon.setImageResource(R.drawable.ic_sleet_white);
                break;
            case "wind":
                condIcon.setImageResource(R.drawable.ic_windrose_white);
                break;
            case "fog":
                if(MainActivity.setID != 3){
                    condIcon.setImageResource(R.drawable.ic_foggyday_white);
                }
                else{
                    condIcon.setImageResource(R.drawable.ic_foggynight_white);
                }
                break;
            case "cloudy":
                condIcon.setImageResource(R.drawable.ic_cloudy_white);
                break;
            case "partly-cloudy-day":
                condIcon.setImageResource(R.drawable.ic_partlycloudy_white);
                break;
            case "partly-cloudy-night":
                condIcon.setImageResource(R.drawable.ic_partlycloudynight_white);
                break;
            default:
                condIcon.setImageResource(R.drawable.ic_cloudy_white);
                Log.i("CurrentConditions", "Unsupported condition.");
                break;
        }
        switch (MainActivity.setID){
            case 0:
                condIcon.setColorFilter(getContext().getResources().getColor(R.color.colorYellow));
                break;
            case 1:
                condIcon.setColorFilter(getContext().getResources().getColor(R.color.colorBlue));
                break;
            case 2:
                condIcon.setColorFilter(getContext().getResources().getColor(R.color.colorOrange));
                break;
            case 3:
                condIcon.setColorFilter(getContext().getResources().getColor(R.color.colorPurple));
                break;
        }
        //Customize fonts
        dayView.setTypeface(robotoLight);
        precipView.setTypeface(robotoLight);
        HILOView.setTypeface(robotoLight);

        //Set the background of the item
        int setID = MainActivity.setID;
        switch (setID){
            case 0:
                holder.itemView.setBackground(getContext().getResources().getDrawable(R.drawable.background_ripple_yellow));
                break;
            case 1:
                holder.itemView.setBackground(getContext().getResources().getDrawable(R.drawable.background_ripple_blue));
                break;
            case 2:
                holder.itemView.setBackground(getContext().getResources().getDrawable(R.drawable.background_ripple_orange));
                break;
            case 3:
                holder.itemView.setBackground(getContext().getResources().getDrawable(R.drawable.background_ripple_purple));
                break;
        }

        if(animCount < 5) {
            //Set animations
            setFadeAnimation(dayView);
            setFadeAnimation(HILOView);
            setFadeAnimation(precipView);
            setFadeAnimation(condIcon);
            animCount++;
        }

        //Setup OnClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("dailyRecycler", "Clicked" + daySet.get(position));

            }
        });
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setStartOffset(300);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return daySet.size();
    }

    //Provide easy access to context object in the recyclerview
    private Context getContext(){
        return context;
    }
}
