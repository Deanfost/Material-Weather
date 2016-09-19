package dean.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DeanF on 9/16/2016.
 */
public class detailsFrag extends Fragment {
    TextView detailsTitle;
    TextView reportTitle;
    Typeface robotoThin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Customize view fonts
        detailsTitle = (TextView) getView().findViewById(R.id.detailsTitle);
        reportTitle = (TextView) getView().findViewById(R.id.reportTitle);


    }
}
