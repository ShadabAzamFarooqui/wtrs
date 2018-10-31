package com.android.app.watersupply.activities.supplier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.SignUp2Activity;
import com.android.app.watersupply.activities.SignUpActivity;
import com.android.app.watersupply.bean.Item;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplierProfile extends AppCompatActivity {
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.shop_name)
    TextView shop_name;
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
    @Bind(R.id.parent_layout)
    LinearLayout parent_layout;
    View mConvertView;

    AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_profile);

        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "My Account", true);
        appUser = LocalRepositories.getAppUser(this);

        name.setText(appUser.user.getName());
        shop_name.setText(appUser.user.getShopName());
        email.setText(appUser.user.getEmail());
        mobile.setText(appUser.user.getMobile());
        address.setText(appUser.user.getAddress());
        single_char.setText("" + appUser.user.getName().charAt(0));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParameterConstants.isUpdate=true;
                ParameterConstants.KEY = "Supplier";
                startActivity(new Intent(getApplicationContext(),SignUp2Activity.class));
            }
        });
        for (int i=0;i<appUser.user.getItems().size();i++){
            addView(appUser.user.getItems().get(i));
        }


//        for (int i=0;i<appUser.supplier.)
    }


    void addView(Item item) {
        mConvertView = getLayoutInflater().inflate(R.layout.dynamic_show_water, null);
        TextView water_type = ((TextView) mConvertView.findViewById(R.id.water_type));
        TextView water_rate = ((TextView) mConvertView.findViewById(R.id.water_rate));
        TextView bottle_rate = ((TextView) mConvertView.findViewById(R.id.bottle_rate));
        water_type.setText(item.getName());
        water_rate.setText(item.getWaterRate());
        bottle_rate.setText(item.getBottleRate());
        ((ViewGroup) parent_layout).addView(mConvertView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
