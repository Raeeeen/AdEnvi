package com.rod.adenvi.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.rod.adenvi.Domains.ItemsListDomain;
import com.rod.adenvi.Fragments.HomeFragment;
import com.rod.adenvi.Fragments.ItemInfoFragment;
import com.rod.adenvi.Fragments.ItemListFragment;
import com.rod.adenvi.R;

import java.util.ArrayList;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private ArrayList<ItemsListDomain> itemsListDomains;
    private String departmentName;
    private String categoryName;
    private String currentUsername;

    public ItemListAdapter(ArrayList<ItemsListDomain> itemsListDomains, String departmentName, String categoryName, String currentUsername) {
        this.itemsListDomains = itemsListDomains;
        this.departmentName = departmentName;
        this.categoryName = categoryName;
        this.currentUsername = currentUsername;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist_viewholder, parent, false);
        return new ItemListAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        String itemsText = itemsListDomains.get(position).getTitle();
        holder.itemssName.setText(itemsText);
    }

    @Override
    public int getItemCount() {
        return itemsListDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemssName;
        ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemssName = itemView.findViewById(R.id.itemssName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemsName = itemssName.getText().toString();

                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentusername", currentUsername);
                    editor.putString("itemsName", itemsName);
                    editor.apply();

                    Context context = itemView.getContext();
                    // Create a new ItemInfoFragment instance //
                    ItemInfoFragment itemInfoFragment = new ItemInfoFragment();
                    // Get the FragmentManager //
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    // Start a FragmentTransaction //
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // Replace the current fragment with the ItemListFragment //
                    transaction.replace(R.id.fragment_container, itemInfoFragment);
                    transaction.commit();
                }
            });



        }

    }




}
