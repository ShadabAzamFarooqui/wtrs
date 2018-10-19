package com.android.app.watersupply.adapter.customer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.OrderActivity;
import com.android.app.watersupply.bean.Combine;
import com.android.app.watersupply.bean.OrderBean;
import com.android.app.watersupply.bean.UserBean;
import com.android.app.watersupply.fragment.customer.SupplierListFragment;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;
    Boolean aBoolean;
    ProgressDialog mProgressDialog;

    public OrderListAdapter(Activity context, List<OrderBean> data, Boolean b) {
        this.data = data;
        this.context = context;
        aBoolean = b;
    }


    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getUser().getName());
        viewHolder.supplierMobile.setText(data.get(position).getSupplier().getMobile());
        viewHolder.userMobile.setText(data.get(position).getUser().getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        viewHolder.status.setText(data.get(position).getStatus());
        viewHolder.amount.setText("\u20B9" + data.get(position).getAmount());
        viewHolder.orderId.setText(data.get(position).getOrderId().toUpperCase());
        if (data.get(position).getComment().trim().isEmpty()) {
            viewHolder.comment.setVisibility(View.GONE);
        } else {
            viewHolder.comment.setVisibility(View.VISIBLE);
            viewHolder.comment.setText(data.get(position).getComment());
        }
        viewHolder.bind(data.get(position).getStatus());
        if (data.get(position).getStatus().equals(ParameterConstants.CANCEL)) {
            viewHolder.cancel.setVisibility(View.VISIBLE);
            viewHolder.reason.setVisibility(View.VISIBLE);
            viewHolder.reason.setText(data.get(position).getReason());
        } else {
            viewHolder.cancel.setVisibility(View.GONE);
            viewHolder.reason.setVisibility(View.GONE);
        }
        removeAllViews(viewHolder);
        try {
            for (int i = 0; i < data.get(position).combine().size(); i++) {
                Combine combine = data.get(position).combine().get(i);
                if (combine.getWater() == null && combine.getBottle() != null) {
                    String name = combine.getBottle().getName();
                    Integer wQty = 0;
                    Integer bQty = combine.getBottle().getQty();
                    Double rate = combine.getBottle().getRate() * bQty;
                    addView(name, wQty, bQty, rate, viewHolder);
                } else if (combine.getBottle() == null && combine.getWater() != null) {
                    String name = combine.getWater().getName();
                    Integer bQty = 0;
                    Integer wQty = combine.getWater().getQty();
                    Double rate = combine.getWater().getRate() * wQty;
                    addView(name, wQty, bQty, rate, viewHolder);
                } else if (combine.getBottle() != null && combine.getWater() != null) {
                    String name = combine.getWater().getName();
                    Integer bQty = combine.getBottle().getQty();
                    Integer wQty = combine.getWater().getQty();
                    Double rate = combine.getWater().getRate() * wQty + combine.getBottle().getRate() * bQty;
                    addView(name, wQty, bQty, rate, viewHolder);
                }
            }
        } catch (Exception e) {
        }
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.get(position).getStatus().equals(ParameterConstants.PENDING) || data.get(position).getStatus().equals(ParameterConstants.CANCEL)) {
                    dialog(position, "Delete Confirmation", "Do you want to delete this order ?");
                } else {
                    Toast.makeText(context, "Order Can't delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.get(position).getStatus().equals(ParameterConstants.PENDING) ) {
                    OrderActivity.isUpdateOrder = true;
                    OrderActivity.key = data.get(position).getOrderId();
                    AppUser appUser = LocalRepositories.getAppUser(context);
                    UserBean supplier=getSupplier(position);
                    String today=String.valueOf(Calendar.getInstance().getTime()).split(" ")[0];
                    if (today.equalsIgnoreCase("Sun") && supplier.isSunday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Mon") && supplier.isMonday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Tue") && supplier.isTuesday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Wed") && supplier.isWednesday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Thu") && supplier.isThursday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Fri") && supplier.isFriday()) {
                        setStatus(position,appUser);
                    } else if (today.equalsIgnoreCase("Sat") && supplier.isSaturday()) {
                        setStatus(position,appUser);
                    } else {
                        appUser.status="Booking Closed";
                    }
                    appUser.supplier = supplier;
                    OrderActivity.orderedItemList=data.get(position).combine();
                    LocalRepositories.saveAppUser(context, appUser);
                    context.startActivity(new Intent(context, OrderActivity.class));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    void addView(String name, Integer wQty, Integer bQty, Double rate, ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        TextView orderName = ((TextView) mConvertView.findViewById(R.id.orderName));
        TextView waterQty = ((TextView) mConvertView.findViewById(R.id.waterQty));
        TextView bottleQty = ((TextView) mConvertView.findViewById(R.id.bottleQty));
        TextView orderRate = ((TextView) mConvertView.findViewById(R.id.orderRate));
        orderName.setText(name/*+" (\u20B9"+rate+")"*/);
        waterQty.setText("" + wQty);
        bottleQty.setText("" + bQty);
        orderRate.setText("" + rate);
        ((ViewGroup) viewHolder.parentLayout).addView(mConvertView);
    }

    void removeAllViews(ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        ((ViewGroup) viewHolder.parentLayout).removeAllViews();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.shopName)
        TextView shopName;
        @Bind(R.id.day)
        TextView day;
        @Bind(R.id.month)
        TextView month;
        @Bind(R.id.year)
        TextView year;
        @Bind(R.id.bookingTime)
        TextView bookingTime;
        @Bind(R.id.supplierName)
        TextView supplierName;
        @Bind(R.id.supplierMobile)
        TextView supplierMobile;
        @Bind(R.id.userMobile)
        TextView userMobile;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.status)
        TextView status;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.orderId)
        TextView orderId;
        @Bind(R.id.parentLayout)
        LinearLayout parentLayout;
        @Bind(R.id.amount)
        TextView amount;
        @Bind(R.id.delete)
        ImageView delete;
        @Bind(R.id.reason)
        TextView reason;
        @Bind(R.id.cancel)
        TextView cancel;
        @Bind(R.id.mainLayout)
        LinearLayout mainLayout;


        private ObjectAnimator anim;

        @SuppressLint("WrongConstant")
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            anim = ObjectAnimator.ofFloat(status, "alpha", 0.2f, 1f);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(800);
        }

        public void bind(String dataObject) {
            anim.start();
        }
    }


    void dialog(int position, String tittle, String message) {
        new AlertDialog.Builder(context)
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (!Helper.isNetworkAvailable(context)) {
                        Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Deleting...");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                    database.child(data.get(position).getOrderId()).setValue(null, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mProgressDialog.dismiss();
                            if (databaseError == null) {
                                Toast.makeText(context, "This order has been deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    UserBean getSupplier(int position){
        UserBean supplier=null;
         for( int i=0;i< SupplierListFragment.userBeanList.size();i++){
             if (data.get(position).getSupplier().getMobile().equals(SupplierListFragment.userBeanList.get(i).getMobile())){
                 supplier=SupplierListFragment.userBeanList.get(i);
             }
         }
        return supplier;
    }

    void setStatus(int position,AppUser appUser){
        if (Helper.checkDate(data.get(position).getSupplier().getDeliveryTime(), data.get(position).getSupplier().getCloseBooking())) {
            appUser.status="Booking Open";
        } else {
            appUser.status="Booking Closed";
        }
    }
}
