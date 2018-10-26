package com.android.app.watersupply.activities.customer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.SignInActivity;
import com.android.app.watersupply.activities.SignUpActivity;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomerProfile extends AppCompatActivity {

    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.mobile)
    TextView mobile;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.update)
    TextView update;
    @Bind(R.id.single_char)
    TextView single_char;

    AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "My Account", true);
        appUser = LocalRepositories.getAppUser(this);

        name.setText(appUser.user.getName());
        email.setText(appUser.user.getEmail());
        mobile.setText(appUser.user.getMobile());
        address.setText(appUser.user.getAddress());
        single_char.setText(""+appUser.user.getName().charAt(0));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParameterConstants.isUpdate = true;
                ParameterConstants.KEY = "Customer";
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
