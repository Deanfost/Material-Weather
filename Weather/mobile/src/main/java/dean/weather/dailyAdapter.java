package dean.weather;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public DailyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Setup typeface
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on position
        String pulledDay = daySet.get(position);
        String pulledCond = conditionSet.get(position);
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
            case "cloudy":
                condIcon.setImageResource(R.drawable.ic_cloudy_color);
                break;
        }
        //Customize fonts
        dayView.setTypeface(robotoLight);
        precipView.setTypeface(robotoLight);
        HILOView.setTypeface(robotoLight);
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
