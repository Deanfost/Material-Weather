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
    TextView detailsTitle;
    TextView reportTitle;
    TextView reportTemp;
    Typeface robotoLight;
    LinearLayout reportLayout;

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
        detailsTitle = (TextView) getView().findViewById(R.id.detailsTitle);
        reportTitle = (TextView) getView().findViewById(R.id.reportTitle);
        reportTemp = (TextView) getView().findViewById(R.id.reportTemp);
        reportLayout = (LinearLayout) getView().findViewById(R.id.reportLayout);

        //Customize font
        reportTemp.setTypeface(robotoLight);
        reportTemp.setText("72" + (char) 0x00B0);//Set the temp with degree symbol

    }
}
