package com.sandwhich.tuna.projectlucidity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sandwhich.tuna.projectlucidity.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    ProgressDialog pd;
    String userEmailVal;
    final String EMAIL="email";
    final String PASSWORD="password";
    String userPasswordVal,userPasswordValConfirm;
    TextInputEditText userEmail,userPassword,userPasswordConfirmed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        initUI();
    }
    private boolean isValid(String content, String type){
        if (type.equals(EMAIL)){
            if (content.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
                return true;
            }
            else{
                return false;
            }
        }
        else if (type.equals(PASSWORD)){
            if (content.matches("^\\w{8,18}$")){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }

    }
    private void initUI() {
        pd = new ProgressDialog(SignUpActivity.this);
        pd.setTitle("Signing you up");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        userPasswordConfirmed = findViewById(R.id.user_passwordConfirm);
        findViewById(R.id.submit_credential).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        userEmailVal = userEmail.getText().toString();
        userPasswordVal = userPassword.getText().toString();
        userPasswordValConfirm = userPasswordConfirmed.getText().toString();
        if((userPasswordVal.equals(userPasswordValConfirm))){
            if (isValid(userEmailVal,EMAIL) && isValid(userPasswordValConfirm,PASSWORD)){
                //good to go for account creation
                pd.show();
                mAuth.createUserWithEmailAndPassword(userEmailVal,userPasswordValConfirm).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //user has been created, send to main activity
                            if (pd.isShowing()){
                                pd.dismiss();
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("newuser",true);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("newuser",true);
                                startActivity(intent);
                            }

                        }
                        else{
                            if (pd.isShowing()){
                                pd.dismiss();
                            }
                            Toast.makeText(SignUpActivity.this,"Failed to sign-up, please try again",Toast.LENGTH_SHORT).show();
                            //failed to create user at server end, listen for specific error
                            Log.i("Failed sign-in",task.getException().getMessage());
                        }
                    }
                });
            }
            else{
                //invalid user credentials on client end
                if (pd.isShowing()) pd.dismiss();

                if (!isValid(userEmailVal,EMAIL)){
                    Toast.makeText(SignUpActivity.this,"Entered email is not valid.",Toast.LENGTH_SHORT).show();
                }
                else if(!isValid(userPasswordValConfirm,PASSWORD)){
                    Toast.makeText(SignUpActivity.this,"Entered password is too short or too long.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Entered credentials are not valid.",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            if (pd.isShowing()) pd.dismiss();
            Toast.makeText(SignUpActivity.this,"Passwords entered don't match.",Toast.LENGTH_SHORT).show();
        }
    }
}
