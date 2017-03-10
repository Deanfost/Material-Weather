package dean.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcLoader;


/**
 * Created by DeanF on 10/31/2016.
 */

public class LoadingFragment extends Fragment {
    ArcConfiguration configuration;
    SimpleArcLoader simpleArcLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("Loading Fragment", "Instantiated");

        //Customize loading view
        simpleArcLoader =(SimpleArcLoader) getView().findViewById(R.id.arcLoader);
        configuration = new ArcConfiguration(getActivity());
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        configuration.setColors(new int[]{getResources().getColor(R.color.colorBlueLight)});
        simpleArcLoader.refreshArcLoaderDrawable(configuration);
        simpleArcLoader.start();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getActivity());
    }
}
