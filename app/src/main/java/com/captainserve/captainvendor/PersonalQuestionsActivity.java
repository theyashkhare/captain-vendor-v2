package com.captainserve.captainvendor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PersonalQuestionsActivity extends AppCompatActivity {

    Button next_btn;
    private AutoCompleteTextView ques_name, ques_email, ques_address;
    private EditText ques_no, ques_dob, ques_adhaar, ques_pan;
    private Spinner gender_spinner, services_spinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int mYear, mDay, mMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.p_info_fragment);
        next_btn = findViewById(R.id.fill_button);
        ques_name = findViewById(R.id.quesPage_name);
        ques_email = findViewById(R.id.quesPage_email);
        ques_address = findViewById(R.id.quesPage_add);
        ques_no = findViewById(R.id.quesPage_no);
        ques_dob = findViewById(R.id.quesPage_DOB);
        ques_adhaar = findViewById(R.id.quesPage_adhaar);
        ques_pan = findViewById(R.id.quesPage_pan);
        gender_spinner = findViewById(R.id.quesPage_sex);
        services_spinner = findViewById(R.id.ques_services);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ques_dob.setShowSoftInputOnFocus(false);
        }

    /*    ques_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(PersonalQuestionsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                        ques_dob.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                //mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });
        */
        ques_dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar mcurrentDate = Calendar.getInstance();
                    mYear = mcurrentDate.get(Calendar.YEAR);
                    mMonth = mcurrentDate.get(Calendar.MONTH);
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog mDatePicker = new DatePickerDialog(PersonalQuestionsActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            Calendar myCalendar = Calendar.getInstance();
                            myCalendar.set(Calendar.YEAR, selectedyear);
                            myCalendar.set(Calendar.MONTH, selectedmonth);
                            myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                            String myFormat = "dd/MM/yy"; //Change as you need
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                            ques_dob.setText(sdf.format(myCalendar.getTime()));
                            mDay = selectedday;
                            mMonth = selectedmonth;
                            mYear = selectedyear;
                        }
                    }, mYear, mMonth, mDay);
                    //mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                }
            }
        });


        ques_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ques_name.setText("");
            }
        });
        ques_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ques_email.setText("");
            }
        });
        ques_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ques_address.setText("");
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = ques_name.getText().toString();
                String email = ques_email.getText().toString();
                String address = ques_address.getText().toString();
                String number = ques_no.getText().toString();
                String doB = ques_dob.getText().toString();
                String adhaar = ques_adhaar.getText().toString();
                String pan = ques_pan.getText().toString();
                String sex = gender_spinner.getSelectedItem().toString();
                String service = services_spinner.getSelectedItem().toString();

                if(adhaar.equals("")){
                    adhaar = "00";
                }

                if(pan.equals("")){
                    pan = "00";
                }

                Map<String, Object> data = new HashMap<>();
                data.put("Name", name);
                data.put("Email", email);
                data.put("Address", address);
                data.put("Phone Number", number);
                data.put("Gender", sex);
                data.put("Date of Birth", doB);
                data.put("Adhaar Card Number", adhaar);
                data.put("Pan Card Number", pan);
                data.put("Sex", sex);
                data.put("Service", service);


                if (!name.equals("") && !email.equals("") && !number.equals("") && !doB.equals("") && !address.equals("")  && !sex.equals("Gender") && !service.equals("Services")){
                    db.collection("vendors").document(name).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String name = ques_name.getText().toString();
                                Intent intent = new Intent(PersonalQuestionsActivity.this, FinalQuestionsActivity.class);
                                intent.putExtra("Name", name);
                                startActivity(intent);
                                Toast.makeText(PersonalQuestionsActivity.this, "Submission Complete", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PersonalQuestionsActivity.this, "Please fill in the details to proceed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}