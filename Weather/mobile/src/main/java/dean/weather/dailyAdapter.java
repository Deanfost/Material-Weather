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

public class dailyAdapter extends RecyclerView.Adapter<dailyAdapter.ViewHolder> {
    private List<String> dateSet;
    private List<String> descriptionSet;
    private List<String> conditionSet;
    private List<Integer> HISet;
    private List<Integer> LOSet;
    private List<Integer> precipSet;
    private List<String> windSet;
    private Context context;
    Typeface robotoLight;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Make references to each view in the row
        public TextView dateView;
        public ImageView condIcon;
        public TextView condView;
        public TextView precipView;
        public TextView windView;
        public TextView descView;
        public TextView HILOView;

        //Create public constructor that accepts entire row and finds each subview
        public ViewHolder(View itemView){
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.dailyCardItem1);
            condIcon = (ImageView) itemView.findViewById(R.id.dailyCardItem2);
            condView = (TextView) itemView.findViewById(R.id.dailyCardItem3);
            precipView = (TextView) itemView.findViewById(R.id.dailyCardItem5);
            windView = (TextView) itemView.findViewById(R.id.dailyCardItem7);
            descView = (TextView) itemView.findViewById(R.id.dailyCardItem8);
            HILOView = (TextView) itemView.findViewById(R.id.dailyCardItem9);
        }
    }

    //Pass pulled data from API
    public dailyAdapter(Context pulledContext, List<String> pulledDates, List<String> pulledDescriptions, List<String> pulledConditions, List<Integer> pulledHI, List<Integer> pulledLO, List<Integer> pulledPrecip, List<String> pulledWind){
        context = pulledContext;
        dateSet = pulledDates;
        descriptionSet = pulledDescriptions;
        conditionSet = pulledConditions;
        HISet = pulledHI;
        LOSet = pulledLO;
        precipSet = pulledPrecip;
        windSet = pulledWind;
    }

    //Inflate the layout and return the holder
    @Override
    public dailyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //Inflate the layout
        View rowView = layoutInflater.inflate(R.layout.daily_list_item, parent, false);

        //Return a new holder instance
        dailyAdapter.ViewHolder viewHolder = new dailyAdapter.ViewHolder(rowView);
        return viewHolder;
    }

    //Populate the data into the item through the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Setup typeface
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on position
        String pulledDate = dateSet.get(position);
        String pulledDesc = descriptionSet.get(position);
        String pulledCond = conditionSet.get(position);
        int pulledHI = HISet.get(position);
        int pulledLO = LOSet.get(position);
        int pulledPrecip = precipSet.get(position);
        String pulledWind = windSet.get(position);

        //Set item views to pulled data values
        TextView dateView = holder.dateView;
        dateView.setText(pulledDate);
        TextView descView = holder.descView;
        descView.setText(pulledDesc);
        TextView HILOView = holder.HILOView;
        HILOView.setText(String.valueOf(pulledHI) + (char) 0x00B0 + "/" + String.valueOf(pulledLO)+ (char) 0x00B0);
        TextView precipView = holder.precipView;
        precipView.setText(String.valueOf(pulledPrecip)+ "%");
        TextView windView = holder.windView;
        windView.setText(pulledWind);
        TextView condView = holder.condView;
        ImageView condIcon = holder.condIcon;
        //Determine which condition and icon to set
        switch (pulledCond){
            case "clear":
                condView.setText("Clear");
                condIcon.setImageResource(R.drawable.ic_sunny_white);
                break;
        }
        //Customize fonts
        precipView.setTypeface(robotoLight);
        windView.setTypeface(robotoLight);
        descView.setTypeface(robotoLight);
        HILOView.setTypeface(robotoLight);
    }

    @Override
    public int getItemCount() {
        return dateSet.size();
    }

    //Provide easy access to context object in the recyclerview
    private Context getContext(){
        return context;
    }
}
