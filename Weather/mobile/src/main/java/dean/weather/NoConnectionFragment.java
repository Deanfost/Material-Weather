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

public class NoConnectionFragment extends Fragment {
    Button btnRetry;
    private static connectionRefresher sConnectionRefresher;

    interface connectionRefresher {
        //Retry the connection
        void retryConnection();
    }

    //Obtain reference to calling activity
    public static void setConnectionRefresher(connectionRefresher connectionRefresher){
        sConnectionRefresher = connectionRefresher;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.no_connection_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRetry = (Button) getView().findViewById(R.id.btnRetryConnection);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retry the connection
                sConnectionRefresher.retryConnection();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getActivity());
    }
}
