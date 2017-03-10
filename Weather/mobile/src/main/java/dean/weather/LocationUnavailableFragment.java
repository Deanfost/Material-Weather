package dean.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

/**
 * Created by DeanF on 11/8/2016.
 */

public class LocationUnavailableFragment extends Fragment {
    Button btnRetry;
    private static dataFetcher sDataFetcher;

    interface dataFetcher{
        //Retry the data fetch
        void retryDataFetch();
    }

    //Obtain reference to calling activity
    public static void setDataFetcher(dataFetcher dataFetcher){
        sDataFetcher = dataFetcher;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_unavailable_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRetry = (Button) getView().findViewById(R.id.btnRetryLocation);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retry data fetch
                sDataFetcher.retryDataFetch();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getActivity());
    }
}
