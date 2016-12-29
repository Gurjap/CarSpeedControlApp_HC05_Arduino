package com.example.gurjap.carspeedcontrol;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.*;
import java.util.ArrayList;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.widget.Toast;
import android.speech.RecognizerIntent;

import android.widget.ImageView;
import java.util.Locale;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

@SuppressWarnings("ALL")
public final class DeviceControlActivity extends BaseActivity  {

    private static final String DEVICE_NAME = "DEVICE_NAME";

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static BluetoothResponseHandler mHandler;
    private final int REq_code = 100;
    SeekBar speed;
    ToggleButton horn;
    SeekBar direction;
    AppCompatTextView speed_view,speech_view;
  //  AppCompatTextView reply_view;
    AppCompatButton stop_btn;

    private String deviceName;
    static String tobesend;
  //  static DeviceControlActivity a=new DeviceControlActivity();
    String speed_var="00";
    String Direction_var="00";
ImageView mic;
      @Override

    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

     //   PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);
        speed_view= (AppCompatTextView) findViewById(R.id.speed_view);
      //  reply_view= (AppCompatTextView) findViewById(R.id.replay_view);
        speed= (SeekBar) findViewById(R.id.speed_seekbar);
        direction= (SeekBar) findViewById(R.id.direction_seekbar);
        horn= (ToggleButton) findViewById(R.id.horn);
        mic= (ImageView) findViewById(R.id.mic);
        speech_view= (AppCompatTextView) findViewById(R.id.speech_view);
        stop_btn= (AppCompatButton) findViewById(R.id.stop_btn);

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed.setProgress(100);
                direction.setProgress(100);
        onchange_values();
            }
        });
        speed.setProgress(100);
        direction.setProgress(100);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        onchange_values();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
       /* mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotopromtmic();
            }
        });*/
        horn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               onchange_values();
            }
        });
        direction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            onchange_values();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            direction.setProgress(100);
            onchange_values();
        }
    });


        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);


        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else {
       getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);
        }




    }

    /*private void gotopromtmic() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt_msg));
        try {
            startActivityForResult(intent, REq_code);
        } catch (Exception a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }



    }*/



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
    }

    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }

    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }

    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    @Override

    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        final MenuItem bluetooth = menu.findItem(R.id.menu_search);
        if (bluetooth != null) bluetooth.setIcon(this.isConnected() ?
                R.drawable.ic_action_device_bluetooth_connected :
                R.drawable.ic_action_device_bluetooth);
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override

    public void onStart() {
        super.onStart();
 }

    public void sendCommand(String a) {

        byte[] command = a.getBytes();
        if (isConnected())connector.write(command);

    }

    void onchange_values(){

   //     speed_view.setText(""+speed.getProgress());


        if(speed.getProgress()>100)
        {
           speed_var=String.format("%02d",(speed.getProgress()-100));
            speed_var=speed_var+"F";

        }
        else if(speed.getProgress()<100){

            speed_var=String.format("%02d",(99-speed.getProgress()));

            speed_var=speed_var+"B";


        }
        else if(speed.getProgress()==100){
            speed_var="00";
            speed_var=speed_var+"F";

        }

        if(direction.getProgress()>100)
        {
            Direction_var=String.format("%02d",(direction.getProgress()-100));
Direction_var=Direction_var+"R";

        }
       else if(direction.getProgress()<100)
        {
            Direction_var=String.format("%02d",(99-direction.getProgress()));
            Direction_var=Direction_var+"L";

        }
        else if(direction.getProgress()==100){
            Direction_var="00";
            Direction_var=Direction_var+"R";
        }

        String horn1=horn.isChecked()?"T":"S";

        String a=horn1+speed_var+Direction_var;
        speed_view.setText(a);
        sendCommand(a);

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REq_code: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speech_view.setText(result.get(0));
                    switch (result.get(0)){
                        case "forward":speed.setProgress(199);break;
                        case "backward":speed.setProgress(0);break;
                        case "left":direction.setProgress(0);break;
                        case "right":direction.setProgress(199);break;
                        case "normal":direction.setProgress(100);break;
                        case "stop":speed.setProgress(100);direction.setProgress(100);break;
                        case "sound":if(horn.isChecked())horn.setChecked(false);
                                else horn.setChecked(true);
                            }
                    onchange_values();
                }
                break;
            }


    }
*/
    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
        }
    }

      void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
    }

    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<DeviceControlActivity> mActivity;

        public BluetoothResponseHandler(DeviceControlActivity activity) {
            mActivity = new WeakReference<DeviceControlActivity>(activity);
        }

        public void setTarget(DeviceControlActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<DeviceControlActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity activity = mActivity.get();
           if (activity != null)
            {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        final android.support.v7.app.ActionBar bar = activity.getSupportActionBar();
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                bar.setSubtitle(MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                bar.setSubtitle(MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                bar.setSubtitle(MSG_NOT_CONNECTED);
                                break;
                        }
                        activity.invalidateOptionsMenu();
                        break;

                    case MESSAGE_READ:
                        String readMessage = (String) msg.obj;
                        if (readMessage != null) {
                            //activity.reply_view.setText(readMessage);
                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                 }
            }
        }


    }
}