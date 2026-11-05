package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.bean.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * 收货地址列表适配器。
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    private final List<Address> items = new ArrayList<>();
    private final Listener listener;

    public interface Listener {
        void onAddressClick(Address address);

        void onEdit(Address address);

        void onDelete(Address address);

        void onSetDefault(Address address);
    }

    public AddressAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<Address> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
        Address address = items.get(position);
        holder.tvName.setText(address.getName());
        holder.tvPhone.setText(maskPhone(address.getPhone()));
        holder.tvDetail.setText(address.getDetail());
        holder.tvDefault.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        holder.btnSetDefault.setVisibility(address.isDefault() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressClick(address);
            }
        });
        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefault(address);
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(address);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    static class AddressHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvDetail;
        final TextView tvDefault;
        final TextView btnSetDefault;
        final TextView btnEdit;
        final TextView btnDelete;

        AddressHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_address_name);
            tvPhone = itemView.findViewById(R.id.tv_address_phone);
            tvDetail = itemView.findViewById(R.id.tv_address_detail);
            tvDefault = itemView.findViewById(R.id.tv_address_default);
            btnSetDefault = itemView.findViewById(R.id.btn_set_default);
            btnEdit = itemView.findViewById(R.id.btn_edit_address);
            btnDelete = itemView.findViewById(R.id.btn_delete_address);
        }
    }
}
