package com.example.maptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.odgnss.android.sdk.lib.client.RegistrationClient;
import com.odgnss.android.sdk.lib.data.LoginInfo;
import com.odgnss.android.sdk.lib.data.VehicleInfo;
import com.odgnss.android.sdk.lib.exception.InvalidImeiException;
import com.odgnss.android.sdk.lib.exception.InvalidSdkInitException;
import com.odgnss.android.sdk.lib.listener.RegistrationServiceListener;
import com.odgnss.android.sdk.lib.data.RegisterResponseData;
import com.odgnss.android.sdk.lib.log.Logger;
import com.odgnss.android.sdk.lib.utils.SetupUtil;

public class UserRegister extends Activity {

    // sdk
    private SetupUtil setupUtil;
    private RegistrationClient regClient;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(regClient!=null){
            regClient.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   // 呼叫父類別的onCreate，確保父類所做的初始化都有做到
        setContentView(R.layout.user_register); // 設定当前Activity (畫面) 的內容，内容是从layout中取得（.xml文件）

        // Initial the SDK Registration client object
        setupUtil = new SetupUtil(this);
        regClient = new RegistrationClient(this);

        final EditText txtRegName = findViewById(R.id.txtRegUsername);
        final EditText txtPwd = findViewById(R.id.txtRegPassword);
        final EditText txtConfirmPwd = findViewById(R.id.txtRegConfirmPsw);
        final EditText txtRegKey = findViewById(R.id.txtRegkey);
        final EditText txtRegId = findViewById(R.id.txtRegid);
        final EditText txtRegMobileName = findViewById(R.id.txtRegmobile);
        final EditText txtRegVehicleNo = findViewById(R.id.txtVehicleNo);
        final EditText txtRegVehicleModel = findViewById(R.id.txtVehicleModel);
        final EditText txtRegVehicleDriver = findViewById(R.id.txtVehicleDriver);


        regClient.setListener(new RegistrationServiceListener() {

            @Override
            public void onErrorResponse(int err, String errorMessage) {
                Logger.Log("Network Error Code: " + err + ", Message: " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(RegisterResponseData registerResponseData) {
                Intent mainIntent = new Intent(getBaseContext(),  UserLogin.class);
                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                // Quit registration
                UserRegister.this.finish();
                // Launch Main screen
                startActivity(mainIntent);
            }

        });

        Button buttonRegister = findViewById(R.id.register_button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtRegName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    setupUtil.setupConnection("http://61.92.168.2:9082", "JTFwsLRMg5U2aeMAuYCjdWEuN7INyD2pSVEaNknw");
                    setupUtil.setupAppId("hohocaredev");

                    String loginName = txtRegName.getText().toString().trim();
                    String password = txtPwd.getText().toString().trim();
                    String registrationKey = txtRegKey.getText().toString().trim();
                    String mobileName = txtRegMobileName.getText().toString().trim();
                    String staffId = txtRegId.getText().toString().trim();

                    VehicleInfo vehicleInfo = new VehicleInfo();
                    vehicleInfo.setNo(txtRegVehicleNo.getText().toString().trim());
                    vehicleInfo.setModel(txtRegVehicleModel.getText().toString().trim());
                    vehicleInfo.setDriver(txtRegVehicleDriver.getText().toString().trim());

                    LoginInfo loginInfo = new LoginInfo();
                    loginInfo.setName(loginName);
                    loginInfo.setPwd(password);

                    try {
                        String appVersion = CommonUtil.getAppVersionName(UserRegister.this);
                        regClient.sendHttpRegistration(registrationKey, mobileName, staffId, loginInfo, vehicleInfo, appVersion, null);

                    } catch (InvalidImeiException ex) {
                        Toast.makeText(getApplicationContext(), "Invalid IMEI", Toast.LENGTH_LONG).show();
                    } catch (InvalidSdkInitException ex) {
                        Toast.makeText(getApplicationContext(), "Invalid Setup Info", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Registration Fail", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
