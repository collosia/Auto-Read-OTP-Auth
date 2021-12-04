package com.example.otpauth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authentication extends AppCompatActivity {

    EditText contCode,phoneNumber,enterOTP;
    Button sendBtn,verifyBtn,resendBtn;
    String userPhoneNumber,verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    FirebaseAuth fAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(Authentication.this);
        setContentView(R.layout.activity_authentication);

        FirebaseApp.initializeApp(Authentication.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        contCode= findViewById(R.id.contCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        sendBtn = findViewById(R.id.sendBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        resendBtn = findViewById(R.id.resendBtn);
        enterOTP = findViewById(R.id.enterOtp);


        fAuth = FirebaseAuth.getInstance();



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contCode.getText().toString().isEmpty()){
                     contCode.setError("Required");
                     return;
                }

                if(phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setError("Phone Number is Required");
                    return;
                }
                userPhoneNumber = "+"+ contCode.getText().toString()+phoneNumber.getText().toString();
                verifyPhoneNumber(userPhoneNumber);
                Toast.makeText(Authentication.this, userPhoneNumber, Toast.LENGTH_SHORT).show();
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(enterOTP.getText().toString().isEmpty()){
                    enterOTP.setError("Enter OTP first");
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enterOTP.getText().toString());
                authenticationUser(credential);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticationUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Authentication.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;

                contCode.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);

                enterOTP.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);
                resendBtn.setVisibility(View.VISIBLE);
                resendBtn.setEnabled(true);



            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
               resendBtn.setEnabled(false);

            }
        };

    }

    public void verifyPhoneNumber(String phoneNum){
        //send OTP
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();


        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public void authenticationUser(PhoneAuthCredential credential){
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Authentication.this, "Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Authentication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}