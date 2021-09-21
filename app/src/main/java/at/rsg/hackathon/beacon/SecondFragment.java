package at.rsg.hackathon.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class SecondFragment extends Fragment  {
    private BeaconManager beaconManager;

    protected static final String TAG = "SecondFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        Log.e(TAG, "XXX Bind");
        //beaconManager.bind(this);
        addMonitorNotifier();

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


    public void addMonitorNotifier() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                setStatusInfo(TAG, "I just saw a beacon for the first time!");

            }

            @Override
            public void didExitRegion(Region region) {
                setStatusInfo(TAG, "I no longer see a beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                setStatusInfo(TAG, "I have just switched from seeing/not seeing beacons: " + (state == MonitorNotifier.INSIDE ? "INSIDE" : "OUTSIDE"));
            }
        });

        beaconManager.startMonitoring(new Region("myMonitoringUniqueId", null, null, null));
    }

    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }


    private void setStatusInfo(String tag, String message) {
        Log.i(TAG, message);

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                TextView tv = (TextView) getActivity().findViewById(R.id.beaconTextViewSecond);
                if (tv != null) {
                    CharSequence oldMsg = tv.getText();
                    tv.setText("MONITORING: " + message + "\n" + oldMsg);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}