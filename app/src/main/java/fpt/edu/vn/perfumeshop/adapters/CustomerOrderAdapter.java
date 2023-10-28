package fpt.edu.vn.perfumeshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.dao.OrderViewDao;

public class CustomerOrderAdapter extends  RecyclerView.Adapter<CustomerOrderAdapter.MyViewHolder> {
    private Context context;
    private List<OrderViewDao> orderList;

    public CustomerOrderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String total = String.valueOf(orderList.get(position).getTotal());
        holder.total.setText(total);
        holder.orderStatus.setText(orderList.get(position).getOrderStatus());
        holder.customerName.setText(orderList.get(position).getCustomerName());
    }
    public void setOrderList(List<OrderViewDao> orderList){
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        }
        return orderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customerName,total,orderStatus;
        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tvCustomerName);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            total = itemView.findViewById(R.id.tvTotal);
        }
    }
}
