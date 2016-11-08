package dean.weather;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static dean.weather.IntroActivity.LOCATION_PERMISSIONS_REQUEST;

/**
 * Created by DeanF on 11/7/2016.
 */

public class PermissionsFragment extends Fragment {
    Button btnAllowAccess;
    private static Initializer sInitializer;

    interface Initializer {
        //Signal mainActivity to begin normal operations
        void beginNormalOperations();
    }

    //Obtain reference to calling activity
    public static void setInitializer(Initializer initializer){
        sInitializer = initializer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.need_permission_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //If we lose or for some reason don't have location permissions by main launch, display this fragment
        btnAllowAccess = (Button) getView().findViewById(R.id.btnNeedAccess);
        btnAllowAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);

            }
        });
    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //If the user granted permissions, begin normal operation in mainActivity
                sInitializer.beginNormalOperations();
            }
        }
    }
}
