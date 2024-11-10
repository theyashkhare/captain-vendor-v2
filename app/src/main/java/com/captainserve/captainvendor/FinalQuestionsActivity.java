package com.captainserve.captainvendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FinalQuestionsActivity extends AppCompatActivity {

    Button fill_btn;
    private AutoCompleteTextView ques_ac_name;
    private EditText ques_bankNo, ques_IFSC;
    private Spinner bank_name;
    CheckBox mCheckBox;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean form_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_info_fragment);

        ques_bankNo = findViewById(R.id.quesPage_bankAcc);
        ques_IFSC = findViewById(R.id.quesPage_ifsc);
        bank_name = findViewById(R.id.bank_name);
        ques_ac_name = findViewById(R.id.quesPage_name);
        mCheckBox = findViewById(R.id.mcheckBox);

        fill_btn = findViewById(R.id.fill_button);
        ques_ac_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ques_ac_name.setText("");
            }
        });

        Intent i = getIntent();
        final String name = i.getStringExtra("Name");

        fill_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ac_name = ques_ac_name.getText().toString();
                String bankNumber = ques_bankNo.getText().toString();
                String ifsc = ques_IFSC.getText().toString();
                String ques_bankName = bank_name.getSelectedItem().toString();

                if (ques_bankName.equals("Select Bank")) {
                    ques_bankName = "Not Selected";
                }

                if (ac_name.equals("")) {
                    ac_name = "ND";
                }
                Map<String, Object> data = new HashMap<>();
                data.put("Account Name", ac_name);
                data.put("Bank Name", ques_bankName);
                data.put("Bank Account Number", bankNumber);
                data.put("IFSC", ifsc);


                if (!name.equals("") && mCheckBox.isChecked()) {
                    db.collection("vendors").document(name).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(FinalQuestionsActivity.this, ThankYouActivity.class);
                                startActivity(intent);
                                SharedPreferences FormVerifier = getSharedPreferences("checkbox", MODE_PRIVATE);
                                SharedPreferences.Editor editor = FormVerifier.edit();
                                editor.putBoolean("formDone", true);
                                editor.apply();
                            } else {
                                Log.d("dataPacket", "onComplete: Not Successful");
                            }
                        }
                    });
                } else {
                    Toast.makeText(FinalQuestionsActivity.this, "Please Accept the Terms and Conditions", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

