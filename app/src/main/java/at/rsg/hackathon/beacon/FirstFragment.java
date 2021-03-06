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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.Collection;

public class FirstFragment  extends Fragment {
    private BeaconManager beaconManager;

    protected static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
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

        addRangeNotifier();



        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }
    public void addRangeNotifier() {

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                Beacon[] beaconList = beacons.toArray(new Beacon[beacons.size()]);

                for (int i= 0; i < beacons.size() ;i++) {
                    Beacon firstBeacon = beaconList[i];
                    if (firstBeacon.getId1().toString().equals("97c409f9-279a-4e52-98d2-be4528c9238b")) {
                        setStatusInfo(TAG, "The beacon " + i + "|" + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away and has the following data: major=" + firstBeacon.getId2() + ", minor=" + firstBeacon.getId3());
                    } else {
                        //setStatusInfo(TAG, "Found different  beacon " + i + "|" + firstBeacon.getId1().toString() + " is about " + firstBeacon.getDistance() + " meters away and has the following data: major=" + firstBeacon.getId2() + ", minor=" + firstBeacon.getId3());
                    }
                }

                setStatusInfo(TAG, beacons.size() + " beacons found. ");
            }

        };
        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));
        beaconManager.addRangeNotifier(rangeNotifier);
    }


    private void setStatusInfo(String tag, String message) {
        Log.i(TAG, message);

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                TextView tv = (TextView) getActivity().findViewById(R.id.beaconTextViewFirst);
                if (tv != null) {
                    CharSequence oldMsg = tv.getText();
                    tv.setText("RANGING: " + message + "\n" + oldMsg);
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }
}