package com.example.conatactmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private OnItemClickListener onItemClickListener;

    // Constructor to initialize the contact list
    public ContactAdapter(List<Contact> contactList, OnItemClickListener onItemClickListener) {
        this.contactList = contactList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eachitem, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhone());

        // Setting a click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(contact);
            }
        });

        // Setting click listener for the call icon
        holder.callIcon.setOnClickListener(v -> {
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onCallIconClick(contact);
            }
        });

        // Optionally, you can set up other click listeners if needed
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, phoneTextView;
        public CircleImageView profileImageView, callIcon;

        public ContactViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.name);
            phoneTextView = view.findViewById(R.id.phone);
            profileImageView = view.findViewById(R.id.profile_image);
            callIcon = view.findViewById(R.id.profile_image1);
        }
    }

    // Define an interface for item click events
    public interface OnItemClickListener {
        void onItemClick(Contact contact);
        void onCallIconClick(Contact contact);
    }
}
