package org.pub.girlview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pub.girlview.R;
import org.pub.girlview.domain.Item;
import org.pub.girlview.listener.OnItemClickListener;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private OnItemClickListener itemClickListener;

    private List<Item> data;

    public ItemAdapter(List<Item> data) {
        this.data = data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girl_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(data.get(position).getSrc()).into(holder.src);
        holder.title.setText(data.get(position).getTitle());

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener((View view) -> {
                int pos = holder.getLayoutPosition();
                itemClickListener.onItemClick(holder.itemView, pos);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * MyView Holder
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView src;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            src = view.findViewById(R.id.src);
        }
    }
}
