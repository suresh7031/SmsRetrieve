package com.tech2020.smsretrieve;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MySMSBroadcastReceiver.OTPReceiveListener {

    private static final int RESOLVE_HINT = 2;
    private String TAG="flow";
    private String TAG1="flow";
    private GoogleApiClient apiClient;
    TextView textView;
    EditText phoneView;
    Button btn_resend;
    Button btn_verify;
    MySMSBroadcastReceiver mySMSBroadcastReceiver;
    private GoogleApiClient mCredentialsApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");
        textView= findViewById(R.id.code);
        phoneView = findViewById(R.id.phonenumber);
        btn_resend = findViewById(R.id.resend);
        btn_verify = findViewById(R.id.btn_verify);
        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reSendCode(view);
            }
        });
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode(view);
            }
        });

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        // This code requires one time to get Hash keys do comment and share key
        Log.d("hash", "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));

        mCredentialsApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        requestHint();
        startSmsListener();

        String sample="<#> code: 12345678 \n" +
                "    xl3ZdlY/chY";
        Log.i("hash", "onCreate: "+parseCode(sample));

    }

    // Construct a request for phone numbers and show the picker
    private void requestHint() {
        Log.i(TAG, "requestHint: ");
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    public void reSendCode(View view){
        //send request to server for otp
        Log.i(TAG, "reSendCode: ");

    }
    
    public void verifyCode(View view){
        Log.i(TAG, "verifyCode: ");
    }

    public void startSmsListener(){
        Log.i(TAG, "startSmsListener: ");
        mySMSBroadcastReceiver=new MySMSBroadcastReceiver();
        mySMSBroadcastReceiver.setOTPListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        this.registerReceiver(mySMSBroadcastReceiver, intentFilter);

        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                Log.i("hash", "onSuccess: ");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }


    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
                Log.i("hash", "onActivityResult: "+credential.getId());
                phoneView.setText(credential.getId());
            }else if (resultCode == RESULT_CANCELED){
                Log.i("hash", "onActivityResult: cancelled");
            }
        }
    }

    private String parseCode(String message) {
        Log.i(TAG, "parseCode: "+message);
        Pattern p = Pattern.compile("\\b\\d{8}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
            Log.i("hash", "parseCode: "+code);
        }
        return code;
    }


    @Override
    public void onOTPReceived(String otp) {
        Log.i(TAG, "onOTPReceived: "+otp);
        Toast.makeText(this, "otp: "+otp, Toast.LENGTH_SHORT).show();
        String code=parseCode(otp);
        Log.i(TAG, "onOTPReceived: code "+code);
        textView.setText(code);
    }

    @Override
    public void onOTPTimeOut() {
        Log.i(TAG, "onOTPTimeOut: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if ( mySMSBroadcastReceiver!= null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mySMSBroadcastReceiver);
        }
    }
}
