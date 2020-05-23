package com.example.seeyousoon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeyousoon.retrofit.INodejs;
import com.example.seeyousoon.retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Signup extends AppCompatActivity {

    TextView loginBtn;
    EditText uEmail, uPassword,uConfirmPassword,uName;
    Button registerBtn;

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

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodejs.class);

        loginBtn =findViewById(R.id.goToLogin);
        uEmail = findViewById(R.id.uEmail);
        uName = findViewById(R.id.uName);
        uPassword = findViewById(R.id.uPassword);
        uConfirmPassword = findViewById(R.id.uConfirmPassword);
        registerBtn = findViewById(R.id.register);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        registerUser(uEmail.getText().toString(),uName.getText().toString(),uPassword.getText().toString());
                    }
                });
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
                    }
                })
        );
    }
}
