package dean.weather;

import android.content.Context;
import android.content.Intent;
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
        public View card;
        public TextView cardTitle;
        public TextView cardDesc;
        public TextView cardActionDismiss;
        public TextView cardActionView;

        //Accepts entire card, and finds each subview
        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            cardTitle = (TextView) itemView.findViewById(R.id.cardViewTitle);
            cardDesc = (TextView) itemView.findViewById(R.id.cardViewDesc);
            cardActionDismiss = (TextView) itemView.findViewById(R.id.btnCardDismiss);
            cardActionView = (TextView) itemView.findViewById(R.id.btnCardView);
        }
    }

    //Gather data for the adapter
    public AlertsAdapter (Context passedContext, List<AlertsBlock> passedDataSet, Integer passedSetID){
        context = passedContext;
        dataSet = new ArrayList<>(passedDataSet);
        setID = passedSetID;
        Log.i("passedSetID", passedSetID.toString());
    }

    @Override
    public AlertsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate a new card for each iteration
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on current iteration
        String alertTitle = dataSet.get(position).getTitle();
        String alertDesc = dataSet.get(position).getDescription();

        //Set the views in the card
        View cardView = holder.card;
        TextView title = holder.cardTitle;
        TextView desc = holder.cardDesc;
        TextView cardActionView = holder.cardActionView;
        TextView cardActionDismiss = holder.cardActionDismiss;
        title.setText(alertTitle);
        desc.setText(alertDesc);

        //Set typeface of description
        desc.setTypeface(robotoLight);

        //Set the background color
        switch (setID){
            case 0:
                cardView.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
                break;
            case 1:
                cardView.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
                break;
            case 2:
                cardView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
                break;
            case 3:
                cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPurple));
                break;
        }

        cardActionDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Card", "Dismiss clicked");
                Log.i("Position", position + "");


            }
        });

        cardActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Card", "View clicked");
                Log.i("Position", position + "");

                Intent alertIntent = new Intent(getContext(), ViewAlertActivity.class);
                alertIntent.putExtra("setID", setID);
                alertIntent.putExtra("alertTitle", dataSet.get(position).getTitle());
                alertIntent.putExtra("alertDesc", dataSet.get(position).getDescription());
                getContext().startActivity(alertIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public Context getContext() {
        return context;
    }
}
