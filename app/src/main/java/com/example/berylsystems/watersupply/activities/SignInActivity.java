package com.example.berylsystems.watersupply.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.example.berylsystems.watersupply.utils.Validation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.submit)
    Button mSignIn;
    @Bind(R.id.mobile)
    EditText mMobile;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.click_here)
    TextView mClickHere;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.customer)
    RadioButton mCustomer;
    @Bind(R.id.supplier)
    RadioButton mSupplier;

    AppUser appUser;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "Sign In", false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
        appUser = LocalRepositories.getAppUser(getApplicationContext());
        database = FirebaseDatabase.getInstance();
//        mMobile.setText("9794763878");
//        mPassword.setText("aaaaaa");
        mClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.userBean=null;
                ParameterConstants.isUpdate=false;
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
//                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity(intent);
            }
        });
        ParameterConstants.KEY = "Customer";
        databaseReference = database.getReference(ParameterConstants.KEY);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.customer) {
                    ParameterConstants.KEY = "Customer";
                    databaseReference = database.getReference(ParameterConstants.KEY);
                } else {
                    ParameterConstants.KEY = "Supplier";
                    databaseReference = database.getReference(ParameterConstants.KEY);
                }
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    Snackbar.make(mainLayout, "Please check your network connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (!Validation.validateMobile(mMobile)) {
                    return;
                }
                if (!Validation.validatePassword(mPassword)) {

                    return;
                }

                retrieveKey(mMobile.getText().toString());
            }
        });

    }


    private void retrieveKey(String mobile) {
        progressDialog.show();
        databaseReference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                UserBean userBean = dataSnapshot.getValue(UserBean.class);
                if (userBean == null) {
                    Snackbar.make(mainLayout, "Sorry you don't have " + ParameterConstants.KEY + " Account", Snackbar.LENGTH_LONG).show();
                } else {
                    if (!userBean.getPassword().equals(mPassword.getText().toString())) {
                        mPassword.setError("Wrong password");
                        mPassword.requestFocus();
                        return;
                    }
                    appUser.user = userBean;
                    LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                    if (ParameterConstants.KEY.contains(ParameterConstants.SUPPLIER)) {
                        Intent intent = new Intent(SignInActivity.this, SupplierHomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SignInActivity.this, CustomerHomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
