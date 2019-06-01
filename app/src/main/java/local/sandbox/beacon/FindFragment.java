package local.sandbox.beacon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class FindFragment extends Fragment {
    public FindFragment() {
    }

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TextView txtFindPlace;
    private TextView txtFindState;
    private TextView txtFindDatetime;
    private TextView txtFindDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        txtFindPlace = view.findViewById(R.id.txtFindPlace);
        txtFindState = view.findViewById(R.id.txtFindState);
        txtFindDatetime = view.findViewById(R.id.txtFindDatetime);
        txtFindDistance = view.findViewById(R.id.txtFindDistance);

        String[] users = {"(ユーザ選択)", "テスト ユーザー"};

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_spinner_item,
                        users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = view.findViewById(R.id.spnName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Log.d("ibeacon", "selected position=" + pos + ", id=" + id);
                if (pos < 1) {
                    txtFindPlace.setText(R.string.unknown);
                    txtFindState.setText("");
                    txtFindDatetime.setText(R.string.unknown);
                    txtFindDistance.setText(R.string.unknown);
                    return;
                }

                Spinner s = (Spinner) adapterView;
                String user = (String) s.getSelectedItem();
                Log.d("ibeacon", "Select " + user);

                DataEntity entity = DataStore.find("12345678");

                if (entity == null) {
                    Log.d("ibeacon", "does not exits.");
                    return;
                }

                txtFindPlace.setText(entity.getPlace());
                txtFindState.setText(stateToViewText(entity.getState()));
                txtFindDatetime.setText(entity.getUpdatedAtStr());
                txtFindDistance.setText(entity.getDistanceStr());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private int stateToViewText(int state) {
        switch (state) {
            case Status.INSIDE:
                return R.string.stay;
            case Status.OUTSIDE:
                return R.string.leave;
            default:
                return -1;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
