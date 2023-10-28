package fpt.edu.vn.perfumeshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.models.Cart;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private Context context;
    private List<Cart> perfumeList;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout,
                            viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.perfumeName.setText(perfumeList.get(position).getPerfumeName());
        String unitPrice = String.valueOf(perfumeList.get(position).getUnitPrice());
        holder.unitPrice.setText("$ " + unitPrice);
        String quantity = String.valueOf(perfumeList.get(position).getQuantity());
        holder.quantity.setText(quantity);
        Picasso.get().load(perfumeList.get(position).getImageUrl()).into(holder.img);
    }

    public List<Cart> getPerfumeList() {
        return perfumeList;
    }

    public void setPerfumeList(List<Cart> perfumeList) {
        this.perfumeList = perfumeList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (perfumeList == null) {
            return 0;
        }
        return perfumeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView perfumeName, unitPrice,quantity;
        ImageView img;
        MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            perfumeName = itemView.findViewById(R.id.textViewPerfumeName);
            unitPrice = itemView.findViewById(R.id.textViewUnitPrice);
            quantity = itemView.findViewById(R.id.textViewQuantity);
            img = itemView.findViewById(R.id.imageCart);
        }
    }
}
