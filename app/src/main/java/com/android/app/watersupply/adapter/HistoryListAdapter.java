package com.android.app.watersupply.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.app.watersupply.R;
import com.android.app.watersupply.bean.Combine;
import com.android.app.watersupply.bean.OrderBean;
import com.android.app.watersupply.utils.ParameterConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;

    public HistoryListAdapter(Activity context, List<OrderBean> data, Boolean b) {
        this.data = data;
        this.context = context;
    }


    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (ParameterConstants.KEY.contains("C")) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_history_customer, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_history_supplier, viewGroup, false);
        }

        return new HistoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryListAdapter.ViewHolder viewHolder, int position) {

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
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    void addView(String name, Integer wQty, Integer bQty, Double rate, HistoryListAdapter.ViewHolder viewHolder) {
        mConvertView = context.getLayoutInflater().inflate(R.layout.dynamic_show_order, null);
        TextView orderName = ((TextView) mConvertView.findViewById(R.id.orderName));
        TextView waterQty = ((TextView) mConvertView.findViewById(R.id.waterQty));
        TextView bottleQty = ((TextView) mConvertView.findViewById(R.id.bottleQty));
        TextView orderRate = ((TextView) mConvertView.findViewById(R.id.orderRate));
        orderName.setText(name/*+" (\u20B9"+rate+")"*/);
        waterQty.setText(String.valueOf(wQty));
        bottleQty.setText(String.valueOf(bQty));
        orderRate.setText(String.valueOf(rate));
        ((ViewGroup) viewHolder.parentLayout).addView(mConvertView);
    }

    void removeAllViews(HistoryListAdapter.ViewHolder viewHolder) {
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
        @Bind(R.id.cancel)
        TextView cancel;
        @Bind(R.id.reason)
        TextView reason;


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

}
