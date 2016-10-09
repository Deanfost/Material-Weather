package dean.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by DeanF on 10/7/2016.
 */

public class hourlyAdapter extends RecyclerView.Adapter<hourlyAdapter.ViewHolder> {
    private int[] hourSet;
    private int[] tempSet;
    private String[] conditionSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Make references to each view in the row
        public TextView hourlyHour;
        public TextView hourlyTemp;
        public TextView hourlyCond;
        public ImageView hourlyIcon;

        //Create public constructor that accepts entire row and finds each subview
        public ViewHolder(View itemView){
            super(itemView);
            hourlyHour = (TextView) itemView.findViewById(R.id.hourlyListItem1);
            hourlyTemp = (TextView) itemView.findViewById(R.id.hourlyListItem2);
            hourlyCond = (TextView) itemView.findViewById(R.id.hourlyListItem3);
            hourlyIcon = (ImageView) itemView.findViewById(R.id.hourlyListItem4);
        }
    }

    //Pass pulled data from API
    public hourlyAdapter(Context pulledContext, int[] pulledHours, int[] pulledTemps, String[] pulledConditions){
        context = pulledContext;
        hourSet = pulledHours;
        tempSet = pulledTemps;
        conditionSet = pulledConditions;
    }

    //Inflate the layout and return the holder
    @Override
    public hourlyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(hourlyAdapter.ViewHolder holder, int position) {
        //Get the data based on position
        int pulledHour = hourSet[position];
        int pulledTemp = tempSet[position];
        String pulledCond = conditionSet[position];

        //Set item views to pulled data values
        TextView hourView = holder.hourlyHour;
        hourView.setText(pulledHour);
        TextView tempView = holder.hourlyTemp;
        tempView.setText(pulledTemp + (char) 0x00B0);
        TextView condView = holder.hourlyCond;
        condView.setText(pulledCond);
        ImageView iconView = holder.hourlyIcon;
        iconView.setImageResource(R.drawable.ic_cloudy);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //Provide easy access to context object in the recyclerview
    private Context getContext(){
        return context;
    }
}
