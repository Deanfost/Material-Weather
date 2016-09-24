package dean.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by DeanF on 9/16/2016.
 */
public class dailyFrag extends Fragment {
    Typeface robotoLight;
//    LinearLayout dailyColDescLayout;
    GridLayout dailyGridLayout;

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
//        dailyColDescLayout = (LinearLayout) getView().findViewById(R.id.dailyColDescLayout);
        dailyGridLayout = (GridLayout) getView().findViewById(R.id.dailyGrid);
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        //Customize fonts
        //Gather views from layouts and attach references to them
//        ArrayList<TextView> colDescViews = new ArrayList<>();
        ArrayList<TextView> dailyGridViews = new ArrayList<>();
//        for(int i = 0; i < dailyColDescLayout.getChildCount(); i++){
//            colDescViews.add((TextView) dailyColDescLayout.getChildAt(i));
//        }
        for(int i = 0; i < dailyGridLayout.getChildCount(); i++){
            if(dailyGridLayout.getChildAt(i) instanceof TextView){
                dailyGridViews.add((TextView) dailyGridLayout.getChildAt(i));
            }
        }

        //Set the font
//        for(TextView tv: colDescViews){
//            tv.setTypeface(robotoLight);
//        }

        for(TextView tv: dailyGridViews){
            tv.setTypeface(robotoLight);
        }
    }
}
