package dean.weather;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DeanF on 9/16/2016.
 */
public class dailyFrag extends Fragment {
    Typeface robotoLight;
    GridLayout dailyGridLayout;
    RelativeLayout dailyFragLayout;
    TextView dailyDetailsBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Setup references
        dailyGridLayout = (GridLayout) getView().findViewById(R.id.dailyGrid);
        dailyFragLayout = (RelativeLayout) getView().findViewById(R.id.dailyLayout);
        dailyDetailsBtn = (TextView) getView().findViewById(R.id.dailyDetailsBtn);
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        //Customize fonts
        ArrayList<TextView> dailyGridViews = new ArrayList<>();
        for(int i = 0; i < dailyGridLayout.getChildCount(); i++){
            if(dailyGridLayout.getChildAt(i) instanceof TextView){
                dailyGridViews.add((TextView) dailyGridLayout.getChildAt(i));
            }
        }

        //Set the font
        for(TextView tv: dailyGridViews){
            tv.setTypeface(robotoLight);
        }

        //Setup click events
        dailyFragLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dailyAct = new Intent(getContext(), dailyActivity.class);
                startActivity(dailyAct);
            }
        });

        dailyDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dailyAct = new Intent(getContext(), dailyActivity.class);
                startActivity(dailyAct);
            }
        });
    }
}
