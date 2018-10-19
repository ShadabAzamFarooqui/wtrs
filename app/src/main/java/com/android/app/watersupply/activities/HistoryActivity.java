package com.android.app.watersupply.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.adapter.HistoryListAdapter;
import com.android.app.watersupply.bean.OrderBean;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    HistoryListAdapter mAdapter;
    List<OrderBean> orderBeanList;
    ValueEventListener firstValueListener;
    AppUser appUser;
    String mobileNumber;
    public static String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        Helper.initActionbar(this, getSupportActionBar(), "HISTORY", true);
        swipeRefreshLayout.setOnRefreshListener(this);
        appUser = LocalRepositories.getAppUser(this);
        mobileNumber = appUser.user.getMobile();

        dateFormatter = new SimpleDateFormat(format, Locale.US);

        orderBeanList = new ArrayList<>();

        if (Helper.isNetworkAvailable(this)) {
            swipeRefreshLayout.setRefreshing(true);
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
    }

    void setAdapter(Boolean b) {
        swipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderBeanList);
        mAdapter = new HistoryListAdapter(this, orderBeanList,b);
        mRecyclerView.setAdapter(mAdapter);
    }

    void getAllRecord(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(key);
        firstValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                int i = 0;
                orderBeanList.clear();
                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    long count = dataSnapshot.getChildrenCount();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        i++;
                        DataSnapshot snapshot = iterator.next();
                        final OrderBean orderBean = (OrderBean) snapshot.getValue(OrderBean.class);
                        if (userType.equals("Customer")) {
                            if (orderBean.getUser().getMobile().equals(mobileNumber)) {
                                orderBeanList.add(orderBean);
                            }
                        } else {
                            if (orderBean.getSupplier().getMobile().equals(mobileNumber)) {
                                orderBeanList.add(orderBean);
                            }
                        }

                        if (i == count) {
                            if (userType.equals("Customer")){
                                setAdapter(false);
                            }else {
                                setAdapter(true);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sorting) {
            showpopup();
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public Dialog dialog;
    private SimpleDateFormat dateFormatter;
    String startdate;
    String enddate;
    DatePickerDialog datePickerDialog;
    Date dateObject1, dateObject2;
    String format="dd MMM yyyy";

    public void showpopup() {
        dialog = new Dialog(HistoryActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_pick_dialog);
        dialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        TextView date1 = (TextView) dialog.findViewById(R.id.date1);
        TextView date2 = (TextView) dialog.findViewById(R.id.date2);
        ImageView date1Icon = (ImageView) dialog.findViewById(R.id.date1_icon);
        ImageView date2Icon = (ImageView) dialog.findViewById(R.id.date2_icon);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateString = sdf.format(date);
        date1.setText(dateString);
        date2.setText(dateString);



        Button submit = (Button) dialog.findViewById(R.id.dialogSubmit);
        LinearLayout close = (LinearLayout) dialog.findViewById(R.id.close);
        datePopup(date1,date1);
        datePopup(date1Icon,date1);
        datePopup(date2,date2);
        datePopup(date2Icon,date2);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dateObject1 = dateFormatter.parse(date1.getText().toString());
                    dateObject2 = dateFormatter.parse(date2.getText().toString());
                    if (dateObject2.compareTo(dateObject1)==-1){
                        Toast.makeText(getApplicationContext(), "Please select valid date range", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    try {
                        dateObject1 = dateFormatter.parse(date1.getText().toString());
                        dateObject2 = dateFormatter.parse(date2.getText().toString());
                        if (dateObject2.compareTo(dateObject1)==-1){
                            Toast.makeText(getApplicationContext(), "Please select valid date range", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e2) {
                        e.printStackTrace();
                    }
                }

                if(!date1.getText().toString().equals("")&&!date2.getText().toString().equals("")){

                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    void datePopup(View view,TextView textView){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                        startdate = sdf.format(myCalendar.getTime());
                        enddate = sdf.format(myCalendar.getTime());
                        textView.setText(startdate);
                    }

                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });
    }


    @Override
    public void onRefresh() {
        if (!Helper.isNetworkAvailable(this)) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mainLayout,"Please check your internet connection!",Snackbar.LENGTH_SHORT).show();
            return;
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
    }
}
