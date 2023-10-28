package fpt.edu.vn.perfumeshop;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fpt.edu.vn.perfumeshop.models.Perfume;

public class ListAdapter extends BaseAdapter {

    private PerfumesList context;
    private int layout;
    private ArrayList<Perfume> perfumeArrayList;

    public ListAdapter(PerfumesList context, int layout, ArrayList<Perfume> perfumeArrayList) {
        this.context = context;
        this.layout = layout;
        this.perfumeArrayList = perfumeArrayList;
    }

    @Override
    public int getCount() {
        return perfumeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView tvName, tvDescription, tvPrice;
        ImageView imgAddToCart, imgThumbnail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            holder.tvName = convertView.findViewById(R.id.tvName);
            //holder.tvDescription = convertView.findViewById(R.id.tvShortDescription);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.imgAddToCart = convertView.findViewById(R.id.imageviewAddToCart);
            holder.imgThumbnail = convertView.findViewById(R.id.imgThubnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Perfume perfume = perfumeArrayList.get(position);
        holder.tvName.setText(perfume.getPerfumeName());
//      holder.tvDescription.setText(perfume.getDescription());
        holder.tvPrice.setText("$ " + perfume.getUnitPrice());
        if (!perfume.getImageUrl().trim().isEmpty()) {
            Picasso.get().load(perfume.getImageUrl()).into(holder.imgThumbnail);
        }

        //Event handler for add a perfume to cart
        holder.imgAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PerfumeDetailActivity.class);
                Log.d("myTag", "onClick() returned: " + perfumeArrayList.get(position).getId());
//                intent.putExtra("choosenPerfume", perfumeArrayList.get(position));
                intent.putExtra("id", perfumeArrayList.get(position).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}