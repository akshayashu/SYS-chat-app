package com.example.seeyousoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeyousoon.data.UserData;
import com.example.seeyousoon.retrofit.INodejs;
import com.example.seeyousoon.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Signup extends AppCompatActivity {

    TextView loginBtn;
    EditText uEmail, uPassword,uConfirmPassword,uName;
    Button registerBtn;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    UserData userData;

    INodejs myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodejs.class);

        loginBtn =findViewById(R.id.goToLogin);
        uEmail = findViewById(R.id.uEmail);
        uName = findViewById(R.id.uName);
        uPassword = findViewById(R.id.uPassword);
        uConfirmPassword = findViewById(R.id.uConfirmPassword);
        registerBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar1);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String MY_PREFS_NAME = "MyPrefsFile";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", uName.getText().toString());
                editor.putString("about", "Hey! I'm new on SYS. ");
                editor.putInt("post", 7);
                editor.putInt("contact", 290);
                editor.putString("profileImageUrl","");
                editor.apply();

                if(TextUtils.isEmpty(uName.getText().toString())){
                    uName.setError("This field is empty!");
                    return;
                }
                if(TextUtils.isEmpty(uPassword.getText().toString())){
                    uPassword.setError("This field is empty!");
                    return;
                }
                if(TextUtils.isEmpty( uEmail.getText().toString())){
                    uEmail.setError("This field is empty!");
                    return;
                }
                if(TextUtils.isEmpty(uConfirmPassword.getText().toString())){
                    uConfirmPassword.setError("This field is empty!");
                    return;
                }
                if(!(uPassword.getText().toString()).equals(uConfirmPassword.getText().toString())){
                    uConfirmPassword.setError("Your password doesn't match!");
                    return;
                }
                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(uEmail.getText().toString(),uPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Signup.this, "Registered!", Toast.LENGTH_SHORT).show();
                                    saveUserInformation();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }else
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Signup.this, "Error! ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void saveUserInformation() {
        mRef = FirebaseDatabase.getInstance().getReference("User Data");
        userData = new UserData();
        userData.setEmail(uEmail.getText().toString());
        userData.setName(uName.getText().toString());
        userData.setUID(mAuth.getCurrentUser().getUid());
        userData.setPassword(uPassword.getText().toString());

        mRef.child(mAuth.getCurrentUser().getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Signup.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerUser(String email, String password,String name){
        compositeDisposable.add(myAPI.registerUser(email, password, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(Signup.this, ""+s, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                })
        );
    }
}
