package com.example.jssmile.thread_test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

    String ipname = null;
    String btname = null;
    int resolution = 10;
    SharedPreferences pre_setting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Layout Setting
        Button btn_Login = (Button) findViewById(R.id.Login);
        Button btn_Cancel = (Button) findViewById(R.id.Cancel);
        final EditText ip_name = (EditText) findViewById(R.id.ip_name);
        final EditText bt_name = (EditText) findViewById(R.id.bt_name);

        pre_setting = getSharedPreferences("Pref_name", 0);
        ipname = pre_setting.getString("ipname","140.116.164.7");
        btname = pre_setting.getString("btname","98:D3:31:20:23:D1");

        ip_name.setText(ipname);
        bt_name.setText(btname);
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save the parameter which we set last time
                ipname = ip_name.getText().toString().trim();
                btname = bt_name.getText().toString().trim();
                pre_setting.edit().putString("ipname",ipname).commit();
                pre_setting.edit().putString("btname",btname).commit();

                //Parameters saved in different activity
                Bundle data = new Bundle();
                data.putString("ipname",ipname);
                data.putString("btname",btname);

                //Change the page
                Intent intent= new Intent(Login.this,MainActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
}
