package com.example.newgymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ModifyEmail extends AppCompatActivity implements TextWatcher {

    private TextView currentEmailField;
    private EditText updateEmailField;
    private Button btnDone;
    private ImageView btnBack;

    private DatabaseReference dbReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_email);


        currentEmailField = findViewById(R.id.current_email_modify_email_view);
        updateEmailField = findViewById(R.id.emailText_modify_view);
        btnDone = findViewById(R.id.done_button_modify_email);
        btnBack = findViewById(R.id.back_to_profile_f_email);
        btnDone.setEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").child(user.getUid()).child("email");

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCurrentEmailField();

        updateEmailField.addTextChangedListener(this);

        btnDone.setEnabled(false);
        btnDone.setOnClickListener(view -> changeEmailFirebase());

        btnBack.setOnClickListener(view -> startActivity(new Intent(ModifyEmail.this, ProfileActivity.class)));
    }


    public void updateCurrentEmailField(){
        assert user != null;
        String currentEmail = user.getEmail();
        String formattedString = "Your current email is " + "<b>" + currentEmail + "</b>" + ".\n To change it, fill in the field below!";
        currentEmailField.setText(Html.fromHtml(formattedString));
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void changeEmailFirebase(){
        assert user != null;
        user.updateEmail(updateEmailField.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                dbReference.setValue(updateEmailField.getText().toString());
                startActivity(new Intent(ModifyEmail.this, ProfileActivity.class));
            }
            else {
                Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Check if email field contains @ character
        String emailInput = charSequence.toString();
        if(isValidEmail(emailInput)){
            btnDone.setTextColor(getResources().getColor(R.color.orange));
            btnDone.setEnabled(true);
        }
        else{
            btnDone.setTextColor(getResources().getColor(R.color.custom_gray));
            btnDone.setEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}