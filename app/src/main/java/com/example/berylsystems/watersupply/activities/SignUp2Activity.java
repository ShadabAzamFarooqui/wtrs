package com.example.berylsystems.watersupply.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.example.berylsystems.watersupply.utils.Validation;


import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp2Activity extends AppCompatActivity {
    @Bind(R.id.submit)
    Button mNext;
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
    UserBean userBean;
    AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up2);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "Sign Up", true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        appUser = LocalRepositories.getAppUser(this);
        if (ParameterConstants.isUpdate) {
            mMobile.setEnabled(false);
            userBean = appUser.user;
        } else {
            mMobile.setEnabled(true);
            userBean = SignUpActivity.userBean;
           /* if (userBean == null) {
                userBean = new UserBean();
            } else {
                userBean = SignUpActivity.userBean;
            }*/
        }
//        try {
//            mAddress.setText(ParameterConstants.ADDRESS);
//        } catch (Exception e) {
//        }

        if (userBean.getName() != null) {
            mName.setText(userBean.getName());
        }
        if (userBean.getShopName() != null) {
            mShop_name.setText(userBean.getShopName());
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
        if (userBean.getAddress() != null) {
            mAddress.setText(userBean.getAddress());
        }


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.validateName(mName)) {
                    Snackbar.make(mainLayout, "Invalid Name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mShop_name.getText().toString().isEmpty()) {
                    Snackbar.make(mainLayout, "Enter Shop Name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!Validation.validateName(mShop_name)) {
                    Snackbar.make(mainLayout, "Invalid Shop Name", Snackbar.LENGTH_LONG).show();
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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                userBean.setName(mName.getText().toString());
                userBean.setShopName(mShop_name.getText().toString());
                userBean.setMobile(mMobile.getText().toString());
                userBean.setEmail(mEmail.getText().toString());
                userBean.setPassword(mPassword.getText().toString());
                userBean.setAddress(mAddress.getText().toString());
                startActivity(new Intent(getApplicationContext(), SignUp3Activity.class));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

}
