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
import org.pub.girlview.domain.Girl;
import org.pub.girlview.listener.OnItemClickListener;

import java.util.List;

/**
 * Gril Adapter
 */
public class GirlAdapter extends RecyclerView.Adapter<GirlAdapter.MyViewHolder> {

    private OnItemClickListener itemClickListener;

    private List<Girl> data;

    public GirlAdapter(List<Girl> data) {
        this.data = data;
    }

    public void setData(List<Girl> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girl_info, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(data.get(position).getSrc()).into(holder.src);
        holder.name.setText(data.get(position).getName());
        holder.desc.setText(data.get(position).getDesc());

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

        public ImageView src;
        public TextView name;
        public TextView desc;

        public MyViewHolder(View view) {
            super(view);
            src = view.findViewById(R.id.src);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.desc);
        }
    }
}
