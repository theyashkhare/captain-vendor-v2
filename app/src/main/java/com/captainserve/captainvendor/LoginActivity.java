package com.captainserve.captainvendor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button register_screen, login_start;
    SignInButton googleSignInButton;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth mAuth;
    CheckBox checkbox;
    GoogleSignInClient googleSignInClient;
    String TAG = "GOOGLELOGIN";
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        register_screen = findViewById(R.id.reg);
        mImageView = findViewById(R.id.login_image);
        login_start = findViewById(R.id.login_btn);
        mEmailView = findViewById(R.id.register_email);
        mPasswordView = findViewById(R.id.m_password);
        checkbox = findViewById(R.id.password_check);
        googleSignInButton = findViewById(R.id.google_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.getText().clear();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
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

        mAuth = FirebaseAuth.getInstance();

        register_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });


    }


    public void ClearTextLogin(View view) {
        switch (view.getId()) {
            case R.id.register_phone:
                mEmailView.getText().clear();
                break;
            case R.id.m_password:
                mPasswordView.getText().clear();
                break;
            default:
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code= " + e.getStatusCode());
                    }
                    break;
            }
    }


    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, PersonalQuestionsActivity.class);
        startActivity(intent);
        finish();
    }


    public void signInExistingUser(View v) {
        attemptLogin();

    }

    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.captainserve.captainvendor.RegisterActivity.class);
        finish();
        startActivity(intent);
    }


    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (password.equals("") || email.equals("")) return;
        Toast.makeText(this, "Login in Progress", Toast.LENGTH_SHORT).show();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("LIN", "onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("LIN", "Nope, Not done.  " + task.getException().toString());
                    showErrorDialog("Problem Signing in");
                } else {

                    SharedPreferences FormVerifier = getSharedPreferences("checkbox", 0);
                    boolean formDone = FormVerifier.getBoolean("formDone", false);

                    if (formDone) {
                        Intent intent = new Intent(LoginActivity.this, ThankYouActivity.class);
                        finish();
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, PersonalQuestionsActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            }
        });

    }


    private void showErrorDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Oops").setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }


}
