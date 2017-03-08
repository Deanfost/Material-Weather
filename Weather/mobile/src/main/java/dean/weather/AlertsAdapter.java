package dean.weather;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        public FrameLayout card;
        public TextView cardTitle;
        public TextView cardDesc;
        public TextView cardActionShare;
        public TextView cardActionView;

        //Accepts entire card, and finds each subview
        public ViewHolder(View itemView) {
            super(itemView);
            card = (FrameLayout) itemView.findViewById(R.id.alertCardView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardViewTitle);
            cardDesc = (TextView) itemView.findViewById(R.id.cardViewDesc);
            cardActionShare = (TextView) itemView.findViewById(R.id.btnCardShare);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Get the data based on current iteration
        String alertTitle = dataSet.get(position).getTitle();
        String alertDesc = dataSet.get(position).getDescription();

        //Set the views in the card
        FrameLayout cardView = holder.card;
        TextView title = holder.cardTitle;
        TextView desc = holder.cardDesc;
        TextView cardActionView = holder.cardActionView;
        TextView cardActionShare = holder.cardActionShare;
        title.setText(alertTitle);
        desc.setText(alertDesc);

        //Set typeface of description
        desc.setTypeface(robotoLight);

        //Set the background color
        switch (setID){
            case 0:
                cardView.setBackground(context.getResources().getDrawable(R.drawable.card_background_yellow));
                cardActionShare.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_yellow));
                cardActionView.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_yellow));
                break;
            case 1:
                cardView.setBackground(context.getResources().getDrawable(R.drawable.card_background_blue));
                cardActionShare.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_blue));
                cardActionView.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_blue));
                break;
            case 2:
                cardView.setBackground(context.getResources().getDrawable(R.drawable.card_background_orange));
                cardActionShare.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_orange));
                cardActionView.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_orange));
                break;
            case 3:
                cardView.setBackground(context.getResources().getDrawable(R.drawable.card_background_purple));
                cardActionShare.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_purple));
                cardActionView.setBackground(context.getResources().getDrawable(R.drawable.button_ripple_purple));
                break;
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Card", "Clicked");
                Log.i("Position", position + "");

                //Pass selected alert details to ViewAlertActivity
                Intent alertIntent = new Intent(getContext(), ViewAlertActivity.class);
                alertIntent.putExtra("setID", setID);
                alertIntent.putExtra("alertTitle", dataSet.get(position).getTitle());
                alertIntent.putExtra("alertDesc", dataSet.get(position).getDescription());
                alertIntent.putExtra("alertSrc", dataSet.get(position).getUri());
                getContext().startActivity(alertIntent);
            }
        });

        cardActionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Card", "Share clicked");
                Log.i("Position", position + "");

                //Create a message to send to a messaging service or social media
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, dataSet.get(position).getUri());
                shareIntent.setType("text/plain");
                //Resolve the intent, and launch chooser
                PackageManager pm = context.getPackageManager();
                if(shareIntent.resolveActivity(pm) != null){
                    context.startActivity(Intent.createChooser(shareIntent, "Send to"));
                }
                else{
                    Log.i("AlertsAdapter", "No application can handle share intent");
                    Toast.makeText(context, "No application can handle this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Card", "View clicked");
                Log.i("Position", position + "");

                //Pass selected alert details to ViewAlertActivity
                Intent alertIntent = new Intent(getContext(), ViewAlertActivity.class);
                alertIntent.putExtra("setID", setID);
                alertIntent.putExtra("alertTitle", dataSet.get(position).getTitle());
                alertIntent.putExtra("alertDesc", dataSet.get(position).getDescription());
                alertIntent.putExtra("alertSrc", dataSet.get(position).getUri());
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
