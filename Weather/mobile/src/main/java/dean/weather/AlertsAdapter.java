package dean.weather;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.johnhiott.darkskyandroidlib.models.AlertsBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean Foster on 1/19/2017.
 */

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder>{
    Typeface robotoLight;
    private List<AlertsBlock> dataSet;
    private Context context;//Activity context passed from AlertsActivity
    private Integer setID;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cardTitle;
        public TextView cardDesc;
        public TextView cardDismiss;
        public TextView cardView;

        //Accepts entire card, and finds each subview
        public ViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardViewTitle);
            cardDesc = (TextView) itemView.findViewById(R.id.cardViewDesc);
            cardDismiss = (TextView) itemView.findViewById(R.id.btnCardDismiss);
            cardView = (TextView) itemView.findViewById(R.id.btnCardView);

            cardDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Card", "Dismiss clicked");
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Card", "View clicked");
                }
            });
        }
    }

    //Gather data for the adapter
    public AlertsAdapter (Context passedContext, List<AlertsBlock> passedDataSet, Integer passedSetID){
        context = passedContext;
        dataSet = new ArrayList<>(passedDataSet);
        setID = passedSetID;
    }

    @Override
    public AlertsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate a new card for each iteration
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlertsAdapter.ViewHolder holder, int position) {
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on current iteration
        String alertTitle = dataSet.get(position).getTitle();
        String alertDesc = dataSet.get(position).getDescription();

        //Set the views in the card
        TextView title = holder.cardTitle;
        TextView desc = holder.cardDesc;
        title.setText(alertTitle);
        desc.setText(alertDesc);

        //Set typeface of description
        desc.setTypeface(robotoLight);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public Context getContext() {
        return context;
    }
}
