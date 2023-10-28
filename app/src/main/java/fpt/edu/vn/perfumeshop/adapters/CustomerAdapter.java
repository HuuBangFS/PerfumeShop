package fpt.edu.vn.perfumeshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.constants.AppConstants;
import fpt.edu.vn.perfumeshop.models.Customer;

public class CustomerAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Customer> customerList;

    public CustomerAdapter(Context context, int layout, ArrayList<Customer> customerList) {
        this.context = context;
        this.layout = layout;
        this.customerList = customerList;
    }

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        // bindings view
        ImageView avatar = view.findViewById(R.id.imgAvatar);
        TextView cusName = view.findViewById(R.id.txtCustomerName);
        TextView cusEmail = view.findViewById(R.id.txtCustomerEmail);

        Customer customer =(Customer)getItem(position);
        if (!customer.getAvatar().equals(AppConstants.DEFAULT_AVATAR)) {
            Picasso.get().load(customer.getAvatar()).into(avatar);
        }
        cusName.setText(customer.getCustomerName());
        cusEmail.setText(customer.getEmail());
        return view;
    }
}
