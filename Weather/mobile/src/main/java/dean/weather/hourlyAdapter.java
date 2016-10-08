package dean.weather;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DeanF on 10/7/2016.
 */

public class hourlyAdapter extends RecyclerView.Adapter<hourlyAdapter.ViewHolder> {
    private int[] hourSet;
    private int[] tempSet;
    private String[] conditionSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    //Pass pulled data from API
    public hourlyAdapter(int[] pulledHours, int[] pulledTemps, String[] pulledConditions){
        hourSet = pulledHours;
        tempSet = pulledTemps;
        conditionSet = pulledConditions;
    }

    @Override
    public hourlyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(hourlyAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
