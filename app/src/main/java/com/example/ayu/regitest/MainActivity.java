package com.example.ayu.regitest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    TextView textStatus, read_textView, welcome;
    Button btnParied, btnSearch, btnSend, send_button;
    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;


    private final static int REQUEST_ENABLE_BT = 1;
    private Socket client;
    private static final String SERVER_IP = "13.209.98.41";
    public String CONNECT_MSG;
    private static final String STOP_MSG = "stop";

    //Guest Key
    private EditText join_gkey;
    private Button gkey_button;

    //Provided Guest Key
    private EditText input_gkey;
    private Button igkey_button;


     public String key = null; // "QWERQWERQWERQWERQWERQWERQWERQWER";


    public String UserName, UserEmail;
    public String OTP;
    public boolean usermode;

    private static final int BUF_SIZE = 100;

    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        join_gkey = findViewById( R.id.join_gkey);
        input_gkey = findViewById( R.id.input_gkey);
        gkey_button = findViewById(R.id.gkey_button);
        igkey_button = findViewById(R.id.igkey_button);
        send_button = findViewById(R.id.send_button);
        read_textView = findViewById(R.id.read_textView);
        welcome = findViewById(R.id.welcome);

        UserName = getIntent().getStringExtra("UserName");
        UserEmail = getIntent().getStringExtra("UserEmail");
        usermode = getIntent().getBooleanExtra("usermode", true);

        welcome.setText(UserName + " 님 환영합니다.");


        //Guest Key 등록
        gkey_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usermode){
                Toast.makeText( getApplicationContext(), String.format("권한이 없습니다."), Toast.LENGTH_SHORT ).show();

            }
                else {
                    if(join_gkey.getText().toString().equals("")){
                        Toast.makeText( getApplicationContext(), String.format("코드를 입력하세요."), Toast.LENGTH_SHORT ).show();
                    }
                    else{
                    String guestkey = join_gkey.getText().toString();
                    guestkey = String.format("%-32s", guestkey);


                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                //등록 성공시

                                if (success) {

                                    Toast.makeText(getApplicationContext(), "정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();

                                    //등록 실패시
                                } else {
                                    Toast.makeText(getApplicationContext(), "등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    //서버로 Volley를 이용해서 요청
                    guestkeyRequest guestkeyRequest = new guestkeyRequest(guestkey, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(guestkeyRequest);
                }
            }
            }

        });
        //버튼 활성화
        join_gkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    gkey_button.setClickable(true);
                    gkey_button.setBackgroundColor(Color.BLUE);
                } else {
                    gkey_button.setClickable(false);
                    gkey_button.setBackgroundColor(Color.GRAY);
                }
            }
        });

        //Guest Key 입력
        igkey_button.setOnClickListener( v-> {
            key = input_gkey.getText().toString();
            key = String.format("%-32s", key);
            Toast.makeText( getApplicationContext(), String.format("Key가 저장되었습니다."), Toast.LENGTH_SHORT ).show();


        });
        //버튼 활성화
        input_gkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    igkey_button.setClickable(true);
                    igkey_button.setBackgroundColor(Color.BLUE);
                } else {
                    igkey_button.setClickable(false);
                    igkey_button.setBackgroundColor(Color.GRAY);
                }
            }
        });

        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);


        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // variables
        textStatus = findViewById(R.id.text_status);
        btnParied = findViewById(R.id.btn_paired);
        btnSearch = findViewById(R.id.btn_search);
        btnSend = findViewById(R.id.btn_send);
        listView = findViewById(R.id.listview);
        // Show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());


        send_button.setOnClickListener(view -> {
            if(!usermode){
                key = "QWERQWERQWERQWERQWERQWERQWERQWER";
            }
            if(key == null){
                Toast.makeText( getApplicationContext(), String.format("Key가 입력되지 않았습니다."), Toast.LENGTH_SHORT ).show();

            }
            else {
                Connect connect = new Connect();
                CONNECT_MSG = UserEmail;
                connect.execute(UserEmail);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @SuppressLint("StaticFieldLeak")
    private class Connect extends AsyncTask< String , String,Void > {

        @Override
        protected Void doInBackground(String... strings) {
            DataInputStream dataInput;
            try {
                client = new Socket(SERVER_IP, 4000);
                DataOutputStream dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                String output_message = strings[0];
                dataOutput.writeUTF(output_message);

            } catch (UnknownHostException e) {
                String str = e.getMessage();
                Log.w("discnt", str + " 1");
                return null;

            } catch (IOException e) {
                String str = e.getMessage();
                Log.w("discnt", str + " 2");
                return null;
            }

            while (true){
                try {
                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte  = dataInput.read(buf);
                    String input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)){
                        publishProgress(input_message);
                    }
                    else{
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){
            Log.v("server: ", params[0]);
            try {
                OTP = AES256.AES_Decode(key, params[0]);
            } catch (NoSuchAlgorithmException e) {
                OTP = "1";
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                OTP = "2";
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                OTP = "3";
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                OTP = "4";
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                OTP = "5";
                e.printStackTrace();
            } catch (BadPaddingException e) {
                OTP = "6";
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                OTP = "7";
                e.printStackTrace();
            }
            read_textView.setText(""); // Clear the chat box
            read_textView.append("OTP : " + OTP);
        }
    }



    @SuppressLint("MissingPermission")
    public void onClickButtonPaired(View view){
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void onClickButtonSearch(View view){
        // Check if the device is already discovering
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter.clear();
                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Send string "a"
    public void onClickButtonSend(View view){
        //Input Popup
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("OTP 코드 전송");
        alert.setMessage(OTP + " 전송");

        alert.setPositiveButton("전송", (dialog, which) -> {
            if(connectedThread!=null){
                connectedThread.write(OTP);
                Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();

            }

        });
        alert.setNegativeButton("취소", (dialog, which) -> {

        });
        alert.show();
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");

            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // create & connect socket
            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                textStatus.setText("connection failed!");
                e.printStackTrace();
            }

            // start bluetooth communication
            if(flag){
                textStatus.setText("connected to "+name);
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
            }

        }
    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }









}