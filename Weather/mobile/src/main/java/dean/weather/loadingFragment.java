//package dean.weather;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.github.rahatarmanahmed.cpv.CircularProgressView;
//
///**
// * Created by DeanF on 10/31/2016.
// */
//
//public class loadingFragment extends Fragment {
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.loading_fragment, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        CircularProgressView progressView = (CircularProgressView) getView().findViewById(R.id.progress_view);
//        progressView.startAnimation();
//    }
//}
