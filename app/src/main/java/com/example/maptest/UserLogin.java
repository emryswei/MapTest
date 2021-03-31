package com.example.maptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.odgnss.android.sdk.lib.client.LoginClient;
import com.odgnss.android.sdk.lib.data.LoginInfo;
import com.odgnss.android.sdk.lib.data.LoginResponseData;
import com.odgnss.android.sdk.lib.exception.InvalidImeiException;
import com.odgnss.android.sdk.lib.exception.InvalidSdkInitException;
import com.odgnss.android.sdk.lib.listener.LoginServiceListener;
import com.odgnss.android.sdk.lib.log.Logger;
import com.odgnss.android.sdk.lib.utils.SetupUtil;


public class UserLogin extends Activity {
    // SDK
    private LoginClient loginClient;
    private SetupUtil setupUtil;
    private Button buttonOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        // Initial the SDK Login client object
        setupUtil = new SetupUtil(this);
        loginClient = new LoginClient(this);


        final EditText txtUsername = findViewById(R.id.txtLoginUsername);
        final EditText txtPwd = findViewById(R.id.txtLoginPassword);
        Button txtToregister = findViewById(R.id.goToRegister);

        loginClient.setListener(new LoginServiceListener() {
            @Override
            public void onResponse(LoginResponseData loginResponseData) {
                buttonOK.setEnabled(true);
                if (loginResponseData==null) return;
                if (loginResponseData.getConfigInfo()!=null) {
                    String log = "Foreground: " + loginResponseData.getConfigInfo().getLocation().getInterval().getForeground();
                    log += ", Background: " + loginResponseData.getConfigInfo().getLocation().getInterval().getBackground();
                    log += ", Hibernate: " + loginResponseData.getConfigInfo().getLocation().getInterval().getHibernate();
                    log += ", Config: " + loginResponseData.getConfigInfo().getConfig().getInterval();
                    Logger.Log(log);
                }
                Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                launchMainActivity();
            }

            @Override
            public void onErrorResponse(int errorCode, String errorMessage) {
                buttonOK.setEnabled(true);
                Logger.Log("Network Error Code: " + errorCode + ", Message: " + errorMessage);
                Toast.makeText(getApplicationContext(), "Server Return: [" + errorCode + "] " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        buttonOK = (Button) findViewById(R.id.login_button);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupUtil.setupConnection("http://61.92.168.2:9082", "JTFwsLRMg5U2aeMAuYCjdWEuN7INyD2pSVEaNknw");
                setupUtil.setupAppId("hohocaredev");

                LoginInfo loginInfo = new LoginInfo();

                loginInfo.setName(txtUsername.getText().toString().trim());
                loginInfo.setPwd(txtPwd.getText().toString().trim());

                try {
                    // Developer may want to know the library requesting URL
                    if (loginClient.getRequestUrl() != null) Logger.Log(loginClient.getRequestUrl().toString());
                    String appVersion = CommonUtil.getAppVersionName(UserLogin.this);
                    loginClient.sendHttpLogin(loginInfo,appVersion,null);
                    // Prevent user click multiple times if network condition not good
                    buttonOK.setEnabled(false);
                } catch (InvalidSdkInitException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid SDK Info", Toast.LENGTH_LONG).show();
                } catch (InvalidImeiException e) {
                    Toast.makeText(getApplicationContext(), "Invalid IMEI", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Login Fail", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Registration button
        txtToregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegistration = new Intent(getBaseContext(), UserRegister.class);
                startActivity(toRegistration);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release Login client object
        if (loginClient != null) loginClient.release();
    }

    private void launchMainActivity() {
        Intent mainIntent = new Intent(getBaseContext(),  MainActivity.class);
        // Quit login screen
        UserLogin.this.finish();
        // Launch Main screen
        startActivity(mainIntent);
    }

}

