package dean.weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by DeanF on 3/23/2017.
 */

public class IabDialogFragment extends DialogFragment {
    public interface BillingDialogListener{
        public void onIndexOneClick();
        public void onIndexTwoClick();
        public void onIndexThreeClick();
        public void onIndexFourClick();
    }

    BillingDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the listener so we can send events to the host
            listener = (BillingDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Donation")
                .setItems(R.array.donationsEntries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                listener.onIndexOneClick();
                                break;
                            case 1:
                                listener.onIndexTwoClick();
                                break;
                            case 2:
                                listener.onIndexThreeClick();
                                break;
                            case 3:
                                listener.onIndexFourClick();
                                break;

                        }
                    }
                });
        return builder.create();
    }
}
