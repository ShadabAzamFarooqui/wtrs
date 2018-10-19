package com.android.app.watersupply.adapter.supplier;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.SupplierHomeActivity;
import com.android.app.watersupply.bean.Combine;
import com.android.app.watersupply.bean.OrderBean;
import com.android.app.watersupply.fragment.supplier.CancelOrderFragment;
import com.android.app.watersupply.fragment.supplier.DeliveredOrderFragment;
import com.android.app.watersupply.fragment.supplier.DispatchOrderFragment;
import com.android.app.watersupply.fragment.supplier.PendingOrderFragment;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PendingOrderListAdapter extends RecyclerView.Adapter<PendingOrderListAdapter.ViewHolder> {

    private List<OrderBean> data;
    private Activity context;
    View mConvertView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog mProgressDialog;

    public PendingOrderListAdapter(Activity context, List<OrderBean> data, SwipeRefreshLayout swipeRefreshLayout) {
        this.data = data;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }


    @Override
    public PendingOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pending_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingOrderListAdapter.ViewHolder viewHolder, int position) {
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
                dialog(position, "Delivery Confirmation", "Are you sure you have delivered this order ?", ParameterConstants.DELIVER, ParameterConstants.DELIVER);
            }
        });

        viewHolder.dispatch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(position, "Dispatch Confirmation", "Are you sure this order has been dispatch ?", ParameterConstants.DISPATCH, ParameterConstants.DISPATCH);
            }
        });
        viewHolder.cancel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(position);
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
        @Bind(R.id.dispatch_layout)
        LinearLayout dispatch_layout;
        @Bind(R.id.cancel_layout)
        LinearLayout cancel_layout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);

        }
    }

    void dialog(int position, String tittle, String message, String status, String from) {
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
                    mProgressDialog.show();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                    OrderBean orderBean = data.get(position);
                    orderBean.setStatus(status);
                    database.child(data.get(position).getOrderId()).setValue(orderBean, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mProgressDialog.dismiss();
                            if (databaseError == null) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
//                              new NetworkAsyncTask(orderBean.getUser().getRefreshToken(),"delivered","Share Location").execute();
                                swipeRefreshLayout.setRefreshing(false);
                                if (from.equals(ParameterConstants.DELIVER)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    DeliveredOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    DeliveredOrderFragment.mAdapter.notifyDataSetChanged();
                                } else if (from.equals(ParameterConstants.DISPATCH)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    DispatchOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    DispatchOrderFragment.mAdapter.notifyDataSetChanged();
                                } else if (from.equals(ParameterConstants.CANCEL)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    CancelOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    CancelOrderFragment.mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void dialogCancel(int position, String tittle, String message, String status, String from, String reason) {
        dialog.dismiss();
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
                    mProgressDialog.show();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
                    OrderBean orderBean = data.get(position);
                    orderBean.setStatus(status);
                    orderBean.setReason(reason);
                    database.child(data.get(position).getOrderId()).setValue(orderBean, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mProgressDialog.dismiss();
                            if (databaseError == null) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
//                              new NetworkAsyncTask(orderBean.getUser().getRefreshToken(),"delivered","Share Location").execute();
                                swipeRefreshLayout.setRefreshing(false);
                                if (from.equals(ParameterConstants.DELIVER)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    DeliveredOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    DeliveredOrderFragment.mAdapter.notifyDataSetChanged();
                                } else if (from.equals(ParameterConstants.DISPATCH)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    DispatchOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    DispatchOrderFragment.mAdapter.notifyDataSetChanged();
                                } else if (from.equals(ParameterConstants.CANCEL)) {
                                    PendingOrderFragment.orderBeanList.remove(orderBean);
                                    CancelOrderFragment.orderBeanList.add(orderBean);
                                    PendingOrderFragment.mAdapter.notifyDataSetChanged();
                                    CancelOrderFragment.mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    Dialog dialog;

    void showPopup(int position) {
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_order_dailog);
        dialog.setCancelable(false);
        EditText message = (EditText) dialog.findViewById(R.id.message);
        TextView error_textView = (TextView) dialog.findViewById(R.id.error_textView);
        LinearLayout message_layout = (LinearLayout) dialog.findViewById(R.id.message_layout);
        Spinner reason = (Spinner) dialog.findViewById(R.id.spinner);
        Button submit = (Button) dialog.findViewById(R.id.dialogSubmit);
        LinearLayout close = (LinearLayout) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                message.setText(reason.getSelectedItem().toString());
                error_textView.setVisibility(View.GONE);
                if (i!=3){
                    message_layout.setVisibility(View.GONE);
                }else {
                    message_layout.setVisibility(View.VISIBLE);
                    message.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().isEmpty()){
                    message.setError("Please Enter the reason");
                    return;
                }
                if (message.getText().toString().equals("Select Reason")){
                    error_textView.setVisibility(View.VISIBLE);
                    return;
                }
                dialogCancel(position, "Cancel Confirmation", "Are you sure to cancel ?", ParameterConstants.CANCEL, ParameterConstants.CANCEL, message.getText().toString());
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Helper.closeKeyPad(context, SupplierHomeActivity.isKeyPadOpen);
            }
        });
    }
}
