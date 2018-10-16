package com.example.berylsystems.watersupply.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.fragment.supplier.DeliveredOrderFragment;
import com.example.berylsystems.watersupply.fragment.supplier.PendingOrderFragment;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.MyLocationListener;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.example.berylsystems.watersupply.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    @Bind(R.id.submit)
    Button mSignUp;
    @Bind(R.id.verifyCode)
    Button mVerifyCode;
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.name)
    EditText mName;
    @Bind(R.id.email)
    EditText mEmail;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.confirmPassword)
    EditText confirmPassword;
    @Bind(R.id.mobile)
    EditText mMobile;
    @Bind(R.id.address)
    EditText mAddress;
    @Bind(R.id.code)
    EditText mCode;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.customer)
    RadioButton mCustomer;
    @Bind(R.id.supplier)
    RadioButton mSupplier;
    @Bind(R.id.supplierLayout)
    LinearLayout supplierLayout;
    @Bind(R.id.customerLayout)
    LinearLayout customerLayout;
    @Bind(R.id.terms)
    LinearLayout terms;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static UserBean userBean;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Dialog dialog;
    AppUser appUser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "Sign Up", true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        appUser = LocalRepositories.getAppUser(this);
        if (ParameterConstants.isUpdate) {
            radioGroup.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
            mMobile.setEnabled(false);
            userBean = appUser.user;
            mSignUp.setText("Update Account");
        } else {
            radioGroup.setVisibility(View.VISIBLE);
            terms.setVisibility(View.VISIBLE);
            mMobile.setEnabled(true);
            if (userBean == null) {
                userBean = new UserBean();
            }
        }
//        try {
//            mAddress.setText(ParameterConstants.ADDRESS);
//        } catch (Exception e) {
//        }
        if (userBean.getName() != null) {
            mName.setText(userBean.getName());
        }
        if (userBean.getEmail() != null) {
            mEmail.setText(userBean.getEmail());
        }
        if (userBean.getPassword() != null) {
            mPassword.setText(userBean.getPassword());
            confirmPassword.setText(userBean.getPassword());
        }
        if (userBean.getAddress() != null) {
            mAddress.setText(userBean.getAddress());
        }
        if (userBean.getMobile() != null) {
            mMobile.setText(userBean.getMobile());
        }

        dialog = new Dialog(SignUpActivity.this);
        otpDialog();
        buildAlertMessageNoGps();
        appUser = LocalRepositories.getAppUser(this);
        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering.. Please wait");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ParameterConstants.KEY = "Customer";
        databaseReference = database.getReference("Customer");
        customerLayout.setVisibility(View.VISIBLE);
        supplierLayout.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.customer) {
                    customerLayout.setVisibility(View.VISIBLE);
                    supplierLayout.setVisibility(View.GONE);
                    ParameterConstants.KEY = "Customer";
                    userBean.setUserType(ParameterConstants.KEY);
                } else {
                    customerLayout.setVisibility(View.GONE);
                    supplierLayout.setVisibility(View.VISIBLE);
                    ParameterConstants.KEY = "Supplier";
                    userBean.setUserType(ParameterConstants.KEY);
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(SignUpActivity.this)) {
                    Snackbar.make(v, "Please check your network connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (ParameterConstants.KEY.equals("Customer")) {
                    if (!Validation.validateName(mName)) {
                        return;
                    }

                    if (!Validation.validateEmail(mEmail)) {
                        return;
                    }
                    if (!Validation.validatePassword(mPassword)) {
                        return;
                    }
                    if (!Validation.validateConfirmPassword(confirmPassword, mPassword)) {
                        return;
                    }
                    if (mAddress.getText().toString().isEmpty()) {
                        mAddress.setError("Please Enter Your Complete Address");
                        mAddress.requestFocus();
                        return;
                    }
                    if (!Validation.validateMobile(mMobile)) {
                        return;
                    }
                    if (ParameterConstants.location == null) {
                        Snackbar.make(mainLayout, "Location not found", Snackbar.LENGTH_LONG).show();
                        progressDialog.setMessage("Getting Location...");
                        progressDialog.show();
                        new MyLocationListener(getApplicationContext(),progressDialog);
                        return;
                    }
                } else {
                    if (mCode.getText().toString().isEmpty()) {
                        mCode.setError("Enter Supplier Code");
                        mCode.requestFocus();
                        return;
                    }
                }

                userBean = new UserBean();
                userBean.setName(mName.getText().toString());
                userBean.setEmail(mEmail.getText().toString());
                userBean.setMobile(mMobile.getText().toString());
                userBean.setPassword(mPassword.getText().toString());
                userBean.setAddress(mAddress.getText().toString().trim());
                userBean.setRefreshToken(FirebaseInstanceId.getInstance().getToken());
                if (!ParameterConstants.isUpdate) {
                    retrieveKey(mMobile.getText().toString());
                } else {
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setTitle("Update Location")
                            .setMessage("Would you like to update location as well ?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> {
                                insertUpdate(Double.valueOf(appUser.user.getLatitude()), Double.valueOf(appUser.user.getLongitude()));
                            }).show();
                }
            }
        });

        mVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Helper.isNetworkAvailable(SignUpActivity.this)) {
                    Snackbar.make(view, "Please check your network connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (mCode.getText().toString().isEmpty()) {
                    mCode.setError("Enter Code");
                    mCode.requestFocus();
                    return;
                }
                progressDialog.setMessage("Matching Supplier code");
                progressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ParameterConstants.SUPPLIER_CODE);
                ref.child(ParameterConstants.SUPPLIER_CODE).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        String code = dataSnapshot.getValue(String.class);
                        if (!code.equals(mCode.getText().toString())) {
                            mCode.setError("Invalid Code");
                            mCode.requestFocus();
                        } else {
                            startActivity(new Intent(getApplicationContext(), SignUp2Activity.class));
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
//                          Auto Verified here
                            Toast.makeText(SignUpActivity.this, "Verification done", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUpActivity.this, "invalid opt", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void retrieveKey(String mobile) {
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        databaseReference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserBean bean = dataSnapshot.getValue(UserBean.class);
                if (bean == null) {
                    registerUser(userBean);
                } else {
                    progressDialog.dismiss();
                    mMobile.setError("User with this Mobile number already registered, Please login");
                    mMobile.requestFocus();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void registerUser(UserBean userBean) {

//have to remove
        insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());


//have to uncomment for otp


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(SignUpActivity.this, "verification done", Toast.LENGTH_LONG).show();
//                insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                progressDialog.dismiss();
//                Toast.makeText(SignUpActivity.this, "" + e, Toast.LENGTH_SHORT).show();
//                System.out.println("aaaaaaaaaaaaa  " + e);
//                System.out.println("aaaaaaaaaaaaa  " + e.getMessage());
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    Toast.makeText(SignUpActivity.this, "invalid mob no", Toast.LENGTH_LONG).show();
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    Toast.makeText(SignUpActivity.this, "quota over", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCodeSent(String verificationId,
//                                   PhoneAuthProvider.ForceResendingToken token) {
//                progressDialog.dismiss();
//                Toast.makeText(SignUpActivity.this, "Verification code sent", Toast.LENGTH_LONG).show();
//                mVerificationId = verificationId;
//                mResendToken = token;
//                dialog.show();
//            }
//        };
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+91" + userBean.getMobile(),             // Phone number to verify
//                60,                      // Timeout duration
//                TimeUnit.SECONDS,        // Unit of timeout
//                SignUpActivity.this,   // Activity (for callback binding)
//                mCallbacks);         // OnVerificationStateChangedCallbacks
    }


    void otpDialog() {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);
        EditText otp = dialog.findViewById(R.id.otp);
        Button submit = (Button) dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().length() > 5) {
                    progressDialog.setMessage("Validating OTP...");
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(SignUpActivity.this, "invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // set the custom dialog components - text, image and button
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
    }

    private void buildAlertMessageNoGps() {
        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            @SuppressLint("RestrictedApi") final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }


    void insertUpdate(Double latitude, Double longitude) {
        userBean.setUserType(ParameterConstants.KEY);
        userBean.setLatitude("" + latitude);
        userBean.setLongitude("" + longitude);
        databaseReference.child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    if (ParameterConstants.isUpdate) {
                        Toast.makeText(SignUpActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    appUser.user = userBean;
                    LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                    Intent intent = new Intent(SignUpActivity.this, CustomerHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }


}
