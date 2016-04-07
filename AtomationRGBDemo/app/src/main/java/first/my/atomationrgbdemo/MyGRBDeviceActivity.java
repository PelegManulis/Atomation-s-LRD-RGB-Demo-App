package first.my.atomationrgbdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;

import net.atomation.atomationsdk.ble.AtomManager;

import java.util.UUID;


public class MyGRBDeviceActivity extends AppCompatActivity {

    AtomManager mAtomManager;
    String mDevice;
    MenuItem mBattery;
    MenuItem mRssi;
    MenuItem mRssiRemote;
    private Handler mHandler;
    private Context mContext;
    public int CHECK_RSSI_PERIOD = 1500;
    static boolean CONNECTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb);
        CONNECTED = true;
        mAtomManager = AtomManager.getInstance();
        mHandler = new Handler();
        mContext = this;

        mDevice = getIntent().getExtras().getString(MyAtomationDemo.SELECTED_DEVICE_MAC);
        setColorPickerListeners();

        mAtomManager.registerBatteryNotifications(mDevice, true);
        mAtomManager.registerRemoteRssiNotifications(mDevice, true);
        mHandler.postDelayed(new ReadRssi(), CHECK_RSSI_PERIOD);//read rssi loop

    }

    private void setColorPickerListeners() {

        setColorPickerListener (0);
        setColorPickerListener (1);
    }

    private void setColorPickerListener(final int id){

        int pickerId     = (id == 0)? R.id.picker1     : R.id.picker2;
        int opticalBarId = (id == 0)? R.id.opacitybar1 : R.id.opacitybar2;

        final ColorPicker picker = (ColorPicker) findViewById(pickerId);
        picker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    processColor(picker.getColor(), id);
                }
                return false;
            }
        });

        OpacityBar opacityBar = (OpacityBar) findViewById(opticalBarId);
        opacityBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //skip event
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    processColor(picker.getColor(), id);
                }
                return false;
            }
        });
        picker.addOpacityBar(opacityBar);
    }

    protected void processColor(int intColor, int led)
    {

        byte a = (byte)(Color.alpha(intColor));
        float FACTOR = ((float)(a & 0xff)) / 255;
        byte r = (byte)(Color.red(intColor));
        byte g = (byte)(Color.green(intColor));
        byte b = (byte)(Color.blue(intColor));

        r = getColor(r, FACTOR);
        g = getColor(g, FACTOR);
        b = getColor(b, FACTOR);

        AtomManager.ATOM_RGB_LED_ID led_id = (led == 0)? AtomManager.ATOM_RGB_LED_ID.LED_1 :
                AtomManager.ATOM_RGB_LED_ID.LED_2;

        mAtomManager.writeRGB(mDevice, led_id, r, g, b);
    }

    byte getColor(byte color, float alpha){
        float col = (float)(color & 0xff);
        float result = alpha * col;
        if (result > 255) {
            return (byte)255;
        }
        else return (byte)result;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAtomManager == null) {
            mAtomManager = AtomManager.getInstance();
        }
        mAtomManager.updateAtomCallbacks(new MyAtomDevice());
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop() {
        CONNECTED = false;
        mAtomManager.disconnect(mDevice);
        this.finish();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mBattery = menu.findItem(R.id.battery);
        mRssi = menu.findItem(R.id.rssi);
        mRssiRemote = menu.findItem(R.id.rssi_remote);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public class MyAtomDevice extends BasicAtomDevice{

        @Override
        public void onDeviceConnectionStateChanged(String device,
                                                   @AtomManager.ConnectionState int state,
                                                   int status) {
            if (state == AtomManager.ConnectionState.STATE_DISCONNECTED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyGRBDeviceActivity.this.finish();
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.device_disconnected), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else
                CONNECTED = false;
        }

        @Override
        public void onBatteryValueRead(String address, final float batteryLevel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String val = String.format(getString(R.string.precent_valt),
                                    batteryLevel / 106.0);
                    mBattery.setIcon(getResources().getDrawable(R.drawable.battery));
                    mBattery.setTitle(val);
                }
            });

        }

        @Override
        public void onRemoteRssiValueRead(String address, final byte remRssi) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (remRssi > -50)
                        mRssiRemote.setIcon(ContextCompat.getDrawable(mContext,
                                R.drawable.rssi_back_3));
                    else if (remRssi > -65)
                        mRssiRemote.setIcon(ContextCompat.getDrawable(mContext,
                                R.drawable.rssi_back_2));
                    else if (remRssi > -90)
                        mRssiRemote.setIcon(ContextCompat.getDrawable(mContext,
                                R.drawable.rssi_back_1));
                    else
                        mRssiRemote.setIcon(ContextCompat.getDrawable(mContext,
                                R.drawable.rssi_back_0));

                    mRssiRemote.setTitle(String.format(getString(R.string.precent_dbm), remRssi));
                    mRssiRemote.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
            });
        }

        @Override
        public void onRssiValueRead(String address, final int rssi) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rssi > -50)
                        mRssi.setIcon(ContextCompat.getDrawable(mContext, R.drawable.rssi_3));
                    else if (rssi > -65)
                        mRssi.setIcon(ContextCompat.getDrawable(mContext, R.drawable.rssi_2));
                    else if (rssi > -90)
                        mRssi.setIcon(ContextCompat.getDrawable(mContext, R.drawable.rssi_1));
                    else
                        mRssi.setIcon(ContextCompat.getDrawable(mContext, R.drawable.rssi_0));

                    mRssi.setTitle(String.format(getString(R.string.precent_dbm), rssi));
                    mRssi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
            });
        }

        @Override
        public void onWriteExecuted(String address, UUID uuid, final int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == 0)
                        Toast.makeText(getApplicationContext(), getString(R.string.command_executed),
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.command_failed),
                                Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class ReadRssi implements Runnable {
        @Override
        public void run() {
            if (CONNECTED) {
                mAtomManager.readRssi(mDevice);
                mHandler.postDelayed(this, CHECK_RSSI_PERIOD);
            }
        }
    }

}
