package com.android.app.watersupply.fragment.customer;

import android.location.Location;
import android.location.LocationManager;
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
import com.android.app.watersupply.adapter.customer.SupplierListAdapter;
import com.android.app.watersupply.bean.UserBean;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplierListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.mainLayout)
    LinearLayout mainLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    SupplierListAdapter mAdapter;
    public static List<UserBean> userBeanList;
    ValueEventListener valueEventListener;
    AppUser appUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplier_list_fragment, container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
        appUser = LocalRepositories.getAppUser(getActivity());
        userBeanList = new ArrayList<>();
        if (Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
        }
        getAllRecord("Supplier");
        databaseReference.addValueEventListener(valueEventListener);
        return view;
    }

    void getAllRecord(String key) {
        databaseReference = FirebaseDatabase.getInstance().getReference(key);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                userBeanList.clear();
                if (dataSnapshot.getValue() != null) {
                    int i = 0;
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    long count = dataSnapshot.getChildrenCount();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        i++;
                        DataSnapshot snapshot = iterator.next();
                        final UserBean userBean = (UserBean) snapshot.getValue(UserBean.class);
                        try {
                            Location lStart = new Location(LocationManager.NETWORK_PROVIDER);
                            lStart.setLatitude(Double.parseDouble(appUser.user.getLatitude()));
                            lStart.setLongitude(Double.parseDouble(appUser.user.getLongitude()));

                            Location lEnd = new Location(LocationManager.NETWORK_PROVIDER);
                            lEnd.setLatitude(Double.parseDouble(userBean.getLatitude()));
                            lEnd.setLongitude(Double.parseDouble(userBean.getLongitude()));
                            if (lStart.distanceTo(lEnd) / 1000 <= Double.valueOf(userBean.getDeliveryDistance().trim().split(" ")[0])) {
                                userBeanList.add(userBean);
                            }
                        } catch (Exception e) {
                            userBeanList.add(userBean);
                        }
                    }
                    if (i == count) {
                        setAdapter();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    void setAdapter() {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        mAdapter = new SupplierListAdapter(getActivity(), swipeRefreshLayout, userBeanList, String.valueOf(today).split(" ")[0]);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRefresh() {
        if (!Helper.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mainLayout, "Please check your internet connection!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        getAllRecord("Supplier");
        databaseReference.addValueEventListener(valueEventListener);
    }

    String today() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        return String.valueOf(today).split(" ")[0];
    }
}
