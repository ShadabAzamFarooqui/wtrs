package com.android.app.watersupply.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.customer.CustomerHomeActivity;
import com.android.app.watersupply.adapter.EmptyBottleAdapter;
import com.android.app.watersupply.adapter.WaterDetailAdapter;
import com.android.app.watersupply.bean.Bottle;
import com.android.app.watersupply.bean.Combine;
import com.android.app.watersupply.bean.OrderBean;
import com.android.app.watersupply.bean.Water;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity {

    @Bind(R.id.address)
    EditText mAddress;
    @Bind(R.id.checkbox_address)
    CheckBox mCheckboxAddress;
    @Bind(R.id.checkbox_delivery)
    CheckBox mCheckboxDelivery;
    @Bind(R.id.today)
    TextView mToday;
    @Bind(R.id.tomorrow)
    TextView mTomorrow;
    @Bind(R.id.date_time)
    TextView mDate_time;
    @Bind(R.id.comment)
    EditText mComment;
    @Bind(R.id.open_calender)
    LinearLayout mOpen_calender;
    @Bind(R.id.coordinatorLayout)
    LinearLayout coordinatorLayout;
    @Bind(R.id.total)
    TextView total;
    @Bind(R.id.empty_bottle_checkbox)
    CheckBox mCheckBoxEmptyBottle;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerViewWater;
    @Bind(R.id.recycler_view2)
    RecyclerView mRecyclerViewBottle;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.submit)
    Button mSubmit;
    LinearLayoutManager linearLayoutManager;
    WaterDetailAdapter mAdapter;
    EmptyBottleAdapter mAdapter2;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Dialog dialog;

    AppUser appUser;
    int endTime = 15;
    public static OrderActivity context;
    DatePickerDialog datePickerDialog;
    String dateString;
    String format = "dd MMM yyyy";
    String today;
    String tomorrow;
    public static boolean isUpdateOrder;
    public static String key;
    public static List<Combine> orderedItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        context = this;

        Calendar calendar = Calendar.getInstance();
        Date t = calendar.getTime();
//        Toast.makeText(this, ""+t, Toast.LENGTH_SHORT).show();
        long dateToday = System.currentTimeMillis();
        long dateTomorrow = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        today = sdf.format(dateToday);
        tomorrow = sdf.format(dateTomorrow);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Booking...");
        progressDialog.setCancelable(false);
        appUser = LocalRepositories.getAppUser(this);
        Helper.initActionbar(this, getSupportActionBar(), "make order", true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        mAddress.setText(appUser.user.getAddress().trim());
        mAddress.setEnabled(false);

        mCheckboxAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAddress.setText(appUser.user.getAddress().trim());
                    mAddress.setEnabled(false);
                } else {
                    mAddress.setEnabled(true);
                    mAddress.setText(ParameterConstants.ADDRESS);
                }
            }
        });

        mCheckboxDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mCheckboxDelivery.setChecked(true);
//                Snackbar.make(coordinatorLayout, "Online payment is under development", Snackbar.LENGTH_SHORT).show();
            }
        });


        mRecyclerViewBottle.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        mCheckBoxEmptyBottle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    mRecyclerViewBottle.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    setAdapter2();
                } else {
                    mRecyclerViewBottle.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    Set<Integer> set = EmptyBottleAdapter.map.keySet();
                    double r = 0.0;
                    for (Integer key : set) {
                        Bottle bottle = EmptyBottleAdapter.map.get(key);
                        double d = Double.valueOf(bottle.getRate()) * Double.valueOf(bottle.getQty());
                        double t = Double.valueOf(total.getText().toString());
                        r = t - d;
                        total.setText("" + r);
                    }
                    EmptyBottleAdapter.map.clear();
                }
            }
        });

        if (!Helper.checkDate(this) || appUser.status.contains("C")) {
            tomorrowView();
            mDate_time.setText("" + tomorrow + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

        } else {
            todayView();
            mDate_time.setText("" + today + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());
        }


        mToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.checkDate(context) || appUser.status.contains("C")) {
                    Snackbar.make(coordinatorLayout, "Booking Closed", Toast.LENGTH_SHORT).show();
                    return;
                }
                todayView();
                mDate_time.setText("" + today + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

            }
        });

        mTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomorrowView();
                mDate_time.setText("" + tomorrow + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());
            }
        });

        dateDialog(mOpen_calender, mDate_time);

        setAdapter();
        setAdapter2();
        if (isUpdateOrder){
            mSubmit.setText("Update Order");
//            for (int i=0;i<orderedItemList.size();i++){
//                for (int j=0;j<appUser.supplier.getItems().size();j++){
//                    if (orderedItemList.get(i).getWater().getName().equals(appUser.supplier.getItems().get(j).getName()))
//                        WaterDetailAdapter.map.put(j,orderedItemList.get(i).getWater());
//                }
//            }
        }
//        mAdapter.notifyDataSetChanged();
    }


    void setAdapter() {
        mRecyclerViewWater.setHasFixedSize(true);
        mRecyclerViewWater.setFocusable(false);
        mRecyclerViewWater.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerViewWater.setLayoutManager(linearLayoutManager);
        mAdapter = new WaterDetailAdapter(this, appUser.supplier.getItems());
        mRecyclerViewWater.setAdapter(mAdapter);
    }

    void setAdapter2() {
        mRecyclerViewBottle.setHasFixedSize(true);
        mRecyclerViewBottle.setFocusable(false);
        mRecyclerViewBottle.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerViewBottle.setLayoutManager(linearLayoutManager);
        mAdapter2 = new EmptyBottleAdapter(this, appUser.supplier.getItems(), coordinatorLayout);
        mRecyclerViewBottle.setAdapter(mAdapter2);
    }


    void dateDialog(View view, TextView date) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(OrderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                        dateString = sdf.format(myCalendar.getTime());
                        date.setText(dateString + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());

                        if (Integer.valueOf(today.split(" ")[0]) == dayOfMonth) {
                            todayView();
                        } else if (Integer.valueOf(tomorrow.split(" ")[0]) == dayOfMonth) {
                            tomorrowView();
                        } else {
                            mToday.setTextColor(getResources().getColor(R.color.black));
                            mToday.setBackground(getResources().getDrawable(R.drawable.black_border));
                            mTomorrow.setTextColor(getResources().getColor(R.color.black));
                            mTomorrow.setBackground(getResources().getDrawable(R.drawable.black_border));
                        }
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                Calendar cal = Calendar.getInstance();
                if (!Helper.checkDate(context) || appUser.status.contains("C")) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_YEAR, 6);
                    datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                } else {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    cal.add(Calendar.DAY_OF_YEAR, 7);
                    datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                }

            }

        });
    }

    void todayView() {
        mToday.setTextColor(getResources().getColor(R.color.colorPrimary));
        mToday.setBackground(getResources().getDrawable(R.drawable.orange_border));
        mTomorrow.setTextColor(getResources().getColor(R.color.black));
        mTomorrow.setBackground(getResources().getDrawable(R.drawable.black_border));
    }

    void tomorrowView() {
        mToday.setTextColor(getResources().getColor(R.color.black));
        mToday.setBackground(getResources().getDrawable(R.drawable.black_border));
        mTomorrow.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTomorrow.setBackground(getResources().getDrawable(R.drawable.orange_border));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void submit(View view) {
        if (!mCheckboxDelivery.isChecked()) {
            Intent intent = new Intent(getApplicationContext(), PayMentGateWay.class);
            if (WaterDetailAdapter.map.size() == 0) {
                Snackbar.make(coordinatorLayout, "Please Select the Bottle", Snackbar.LENGTH_SHORT).show();
                return;
            }
            CustomerHomeActivity.bool = true;
            intent.putExtra("amount", total.getText().toString());
            startActivity(intent);
        } else {
            CustomerHomeActivity.bool = true;
            postOrder(progressDialog);
        }
    }

    public void postOrder(ProgressDialog progressDialog) {
        OrderBean orderBean = new OrderBean();
        List<Combine> list = new ArrayList();
        if (WaterDetailAdapter.map.size() == 0) {
            Snackbar.make(coordinatorLayout, "Please Select an Bottle quantity", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Set<Integer> waterKey = WaterDetailAdapter.map.keySet();
        for (Integer key : waterKey) {
            Combine combine = new Combine();
            combine.setWater(WaterDetailAdapter.map.get(key));
            list.add(combine);
        }

        Set<Integer> bottleKey = EmptyBottleAdapter.map.keySet();
        Combine combine = null;
        for (Integer key : bottleKey) {
            try {
                combine = new Combine();
                Water water = WaterDetailAdapter.map.get(key);
                Bottle bottle = EmptyBottleAdapter.map.get(key);
                combine.setWater(water);
                combine.setBottle(bottle);
                boolean b = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getWater() == WaterDetailAdapter.map.get(key)) {
                        b = true;
                        list.set(i, combine);
                    }
                }
                if (!b) {
                    list.add(combine);
                }

            } catch (Exception e) {
                list.add(combine);
            }
        }

        orderBean.setUser(appUser.user);
        orderBean.setSupplier(appUser.supplier);
        orderBean.setAmount("10");
        String text = mDate_time.getText().toString();
        String date = text.substring(0, text.indexOf("B"));
        orderBean.setCashOnDelivery(true);
        orderBean.setBookingDate(new SimpleDateFormat(format + " hh:mm aa").format(System.currentTimeMillis()));
        orderBean.setDeliveryDate(date);
        orderBean.setComment(mComment.getText().toString().trim());
        orderBean.setWaterTypeQuantity(list);
        orderBean.setAmount(total.getText().toString());
        orderBean.setAddress(mAddress.getText().toString());
        orderBean.setStatus(ParameterConstants.PENDING);
        if (!Helper.checkDate(context)&&mToday.getCurrentTextColor()==getResources().getColor(R.color.colorPrimary)) {
            tomorrowView();
            mDate_time.setText("" + tomorrow + " Between " + appUser.supplier.getOpenBooking() + " to " + appUser.supplier.getCloseBooking());
            Snackbar.make(coordinatorLayout, "Today's booking has closed", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        String key;
        if (isUpdateOrder){
            key = OrderActivity.key;
        }else {
            key = databaseReference.push().getKey();
        }
        orderBean.setOrderId(key);
        databaseReference.child(key).setValue(orderBean, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (databaseError == null) {
                    Toast.makeText(OrderActivity.this, "Your order is successfully submitted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrderActivity.this, "Sorry, Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public String getTotal() {
        return total.getText().toString();
    }

    public void setTotal(String total_amount) {
        total.setText(total_amount);
    }
}
