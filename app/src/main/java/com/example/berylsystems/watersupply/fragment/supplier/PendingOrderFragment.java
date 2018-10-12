package com.example.berylsystems.watersupply.fragment.supplier;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.adapter.supplier.PendingOrderListAdapter;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PendingOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isExit;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    public static PendingOrderListAdapter mAdapter;
    public static List<OrderBean> orderBeanList;
    ValueEventListener firstValueListener;
    AppUser appUser;
    String mobileNumber;
    String format = "dd MMM yyyy";
    String today;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplier_pending_fragment, container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
        appUser = LocalRepositories.getAppUser(getActivity());
        mobileNumber = appUser.user.getMobile();
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        today = sdf.format(date);
        orderBeanList = new ArrayList<>();
//        progressDialog.setCancelable(false);
        if (Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addListenerForSingleValueEvent(firstValueListener);
        return view;
    }

    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderBeanList);
        mAdapter = new PendingOrderListAdapter(getActivity(), orderBeanList, swipeRefreshLayout);
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
                        if (orderBean.getSupplier().getMobile().equals(mobileNumber)) {
                            if (orderBean.getStatus().equals(ParameterConstants.PENDING)) {
                                if (orderBean.getDeliveryDate().trim().equals(today)) {
                                    orderBeanList.add(orderBean);
                                }
                            }

                        }
                        if (i == count) {
                            setAdapter();
                        }
                    }

                }else {
                    setAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    @Override
    public void onRefresh() {
        if (!Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mainLayout, "Please check your internet connection!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
    }
}
