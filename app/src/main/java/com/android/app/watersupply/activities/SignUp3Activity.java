package com.android.app.watersupply.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.supplier.SupplierHomeActivity;
import com.android.app.watersupply.bean.Item;
import com.android.app.watersupply.bean.UserBean;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.MyLocationListener;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp3Activity extends AppCompatActivity {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.deliveryDistance)
    EditText deliveryDistance;
    @Bind(R.id.add_water)
    LinearLayout mAdd_water;
    @Bind(R.id.add_view)
    TextView mAdd_view;
    @Bind(R.id.remove_view)
    TextView mRemove_view;
    @Bind(R.id.openBooking)
    TextView openBooking;
    @Bind(R.id.openBookingIcon)
    ImageView openBookingIcon;
    @Bind(R.id.closeBooking)
    TextView closeBooking;
    @Bind(R.id.closeBookingIcon)
    ImageView closeBookingIcon;
    @Bind(R.id.deliveryTime)
    Spinner deliveryTime;
    @Bind(R.id.sunday)
    CheckBox sunday;
    @Bind(R.id.monday)
    CheckBox monday;
    @Bind(R.id.tuesday)
    CheckBox tuesday;
    @Bind(R.id.wednesday)
    CheckBox wednesday;
    @Bind(R.id.thursday)
    CheckBox thursday;
    @Bind(R.id.friday)
    CheckBox friday;
    @Bind(R.id.saturday)
    CheckBox saturday;

    @Bind(R.id.time_picker)
    TimePicker mTimePicker;
    @Bind(R.id.terms)
    LinearLayout terms;
//    @Bind(R.id.mCheckBoxEmptyBottle)
//    CheckBox mCheckBoxEmptyBottle;
//    @Bind(R.id.empty_bottle_rate)
//    TextView empty_bottle_rate;

    View mConvertView;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    UserBean userBean;
    DatabaseReference databaseReference;
    Dialog dialog;
    AppUser appUser;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);
        ButterKnife.bind(this);
        spinnerList = new ArrayList<>();
        spinnerList.add("Delivery with in");
        Helper.initActionbar(this, getSupportActionBar(), "Sign Up", true);
        appUser = LocalRepositories.getAppUser(this);

        if (ParameterConstants.isUpdate) {
            userBean = appUser.user;
            submit.setText("Update Account");
            terms.setVisibility(View.GONE);
        } else {
            terms.setVisibility(View.VISIBLE);
            addView("Normal Water");
            userBean = SignUpActivity.userBean;
//            checkbox(sunday);
//            checkbox(monday);
//            checkbox(tuesday);
//            checkbox(wednesday);
//            checkbox(thursday);
//            checkbox(friday);
//            checkbox(saturday);
        }

        if (userBean.getOpenBooking() != null) {
            openBooking.setText(userBean.getOpenBooking());
        }
        if (userBean.getCloseBooking() != null) {
            closeBooking.setText(userBean.getCloseBooking());
            updateSpinner();
        }
        if (userBean.getDeliveryDistance() != null) {
            deliveryDistance.setText(userBean.getDeliveryDistance());
        }

        removeView();
        if (userBean.getItems() != null) {
            for (int i = 0; i < userBean.getItems().size(); i++) {
                Item item = userBean.getItems().get(i);
                addView(item.getName(), item.getWaterRate(), item.getBottleRate());
            }
        }
        if (userBean.isSunday()) {
            sunday.setChecked(true);
        }
        if (userBean.isMonday()) {
            monday.setChecked(true);
        }
        if (userBean.isTuesday()) {
            tuesday.setChecked(true);
        }
        if (userBean.isWednesday()) {
            wednesday.setChecked(true);
        }
        if (userBean.isThursday()) {
            thursday.setChecked(true);
        }
        if (userBean.isFriday()) {
            friday.setChecked(true);
        }
        if (userBean.isSunday()) {
            saturday.setChecked(true);
        }


        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

            }

        });

        mAuth = FirebaseAuth.getInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        databaseReference = FirebaseDatabase.getInstance().getReference(ParameterConstants.KEY);
        dialog = new Dialog(this);
        dialog();

        progressDialog = new ProgressDialog(this);
        openBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(openBooking);
            }
        });
        openBookingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(openBooking);
            }
        });
        closeBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(closeBooking);
            }
        });
        closeBookingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseBooking(closeBooking);
            }
        });

        mAdd_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView("");
            }
        });
        mRemove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openBooking.getText().toString().contains("Open")) {
                    Snackbar.make(mainLayout, "Select Open Booking Time", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (closeBooking.getText().toString().contains("Close")) {
                    Snackbar.make(mainLayout, "Select Close Booking Time", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (deliveryDistance.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Enter Delivery Service Distance", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (deliveryTime.getSelectedItemPosition() == 0) {
                    Snackbar.make(mainLayout, "Select Delivery Time", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (ParameterConstants.location == null) {
                    Snackbar.make(mainLayout, "Location not found", Snackbar.LENGTH_LONG).show();
                    progressDialog.setMessage("Getting Location...");
                    progressDialog.show();
                    new MyLocationListener(getApplicationContext(),progressDialog);
                    return;
                }

                List<Item> list = new ArrayList();
                for (int i = 0; i < ((ViewGroup) mAdd_water).getChildCount(); i++) {
                    View v = ((ViewGroup) mAdd_water).getChildAt(i);
                    EditText waterType = v.findViewById(R.id.water_type);
                    EditText waterRate = v.findViewById(R.id.water_rate);
                    EditText bottleRate = v.findViewById(R.id.bottle_rate);
                    if (waterType.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Enter Water Type", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (waterRate.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Enter Water Rate", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (bottleRate.getText().toString().isEmpty()) {
                        Snackbar.make(mainLayout, "Enter Empty Bottle Rate", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Item item=new Item();
                    item.setName(waterType.getText().toString());
                    item.setWaterRate(waterRate.getText().toString());
                    item.setBottleRate(bottleRate.getText().toString());
                    list.add(item);
                }
                userBean=appUser.userTemp;
                userBean.setLatitude("" + ParameterConstants.location.getLatitude());
                userBean.setLongitude("" + ParameterConstants.location.getLongitude());
                userBean.setOpenBooking(openBooking.getText().toString());
                userBean.setCloseBooking(closeBooking.getText().toString());
                userBean.setDeliveryDistance(deliveryDistance.getText().toString());
                userBean.setDeliveryTime(deliveryTime.getSelectedItem().toString());
                userBean.setItems(list);
                userBean.setSunday(sunday.isChecked());
                userBean.setMonday(monday.isChecked());
                userBean.setTuesday(tuesday.isChecked());
                userBean.setWednesday(wednesday.isChecked());
                userBean.setThursday(thursday.isChecked());
                userBean.setFriday(friday.isChecked());
                userBean.setSaturday(saturday.isChecked());
                if (!ParameterConstants.isUpdate) {
                    retrieveKey(userBean.getMobile());
                } else {
                    new AlertDialog.Builder(SignUp3Activity.this)
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
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void openCloseBooking(TextView textView) {
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
                        textView.setText(strHrsToShow + "." + datetime.get(Calendar.MINUTE) + " " + am_pm);
                        updateSpinner();

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    void updateSpinner() {
        try {
            List<String> list = Helper.deliverTimeList(Double.parseDouble(Helper.getTimeDifferent(openBooking.getText().toString(), closeBooking.getText().toString())));
            list.add(0, "Delivery with in");
            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            deliveryTime.setAdapter(adapter);
            deliveryTime.setSelection(list.indexOf(userBean.getDeliveryTime()));
        } catch (Exception e) {
        }
    }

    void addView(String text) {
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
        editText.setText(text);
        editText.setHint("Enter Water Type");
        ((ViewGroup) mAdd_water).addView(mConvertView);
    }

    void addView(String type, String wRate, String bRate) {
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
        EditText typ = ((EditText) mConvertView.findViewById(R.id.water_type));
        EditText wrt = ((EditText) mConvertView.findViewById(R.id.water_rate));
        EditText brt = ((EditText) mConvertView.findViewById(R.id.bottle_rate));
        typ.setText(type);
        wrt.setText(wRate);
        brt.setText(bRate);
        ((ViewGroup) mAdd_water).addView(mConvertView);
    }

    void removeView() {
        try {
            if (((ViewGroup) mAdd_water).getChildCount() == 1) {
                Toast.makeText(this, "can't remove", Toast.LENGTH_SHORT).show();
                return;
            }
            mConvertView = getLayoutInflater().inflate(R.layout.dynamic_add_water, null);
            EditText editText = ((EditText) mConvertView.findViewById(R.id.water_type));
            editText.setText("");
            editText.setHint("Enter Water Type");
            ((ViewGroup) mAdd_water).removeViewAt(((ViewGroup) mAdd_water).getChildCount() - 1);
        } catch (Exception e) {

        }
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
                    progressDialog.setMessage("Verifying OTP...");
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getApplicationContext(), "invalid OTP", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Verification done", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "invalid opt", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void registerUser(UserBean userBean) {

//have to remove
        insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());




//have to uncomment for otp


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(getApplicationContext(), "verification done", Toast.LENGTH_LONG).show();
//                insertUpdate(ParameterConstants.location.getLatitude(), ParameterConstants.location.getLongitude());
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
//                System.out.println("aaaaaaaaaaaaa  " + e);
//                System.out.println("aaaaaaaaaaaaa  " + e.getMessage());
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    Toast.makeText(getApplicationContext(), "invalid mob no", Toast.LENGTH_LONG).show();
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    Toast.makeText(getApplicationContext(), "quota over", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCodeSent(String verificationId,
//                                   PhoneAuthProvider.ForceResendingToken token) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Verification code sent", Toast.LENGTH_LONG).show();
//                mVerificationId = verificationId;
//                mResendToken = token;
//                dialog.show();
//            }
//        };
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+91" + userBean.getMobile(),             // Phone number to verify
//                60,                      // Timeout duration
//                TimeUnit.SECONDS,        // Unit of timeout
//                SignUp3Activity.this,   // Activity (for callback binding)
//                mCallbacks);         // OnVerificationStateChangedCallbacks
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    void checkbox(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(SignUp3Activity.this, "" + checkBox.getText().toString(), Toast.LENGTH_SHORT).show();
                if (checkBox.getText().toString().equals("Sunday")) {
                    userBean.setSunday(b);
                }
                if (checkBox.getText().toString().equals("Monday")) {
                    userBean.setMonday(b);
                }
                if (checkBox.getText().toString().equals("Tuesday")) {
                    userBean.setTuesday(b);
                }
                if (checkBox.getText().toString().equals("Wednesday")) {
                    userBean.setWednesday(b);
                }
                if (checkBox.getText().toString().equals("Thursday")) {
                    userBean.setThursday(b);
                }
                if (checkBox.getText().toString().equals("Friday")) {
                    userBean.setFriday(b);
                }
                if (checkBox.getText().toString().equals("Saturday")) {
                    userBean.setSaturday(b);
                }


            }
        });
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
                        Toast.makeText(SignUp3Activity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    appUser.user = userBean;
                    LocalRepositories.saveAppUser(getApplicationContext(), appUser);
                    Intent intent = new Intent(SignUp3Activity.this, SupplierHomeActivity.class);
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
