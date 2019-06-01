package local.sandbox.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class StateFragment extends Fragment implements BeaconConsumer {
    private static final String TAG = StateFragment.class.getSimpleName();

    private static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private BeaconManager beaconManager;

    private TextView txtStatus;
    private TextView txtUUID;
    private TextView txtMajor;
    private TextView txtMinor;
    private TextView txtDistance;
    private TextView txtRSSI;

    public StateFragment() {
        // Required empty public constructor
    }

    public static StateFragment newInstance() {
        return new StateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);

        txtStatus = view.findViewById(R.id.txtStatus);
        txtUUID = view.findViewById(R.id.txtUUID);
        txtMajor = view.findViewById(R.id.txtMajor);
        txtMinor = view.findViewById(R.id.txtMinor);
        txtDistance = view.findViewById(R.id.txtDistance);
        txtRSSI = view.findViewById(R.id.txtRSSI);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Identifier uuid = Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825".toUpperCase());
        final Identifier major = null;
        final Identifier minor = null;

        final Region region = new Region("iBeacon", uuid, major, minor);

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region r) {
                Log.d("ibeacon", "Enter region " + r);
                try {
                    txtStatus.setText(R.string.inside);
                    beaconManager.stopRangingBeaconsInRegion(r);
                    beaconManager.startRangingBeaconsInRegion(r);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region r) {
                Log.d("ibeacon", "Exit region " + r);
                try {
                    beaconManager.stopRangingBeaconsInRegion(r);

                    txtStatus.setText(R.string.outside);
                    txtUUID.setText(R.string.unknown);
                    txtMajor.setText(R.string.unknown);
                    txtMinor.setText(R.string.unknown);
                    txtDistance.setText(R.string.unknown);
                    txtRSSI.setText(R.string.unknown);

                    DataStore.regionExited("12345678");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region r) {
                Log.d("ibeacon", "Determine state(" + state + ") " + r);
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region r) {
                Log.d("ibeacon", "Range beacons " + beacons.size());
                if (beacons.isEmpty()) {
                    return;
                }

                Beacon nearest = null;
                for (Beacon b : beacons) {
                    Log.d("ibeacon", "Range " + b);
                    if (nearest == null || nearest.getDistance() > b.getDistance()) {
                        nearest = b;
                    }
                }

                DataEntity entity = new DataEntity(Status.INSIDE,
                        nearest.getId1().toHexString(),
                        nearest.getId2().toString(),
                        nearest.getId3().toString(),
                        nearest.getDistance(),
                        nearest.getRssi());

                txtUUID.setText(entity.getUuid());
                txtMajor.setText(entity.getMajor());
                txtMinor.setText(entity.getMinor());
                txtDistance.setText(entity.getDistanceStr());
                txtRSSI.setText(entity.getRssiStr());

                DataStore.save("12345678", entity);
            }
        });

        try {
            beaconManager.stopMonitoringBeaconsInRegion(region);
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return getContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getContext().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int mode) {
        return getContext().bindService(intent, serviceConnection, mode);
    }
}
