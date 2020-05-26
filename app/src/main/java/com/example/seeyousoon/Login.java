package com.example.seeyousoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    TextView registerBtn;
    EditText uEmail, uPassword;
    Button loginBtn;
    ProgressBar progressBar;

    UserData userData;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodejs.class);

        registerBtn = findViewById(R.id.goToRegister);
        uEmail = findViewById(R.id.eEmail);
        uPassword = findViewById(R.id.ePassword);
        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Signup.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(uEmail.getText().toString(),uPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            setDetails();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setDetails(){
        userData = new UserData();
        databaseReference = FirebaseDatabase.getInstance().getReference("User Data").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);
                try{
                    final String MY_PREFS_NAME = "MyPrefsFile";
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("name", userData.getName());
                    editor.putString("about", "Hey! I'm new on SYS. ");
                    editor.putInt("post", 7);
                    editor.putInt("contact", 290);
                    editor.apply();

                }catch (Exception e){
                    Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginUser(String email, String password) {
        compositeDisposable.add(myAPI.loginUser(email, password)
        .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains("password")){
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this, "Login Unsuccessful   "+s, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }
}
