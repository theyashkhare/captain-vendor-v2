package com.captainserve.captainvendor;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {


    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private  EditText mPhoneNumber;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private FirebaseAuth mAuth;
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mEmailView = findViewById(R.id.register_email);
        mPasswordView =  findViewById(R.id.m_password);
        mConfirmPasswordView =  findViewById(R.id.register_confirm_password);
        mUsernameView =  findViewById(R.id.register_username);
        mPhoneNumber = findViewById(R.id.register_phone);
        mImageView = findViewById(R.id.register_image);
        CheckBox checkbox = findViewById(R.id.password_check);
        Button login_screen = findViewById(R.id.reg);

        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });


        mAuth = FirebaseAuth.getInstance();


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConfirmPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    // hide password
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });


        mUsernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameView.setText("");
            }
        });

        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setText("");
            }
        });

        mConfirmPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                }
            }
        });
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                }
            }
        });

       login_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }


    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {


        mEmailView.setError(null);
        mPasswordView.setError(null);


        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phone = mPhoneNumber.getText().toString();
        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone) || !isPhoneValid(phone)) {
            mPhoneNumber.setError(getString(R.string.error_invalid_phoneno));
            focusView = mPhoneNumber;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {
            CreateFirebaseUSer();
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = mConfirmPasswordView.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4 ;
    }

    private boolean isPhoneValid(String phoneNo) {
        if (phoneNo.length()!=10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phoneNo).matches();
        }
    }


    private  void CreateFirebaseUSer(){
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FlashChat", "Done " + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.d("FlashChat", "Task not complete");
                    showErrorDialog("Registration Attempt Failed");
                }else{
                    saveDisplayName();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }


    private void saveDisplayName(){
        String displayName = mUsernameView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
    }


    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops").setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void ClearTextRegister(View view) {
        switch (view.getId()) {
            case R.id.register_phone:
                mPhoneNumber.getText().clear();
                break;
            case R.id.m_password:
                mPasswordView.getText().clear();
                break;

            case R.id.register_confirm_password:
                mConfirmPasswordView.getText().clear();
                break;
            default:
                break;
        }
    }
}

