package com.android.app.watersupply.fragment.customer;

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

import com.android.app.watersupply.R;
import com.android.app.watersupply.adapter.customer.OrderListAdapter;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    OrderListAdapter mAdapter;
    List<OrderBean> orderBeanList;
    ValueEventListener firstValueListener;
    AppUser appUser;
    String mobileNumber;
    String format="dd MMM yyyy";
    String today;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_order_list_fragment, container, false);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setOnRefreshListener(this);
        appUser = LocalRepositories.getAppUser(getActivity());
        mobileNumber=appUser.user.getMobile();
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        today = sdf.format(date);
        orderBeanList = new ArrayList<>();
        if (Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
        return view;
    }

    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderBeanList);
        mAdapter = new OrderListAdapter(getActivity(), orderBeanList,false);
        mRecyclerView.setAdapter(mAdapter);
    }

    void getAllRecord(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(key);
        firstValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                int i=0;
                orderBeanList.clear();
                if (dataSnapshot.getValue() != null) {

                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    long count = dataSnapshot.getChildrenCount();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        i++;
                        DataSnapshot snapshot = iterator.next();
                        final OrderBean orderBean = (OrderBean) snapshot.getValue(OrderBean.class);
                        if (orderBean.getUser().getMobile().equals(mobileNumber)){
//                            String dateAr[]=orderBean.getBookingDate().split(" ");
//                            String s=new SimpleDateFormat("dd MMM yyyy").format(orderBean.getBookingDate());
//                            String date=dateAr[0]+" "+dateAr[1]+" "+dateAr[2];
                            if (orderBean.getDeliveryDate().trim().equals(today)){
                                orderBeanList.add(orderBean);
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRefresh() {
        if (!Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mainLayout,"Please check your internet connection!",Snackbar.LENGTH_SHORT).show();
            return;
        }
        getAllRecord(ParameterConstants.ORDER);
        databaseReference.addValueEventListener(firstValueListener);
    }
}
