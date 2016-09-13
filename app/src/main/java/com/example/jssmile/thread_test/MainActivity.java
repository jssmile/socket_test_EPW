package com.example.jssmile.thread_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import android.util.Log;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    public static String ip;
    public static String bt;

    public static TextView clnt_recv;
    public static TextView clnt_send;
    public static EditText send_tx;
    public static Button send;

    public static Button bt_send;
    public static EditText bt_send_tx;

    //Parameters for internet connection
    public static int ServerPort = 6000;
    public static Socket socket = null;
    public static OutputStream out = null;
    public static InputStream in = null;

    //Parameters for bluetooth
    public BluetoothAdapter mAdapter= null;
    public BluetoothSocket btSocket = null;
    public OutputStream out_bt = null;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String text;

    //Camera
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    static Camera camera = null;
    boolean isPreview = false;
    private final String tag = "VideoServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the data(ip and bt) from login page
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        ip = data.getString("ipname");
        bt = data.getString("btname");

        //init bluetooth
        init_bt();

        //Initial Layout
        init_layout();

        //send the data through the internet
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = send_tx.getText().toString().trim();
                send_str(text);
            }
        });

        //send the data through the bluetooth
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = bt_send_tx.getText().toString().trim();
                send_bt(text);
                bt_send_tx.setText("");
            }
        });

        start_camera();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        byte[] rebyte = new byte[20];
                        socket = new Socket(ip, ServerPort);
                        in = socket.getInputStream();
                        in.read(rebyte);
                        String str = new String(rebyte);
                        clnt_recv.setText(str);
                    }

                } catch (Exception e) {
                    System.out.println("Fuck up...");
                }
            }
        }).start();

    }

    public void send_str(String a){
        try{
            out = socket.getOutputStream();
            byte[] sendstr = new byte[21];
            System.arraycopy(a.getBytes(), 0, sendstr, 0, a.length());
            out.write(sendstr);
            clnt_send.setText(a);
            text = null;
        }
        catch(IOException e){}

    }

    public void send_bt(String b){
        try{
            out_bt = btSocket.getOutputStream();
            byte[] sendstr = new byte[21];
            System.arraycopy(b.getBytes(), 0, sendstr, 0, b.length());
            out_bt.write(sendstr);
            text = null;
        }
        catch(IOException e){}
    }

    //Initial bluetooth connection
    public void init_bt(){
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        mAdapter.enable();
        BluetoothDevice device = mAdapter.getRemoteDevice(bt);

        while(mAdapter == null);
        mAdapter.cancelDiscovery();

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
        } catch (IOException e) {}
    }

    public void init_layout() {
        //Initial the UI
        clnt_recv = (TextView) findViewById(R.id.client_receive);
        clnt_send = (TextView) findViewById(R.id.client_send);

        send_tx = (EditText) findViewById(R.id.send_text);
        send = (Button) findViewById(R.id.send_btn);

        bt_send_tx = (EditText) findViewById(R.id.bt_send_text);
        bt_send = (Button) findViewById(R.id.bt_btn);

        //Camera
        surfaceView = (SurfaceView)findViewById(R.id.camera_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(MainActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void start_camera()
    {
        if(!isPreview) {
            try {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } catch (RuntimeException e) {
                Log.e(tag, "open_camera: " + e);
                return;
            }
        }

        if(camera != null && !isPreview) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (Exception e) {
                Log.e(tag, "start_preview__camera: " + e);
                return;
            }

            camera.startPreview();
            isPreview = true;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try{
            if(camera != null){
                camera.setPreviewDisplay(holder);
            }
        }
        catch(Exception e){

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try{
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        catch(Exception e){

        }
    }

}
