package first.my.atomationrgbdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.atomation.atomationsdk.ble.AtomManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MyAtomationDemo extends AppCompatActivity {

    ListView list;

    List<String>	nameArr	= new ArrayList<>();
    List<String> addressArr	= new ArrayList<>();

    private AtomManager mAtomManager;
    public static final int STATUS_INIT = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    public static final int SCAN_PERIOD = 20;

    private int mStatus = STATUS_INIT;
    private String	mStatusText;
    public boolean	mIsConnected=false;
    public static final int SUCCESS = 0;
    public static final String SELECTED_DEVICE_MAC = "mac";
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_atomation_demo);
        mAtomManager = AtomManager.getInstance();
        mAtomManager.updateAtomCallbacks(new MyAtomDevice());

        CustomList adapter = new CustomList(MyAtomationDemo.this, nameArr, addressArr);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateStatus(getString(R.string.connection_in_progress));
                mStatus = STATUS_CONNECTING;
                mAtomManager.connect(addressArr.get(position));
                mDialog = ProgressDialog.show(MyAtomationDemo.this, null,
                        getString(R.string.connection_in_progress), true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatus("");
        mAtomManager = AtomManager.getInstance();
        mAtomManager.updateAtomCallbacks(new MyAtomDevice());
    }

    @Override
    public void onDestroy() {
        mAtomManager.stop();
        super.onDestroy();
    }

    public void addDevice(final String address, final String name){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!addressArr.contains(address)) {
                    nameArr.add(name);
                    addressArr.add(address);
                    refreshList();
                }
            }
        });
    }

    private void updateStatus(String statusText){
        mStatusText = statusText;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                TextView text = (TextView)findViewById(R.id.textStatus);
                text.setText(mStatusText);
            }
        });
    }

    private void refreshList(){
        CustomList adapter = new CustomList(MyAtomationDemo.this, nameArr, addressArr);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void clickScan(View view)
    {
        nameArr.clear();
        addressArr.clear();
        refreshList();
        if (checkPermission()) {
            mAtomManager.disconnectAll();
            mAtomManager.startScanning(SCAN_PERIOD);
        }
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        5);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        ) {
                    mAtomManager.startScanning(SCAN_PERIOD);

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public class MyAtomDevice extends BasicAtomDevice{

        @Override
        public void onDeviceFound(String address, String name, int rssi, byte[] scanRecord) {
            addDevice(address, name);
        }

        @Override
        public void onDeviceConnectionStateChanged(String address,
                                                   @AtomManager.ConnectionState int state,
                                                   int status) {
            mDialog.dismiss();
            if (status == SUCCESS) {
                if (state == AtomManager.ConnectionState.STATE_CONNECTED) {
                    mIsConnected = true;
                    Intent myIntent = new Intent(MyAtomationDemo.this,
                            MyGRBDeviceActivity.class);
                    myIntent.putExtra(SELECTED_DEVICE_MAC, address);
                    MyAtomationDemo.this.startActivity(myIntent);
                }
            }else {
                updateStatus(getString(R.string.connection_failed));
            }
        }

        @Override
        public void onScanning(final boolean isScanning) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isScanning)
                        updateStatus(getString(R.string.scaning_in_porgress));
                    else if (mStatus == STATUS_INIT)
                        updateStatus("");
                }
            });
        }
    }
}


