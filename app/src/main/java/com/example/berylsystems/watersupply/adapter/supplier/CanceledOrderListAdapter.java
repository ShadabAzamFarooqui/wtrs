package com.example.berylsystems.watersupply.adapter.supplier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.bean.Combine;
import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.fragment.supplier.CancelOrderFragment;
import com.example.berylsystems.watersupply.fragment.supplier.DeliveredOrderFragment;
import com.example.berylsystems.watersupply.fragment.supplier.PendingOrderFragment;
import com.example.berylsystems.watersupply.utils.Helper;
import com.example.berylsystems.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CanceledOrderListAdapter extends RecyclerView.Adapter<CanceledOrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;
    ProgressDialog mProgressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public CanceledOrderListAdapter(Activity context, List<OrderBean> data, SwipeRefreshLayout swipeRefreshLayout) {
        this.data = data;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;

    }


    @Override
    public CanceledOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_canceled_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CanceledOrderListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.shopName.setText(data.get(position).getSupplier().getShopName());
        viewHolder.bookingTime.setText(data.get(position).getBookingDate());
        String[] dateAr = data.get(position).getDeliveryDate().split(" ");
        viewHolder.day.setText(dateAr[0]);
        viewHolder.month.setText(dateAr[1]);
        viewHolder.year.setText(dateAr[2]);
        viewHolder.supplierName.setText(data.get(position).getSupplier().getName());
        viewHolder.supplierMobile.setText(data.get(position).getUser().getMobile());
        viewHolder.address.setText(data.get(position).getAddress());
        viewHolder.total.setText(data.get(position).getAmount());
        viewHolder.orderId.setText(data.get(position).getOrderId().toUpperCase());
        viewHolder.reason.setText(data.get(position).getReason());
        if (data.get(position).getComment().trim().isEmpty()) {
            viewHolder.comment.setVisibility(View.GONE);
        } else {
            viewHolder.comment.setVisibility(View.VISIBLE);
            viewHolder.comment.setText(data.get(position).getComment());
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
        viewHolder.deliver_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(position, "Cancel Confirmation", "Do you want to cancel ?", ParameterConstants.PENDING);
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
        waterQty.setText(String.valueOf(wQty));
        bottleQty.setText(String.valueOf(bQty));
        orderRate.setText(String.valueOf(rate));
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
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.total)
        TextView total;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.orderId)
        TextView orderId;
        @Bind(R.id.parentLayout)
        LinearLayout parentLayout;
        @Bind(R.id.deliver_layout)
        LinearLayout deliver_layout;
        @Bind(R.id.reason)
        TextView reason;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);

        }
    }


    void dialog(int position, String tittle, String message, String status) {
        new AlertDialog.Builder(context)
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (!Helper.isNetworkAvailable(context)) {
                        Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Please wait...");
                    mProgressDialog.setCancelable(false);
//                    swipeRefreshLayout.setRefreshing(true);
                    mProgressDialog.show();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                    OrderBean orderBean = data.get(position);
                    orderBean.setStatus(status);
                    database.child(data.get(position).getOrderId()).setValue(orderBean, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mProgressDialog.dismiss();
                            if (databaseError == null) {
//                                new NetworkAsyncTask(orderBean.getUser().getRefreshToken(),"canceled","Share Location").execute();
//                                swipeRefreshLayout.setRefreshing(false);
                                CancelOrderFragment.orderBeanList.remove(orderBean);
                                CancelOrderFragment.mAdapter.notifyDataSetChanged();
                                PendingOrderFragment.orderBeanList.add(orderBean);
                                PendingOrderFragment.mAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
