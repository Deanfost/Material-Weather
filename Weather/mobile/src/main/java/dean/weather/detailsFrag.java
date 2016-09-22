package dean.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by DeanF on 9/16/2016.
 */
public class detailsFrag extends Fragment {
    LinearLayout reportLayout;
    TextView reportTemp;
    TextView detailsSunriseSetValue;
    TextView detailsWindValue;
    TextView detailsHumidityValue;
    Typeface robotoLight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setup references
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        reportTemp = (TextView) getView().findViewById(R.id.reportTemp);
        reportLayout = (LinearLayout) getView().findViewById(R.id.reportLayout);
        detailsSunriseSetValue = (TextView) getView().findViewById(R.id.detailsSunriseSetTime);
        detailsWindValue = (TextView) getView().findViewById(R.id.detailsWindValue);
        detailsHumidityValue = (TextView) getView().findViewById(R.id.detailsHumityValue);

        //Customize font
        reportTemp.setTypeface(robotoLight);
        detailsSunriseSetValue.setTypeface(robotoLight);
        detailsWindValue.setTypeface(robotoLight);
        detailsHumidityValue.setTypeface(robotoLight);
        reportTemp.setText("72" + (char) 0x00B0);//Set the temp with degree symbol

    }
}
