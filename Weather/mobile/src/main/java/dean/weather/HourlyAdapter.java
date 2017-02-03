package dean.weather;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by DeanF on 10/7/2016.
 */

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    private Context context;
    private List<Integer> comparisonHourSet;
    private List<String> hourSet;
    private List<Integer> tempSet;
    private List<String> conditionSet;
    private List<Integer> windSet;
    Typeface robotoLight;
    //Set duration for fade anim
    private final int FADE_DURATION = 500;
    //Animate only first 5 items
    int animCount = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Make references to each view in the row
        public TextView hourlyHour;
        public TextView hourlyTemp;
        public TextView hourlyWind;
        public ImageView hourlyIcon;

        //Create public constructor that accepts entire row and finds each subview
        public ViewHolder(View itemView){
            super(itemView);
            hourlyHour = (TextView) itemView.findViewById(R.id.dailyListItem1);
            hourlyTemp = (TextView) itemView.findViewById(R.id.dailyListItem2);
            hourlyWind = (TextView) itemView.findViewById(R.id.dailyListItem3);
            hourlyIcon = (ImageView) itemView.findViewById(R.id.dailyListItem4);
        }
    }

    //Pass pulled data from API
    public HourlyAdapter(Context pulledContext, List<Integer> pulledHours24, List<String> pulledHours, List<Integer> pulledTemps, List<String> pulledConditions, List<Integer> pulledWind){
        context = pulledContext;
        comparisonHourSet = pulledHours24;
        hourSet = pulledHours;
        tempSet = pulledTemps;
        conditionSet = pulledConditions;
        windSet = pulledWind;
    }

    //Inflate the layout and return the holder
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //Inflate the layout
        View rowView = layoutInflater.inflate(R.layout.hourly_list_item, parent, false);

        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    //Populate the data into the item through the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Setup typeface
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on position
        String pulledHour = hourSet.get(position);
        int pulledTemp = tempSet.get(position);
        String pulledCond = conditionSet.get(position);
        int pulledWind = windSet.get(position);

        //Set item views to pulled data values
        TextView hourView = holder.hourlyHour;
        hourView.setText(String.valueOf(pulledHour));
        TextView tempView = holder.hourlyTemp;
        tempView.setText(String.valueOf(pulledTemp) + (char) 0x00B0);
        TextView windView = holder.hourlyWind;
        windView.setText(pulledWind + "MPH");
        ImageView condView = holder.hourlyIcon;
        //TODO - IMPLEMENT LOGIC TO HANDLE ICON SELECTION AND UNITS
        switch (pulledCond){
            case "clear-day":
                condView.setImageResource(R.drawable.ic_sunny_white);
                break;
            case "clear-night":
                condView.setImageResource(R.drawable.ic_clear_night_white);
                break;
            case "rain":
                condView.setImageResource(R.drawable.ic_rain_white);
                break;
            case "snow":
                condView.setImageResource(R.drawable.ic_snow_white);
                break;
            case "sleet":
                condView.setImageResource(R.drawable.ic_sleet_white);
                break;
            case "wind":
                condView.setImageResource(R.drawable.ic_windrose_white);
                break;
            case "fog":
                Log.i("Hourly condition", "Fog");
                Integer selectedHour = comparisonHourSet.get(position);
                Log.i("selectedHour", selectedHour.toString());
                Calendar calendar = Calendar.getInstance();
                Integer currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                Log.i("currentHour", currentHour.toString());
                calendar.setTimeInMillis(getTodayMilli());


                break;
            case "cloudy":
                condView.setImageResource(R.drawable.ic_cloudy_white);
                break;
            case "partly-cloudy-day":
                condView.setImageResource(R.drawable.ic_partlycloudy_white);
                break;
            case "partly-cloudy-night":
                condView.setImageResource(R.drawable.ic_partlycloudynight_white);
                break;
            default:
                condView.setImageResource(R.drawable.ic_cloudy_white);
                Log.i("CurrentConditions", "Unsupported condition.");
                break;
        }

        switch (MainActivity.setID){
            case 0:
                condView.setColorFilter(getContext().getResources().getColor(R.color.colorYellow));
                break;
            case 1:
                condView.setColorFilter(getContext().getResources().getColor(R.color.colorBlue));
                break;
            case 2:
                condView.setColorFilter(getContext().getResources().getColor(R.color.colorOrange));
                break;
            case 3:
                condView.setColorFilter(getContext().getResources().getColor(R.color.colorPurple));
                break;
        }

        //Customize fonts
        hourView.setTypeface(robotoLight);
        tempView.setTypeface(robotoLight);
        windView.setTypeface(robotoLight);

        if(animCount < 5) {
            //Set view animations
            setFadeAnimation(hourView);
            setFadeAnimation(tempView);
            setFadeAnimation(windView);
            setFadeAnimation(condView);
            animCount++;
        }
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setStartOffset(200);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return hourSet.size();
    }

    //Provide easy access to context object in the recyclerview
    private Context getContext(){
        return context;
    }

    /**
     * Gets today's day to pass to mainFragment.
     */
    private Long getTodayMilli(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String today = simpleDateFormat.format(calendar.getTime());
        try {
            Date date = simpleDateFormat.parse(today);
            Log.i("dateInMilli", date.getTime() + "");
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }
}
