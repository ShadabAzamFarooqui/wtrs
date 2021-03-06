package com.example.berylsystems.watersupply.adapter;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berylsystems.watersupply.R;
import com.example.berylsystems.watersupply.activities.OrderActivity;
import com.example.berylsystems.watersupply.bean.Bottle;
import com.example.berylsystems.watersupply.bean.Water;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmptyBottleAdapter extends RecyclerView.Adapter<EmptyBottleAdapter.ViewHolder> {

    private List<String> data;
    private Context context;
    int mInteger = 0, totalAmount;
    OrderActivity object;
    public static Map<Integer, Bottle> map;
    LinearLayout coordinatorLayout;

    public EmptyBottleAdapter(Context context, List<String> data,LinearLayout coordinatorLayout) {
        this.data = data;
        this.context = context;
        object = OrderActivity.context;
        map = new HashMap<>();
        this.coordinatorLayout=coordinatorLayout;
    }


    @Override
    public EmptyBottleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_empty_bottle_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmptyBottleAdapter.ViewHolder viewHolder, int position) {
        mInteger = 0;
        String[] arr = data.get(position).split(",");
        viewHolder.water_type.setText(arr[0]);
        viewHolder.water_rate.setText("(\u20B9" + arr[2] + ")");
        // viewHolder.water_type.setText(data.get(position).split(",")[0]+" ( ₹ "+data.get(position).split(",")[1]+")");

        viewHolder.layout_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (WaterDetailAdapter.map.get(position) == null) {
                    Snackbar.make(coordinatorLayout, "Please the water first", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (WaterDetailAdapter.map.get(position).getQty() <= Integer.valueOf(viewHolder.mQuantity.getText().toString())) {
                    Snackbar.make(coordinatorLayout, "You can't select empty bottle more than water", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mInteger = Integer.parseInt(viewHolder.mQuantity.getText().toString());
                mInteger = mInteger + 1;
                viewHolder.mQuantity.setText("" + mInteger);
                object.setTotal("" + (Double.valueOf(object.getTotal()) + Double.valueOf(arr[2])));
                Bottle bottle = new Bottle();
                bottle.setName(arr[0]);
                bottle.setRate(Double.valueOf(arr[2]));
                bottle.setQty(Integer.valueOf(mInteger));
                map.put(position, bottle);
            }
        });

        viewHolder.layout_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInteger = Integer.parseInt(viewHolder.mQuantity.getText().toString());
                if (mInteger > 0) {
                    mInteger = mInteger - 1;
                    viewHolder.mQuantity.setText("" + mInteger);
                    object.setTotal("" + (Double.valueOf(object.getTotal()) - Double.valueOf(arr[2])));
                    Bottle bottle = new Bottle();
                    bottle.setName(arr[0]);
                    bottle.setRate(Double.valueOf(arr[2]));
                    bottle.setQty(Integer.valueOf(arr[1]));
                    map.put(position, bottle);
                    if (viewHolder.mQuantity.getText().toString().equals("0")) {
                        map.remove(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.water_type)
        TextView water_type;
        @Bind(R.id.water_rate)
        TextView water_rate;
        @Bind(R.id.layout_minus)
        TextView layout_minus;
        @Bind(R.id.layout_plus)
        TextView layout_plus;
        @Bind(R.id.text_quantity)
        TextView mQuantity;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
