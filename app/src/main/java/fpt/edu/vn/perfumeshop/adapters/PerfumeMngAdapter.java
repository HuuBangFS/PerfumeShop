package fpt.edu.vn.perfumeshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.activities.InsertUpdatePerfumeActivity;
import fpt.edu.vn.perfumeshop.constants.AppConstants;
import fpt.edu.vn.perfumeshop.models.Perfume;

public class PerfumeMngAdapter extends BaseAdapter {
    private ArrayList<Perfume> perfumeList;
    private int layout;
    private Context context;

    public PerfumeMngAdapter(ArrayList<Perfume> perfumeList, int layout, Context context) {
        this.perfumeList = perfumeList;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public int getCount() {
        return perfumeList.size();
    }

    @Override
    public Object getItem(int position) {
        return perfumeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return perfumeList.get(position).getId();
    }
    public class ViewHolder {
        ImageView imgPerfume, imgEdit;
        TextView txtName, txtPrice, txtUnitInStock, txtDescribe;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);

            holder.imgPerfume = view.findViewById(R.id.imgPerfume);
            holder.imgEdit = view.findViewById(R.id.imgEditPerfume);
            holder.txtName = view.findViewById(R.id.txtPerfumeName);
            holder.txtDescribe = view.findViewById(R.id.txtDescribePerfume);
            holder.txtPrice = view.findViewById(R.id.txtPerfumePrice);
            holder.txtUnitInStock = view.findViewById(R.id.txtUnitInStock);


        Perfume perfume = perfumeList.get(position);
        holder.txtName.setText(perfume.getPerfumeName());
        holder.txtPrice.setText(perfume.getUnitPrice()+"");
        holder.txtDescribe.setText(perfume.getDescription());
        holder.txtUnitInStock.setText(perfume.getUnitInStock()+"");
        if (!perfume.getImageUrl().isEmpty()) {
            Picasso.get().load(perfume.getImageUrl()).into(holder.imgPerfume);
        }

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InsertUpdatePerfumeActivity.class);
                intent.putExtra(AppConstants.UPDATED_PERFUME, (Serializable) perfume);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
