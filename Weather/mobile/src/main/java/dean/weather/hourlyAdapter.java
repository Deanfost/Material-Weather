package dean.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DeanF on 10/7/2016.
 */

public class hourlyAdapter extends RecyclerView.Adapter<hourlyAdapter.ViewHolder> {
    private List<Integer> hourSet;
    private List<Integer> tempSet;
    private List<String> conditionSet;
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
    public hourlyAdapter(Context pulledContext, List<Integer> pulledHours, List<Integer> pulledTemps, List<String> pulledConditions){
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
        int pulledHour = hourSet.get(position);
        int pulledTemp = tempSet.get(position);
        String pulledCond = conditionSet.get(position);

        //Set item views to pulled data values
        TextView hourView = holder.hourlyHour;
        hourView.setText(String.valueOf(pulledHour)+ "PM");
        TextView tempView = holder.hourlyTemp;
        tempView.setText(String.valueOf(pulledTemp) + (char) 0x00B0);
        TextView condView = holder.hourlyCond;
        condView.setText(pulledCond);
        ImageView iconView = holder.hourlyIcon;
        iconView.setImageResource(R.drawable.ic_cloudy);
    }

    @Override
    public int getItemCount() {
        return hourSet.size();
    }

    //Provide easy access to context object in the recyclerview
    private Context getContext(){
        return context;
    }
}
