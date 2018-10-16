package com.example.berylsystems.watersupply.adapter.customer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.activities.OrderActivity;
import com.example.berylsystems.watersupply.bean.UserBean;
import com.example.berylsystems.watersupply.utils.AppUser;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.LocalRepositories;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SupplierListAdapter extends RecyclerView.Adapter<SupplierListAdapter.ViewHolder> {

    private List<UserBean> data;
    private Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    String today;

    public SupplierListAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout, List<UserBean> data, String today) {
        this.data = data;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.today = today;
    }


    @Override
    public SupplierListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_supplier_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SupplierListAdapter.ViewHolder viewHolder, int position) {
        if (today.equalsIgnoreCase("Sun") && data.get(position).isSunday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Mon") && data.get(position).isMonday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Tue") && data.get(position).isTuesday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Wed") && data.get(position).isWednesday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Thu") && data.get(position).isThursday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Fri") && data.get(position).isFriday()) {
            setStatus(viewHolder,position);
        } else if (today.equalsIgnoreCase("Sat") && data.get(position).isSaturday()) {
            setStatus(viewHolder,position);
        } else {
            viewHolder.status.setText("Booking Closed");
        }


        viewHolder.bind(data.get(position).getDeliveryTime());
        String s = data.get(position).getOpenBooking() + " - " + data.get(position).getCloseBooking() + " (Deliver Up to " + data.get(position).getDeliveryTime() + ")";
        SpannableStringBuilder str = new SpannableStringBuilder(s);
        str.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), s.indexOf("("), s.indexOf(")"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.time.setText(str);
        viewHolder.name.setText(data.get(position).getName());
        viewHolder.shop_name.setText(data.get(position).getShopName());
        viewHolder.mobile.setText(data.get(position).getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRefreshLayout.setRefreshing(false);
                AppUser appUser = LocalRepositories.getAppUser(context);
                appUser.supplier = data.get(position);
                appUser.status = viewHolder.status.getText().toString();
                LocalRepositories.saveAppUser(context, appUser);
                OrderActivity.isUpdateOrder=false;
                context.startActivity(new Intent(context, OrderActivity.class));
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.main_layout)
        LinearLayout main_layout;
        @Bind(R.id.shop_name)
        TextView shop_name;
        @Bind(R.id.mobile)
        TextView mobile;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.status)
        TextView status;
//        @Bind(R.id.delivery_with_in)
//        TextView delivery_with_in;

        private ObjectAnimator anim;

        @SuppressLint("WrongConstant")
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            anim = ObjectAnimator.ofFloat(status, "alpha", 0.0f, 1f);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(800);
        }

        public void bind(String dataObject) {
            anim.start();
        }
    }


    void setStatus(ViewHolder viewHolder,int position){
        if (Helper.checkDate(data.get(position).getDeliveryTime(), data.get(position).getCloseBooking())) {
            viewHolder.status.setText("Booking Open");
        } else {
            viewHolder.status.setText("Booking Closed");
        }
    }
}
