package com.android.app.watersupply.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.bean.UserBean;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.android.app.watersupply.utils.Validation;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BackUp extends AppCompatActivity {
    @Bind(R.id.submit)
    Button mSignUp;
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.name)
    EditText mName;
    @Bind(R.id.shop_name)
    EditText mShop_name;
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
    @Bind(R.id.add_water)
    LinearLayout mAdd_water;
    @Bind(R.id.add_water_layout)
    LinearLayout mAdd_water_layout;
    @Bind(R.id.add_view)
    TextView mAdd_view;
    @Bind(R.id.remove_view)
    TextView mRemove_view;
    @Bind(R.id.delivery_distance)
    TextView mDeliveryDistance;
    @Bind(R.id.show_time)
    TextView mShowTime;
    @Bind(R.id.timeFrom)
    ImageView mTimeFrom;
    @Bind(R.id.timeTo)
    ImageView mTimeTo;@Bind(R.id.delivertimeLayout)
    LinearLayout mDelivertimeLayout;
    View mConvertView;
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
    List<String> typeRateList;
    String deliverTimeFrom="from";
    String deliverTimeTo="to";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back_up);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "Sign Up", true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        typeRateList = new ArrayList<>();
        dialog = new Dialog(BackUp.this);
        dialog();

        appUser = LocalRepositories.getAppUser(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering.. Please wait");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        try {
            mAddress.setText(ParameterConstants.ADDRESS);
        } catch (Exception e) {
        }
        customerView();
        ParameterConstants.KEY = "Customer";
        databaseReference = database.getReference(ParameterConstants.KEY);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.customer) {
                    ParameterConstants.KEY = "Customer";
                    databaseReference = database.getReference(ParameterConstants.KEY);
                    customerView();
                } else {
                    ParameterConstants.KEY = "Supplier";
                    databaseReference = database.getReference(ParameterConstants.KEY);
                    supplierView();
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeRateList.clear();
                if (!Helper.isNetworkAvailable(BackUp.this)) {
                    Snackbar.make(v, "Please check your network connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (!Validation.validateName(mName)) {
                    Snackbar.make(mainLayout, "Invalid Name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mShop_name.getVisibility() == View.VISIBLE && mShop_name.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Enter Shop Name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mShop_name.getVisibility() == View.VISIBLE && !Validation.validateName(mShop_name)) {
                    Snackbar.make(mainLayout, "Invalid SHop Name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!Validation.validateEmail(mEmail)) {
                    Snackbar.make(mainLayout, "Invalid Email", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!Validation.validatePassword(mPassword)) {
                    Snackbar.make(mainLayout, "Password should not be less than 6 digit", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!Validation.validateConfirmPassword(confirmPassword, mPassword)) {
                    Snackbar.make(mainLayout, "Password doesn't match", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mAddress.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Please Enter Your Address", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!Validation.validateMobile(mMobile)) {
                    Snackbar.make(mainLayout, "Invalid Mobile Number", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mCode.getVisibility() == View.VISIBLE && mCode.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Enter Provide Code", Snackbar.LENGTH_LONG).show();
                    return;
                }
                for (int i = 0; i < ((ViewGroup) mAdd_water).getChildCount(); i++) {
                    View view = ((ViewGroup) mAdd_water).getChildAt(i);
                    EditText waterType = ((EditText) view.findViewById(R.id.water_type));
                    EditText rate = ((EditText) view.findViewById(R.id.water_rate));
                    if (waterType.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Please Enter Water Type", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (rate.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Please Enter Rate", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    typeRateList.add(waterType.getText().toString() + "," + rate.getText().toString());
                }
                submitForm();
            }
        });
        mAdd_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView();
            }
        });
        mRemove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView();
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
                            Toast.makeText(BackUp.this, "Verification done", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            databaseReference.child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.dismiss();
                                        registerUser(userBean);
                                        appUser.user = userBean;
                                        LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                                      /*  Intent intent = new Intent(BackUp.this, CustomerHomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);*/
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(BackUp.this, "invalid opt", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void submitForm() {
        userBean = new UserBean();
        userBean.setName(mName.getText().toString());
        userBean.setEmail(mEmail.getText().toString());
        userBean.setMobile(mMobile.getText().toString());
        userBean.setPassword(mPassword.getText().toString());
        userBean.setAddress(mAddress.getText().toString().trim());
        userBean.setShopName(mShop_name.getText().toString().trim());
//        userBean.setItems(typeRateList);
        try {
            userBean.setLatitude(String.valueOf(ParameterConstants.location.getLatitude()));
            userBean.setLongitude(String.valueOf(ParameterConstants.location.getLongitude()));
        } catch (Exception e) {
        }
        if (mCode.getVisibility() == View.VISIBLE) {
            progressDialog.setMessage("Matching Supplier code");
            progressDialog.show();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ParameterConstants.SUPPLIER_CODE);
            ref.child(ParameterConstants.SUPPLIER_CODE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String code = dataSnapshot.getValue(String.class);
                    if (!code.equals(mCode.getText().toString())) {
                        progressDialog.dismiss();
                        Snackbar.make(mainLayout, "Invalid code", Snackbar.LENGTH_LONG).show();
                    } else {
                        startActivity(new Intent(getApplicationContext(),SignUp2Activity.class));
//                        retrieveKey(mMobile.getText().toString().trim());
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            retrieveKey(mMobile.getText().toString());
        }
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
                    Snackbar.make(mainLayout, "User with this Mobile number already registered, Please login", Snackbar.LENGTH_LONG).show();
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
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(BackUp.this, "verification done", Toast.LENGTH_LONG).show();

                databaseReference.child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            appUser.user = userBean;
                            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                           /* Intent intent = new Intent(BackUp.this, CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/
                            finish();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(BackUp.this, "" + e, Toast.LENGTH_SHORT).show();
                System.out.println("aaaaaaaaaaaaa  " + e);
                System.out.println("aaaaaaaaaaaaa  " + e.getMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(BackUp.this, "invalid mob no", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(BackUp.this, "quota over", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                Toast.makeText(BackUp.this, "Verification code sent", Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
                dialog.show();
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + userBean.getMobile(),             // Phone number to verify
                60,                      // Timeout duration
                TimeUnit.SECONDS,        // Unit of timeout
                BackUp.this,   // Activity (for callback binding)
                mCallbacks);         // OnVerificationStateChangedCallbacks
    }


    void dialog() {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);
        EditText otp = dialog.findViewById(R.id.otp);
        Button submit = (Button) dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().length() > 5) {
                    progressDialog.setMessage("Validation OTP...");
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(BackUp.this, "invalid OTP", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }


    void supplierView() {
        mDeliveryDistance.setVisibility(View.VISIBLE);
        mShowTime.setVisibility(View.VISIBLE);
        mDelivertimeLayout.setVisibility(View.VISIBLE);
        mCode.setVisibility(View.VISIBLE);
        mShop_name.setVisibility(View.VISIBLE);
        mAdd_water_layout.setVisibility(View.VISIBLE);
        mAdd_water.setVisibility(View.VISIBLE);
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        ((ViewGroup) mAdd_water).addView(mConvertView);
    }

    void customerView() {
        mDeliveryDistance.setVisibility(View.GONE);
        mShowTime.setVisibility(View.GONE);
        mDelivertimeLayout.setVisibility(View.GONE);
        mCode.setVisibility(View.GONE);
        mShop_name.setVisibility(View.GONE);
        mAdd_water_layout.setVisibility(View.GONE);
        mAdd_water.setVisibility(View.GONE);
        ((ViewGroup) mAdd_water).removeAllViews();
    }

    void addView() {
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
        editText.setText("");
        editText.setHint("Enter Water Type");
        ((ViewGroup) mAdd_water).addView(mConvertView);
    }

    void removeView() {
        if (((ViewGroup) mAdd_water).getChildCount() == 1) {
            Toast.makeText(this, "can't remove", Toast.LENGTH_SHORT).show();
            return;
        }
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
        editText.setText("");
        editText.setHint("Enter Water Type");
        ((ViewGroup) mAdd_water).removeViewAt(((ViewGroup) mAdd_water).getChildCount() - 1);
    }

    public void timeFrom(View view) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String am_pm = "";
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
//        .setText( strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm );
                        deliverTimeFrom= strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;
                        mShowTime.setText(deliverTimeFrom+" - "+deliverTimeTo);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void timeTo(View view) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String am_pm = "";
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
//        .setText( strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm );
                        deliverTimeTo= strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;
                        mShowTime.setText(deliverTimeFrom+" - "+deliverTimeTo);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


}
