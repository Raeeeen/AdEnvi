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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Domains.CategoryListsDomain;
import com.rod.adenvi.Fragments.ItemListFragment;
import com.rod.adenvi.R;

import java.util.ArrayList;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private ArrayList<CategoryListsDomain> categoryListsDomains;
    private  String departmentName;
    private String currentUsername;

    public CategoryListAdapter(ArrayList<CategoryListsDomain> categoryListsDomains, String departmentName, String currentUsername) {
        this.categoryListsDomains = categoryListsDomains;
        this.departmentName = departmentName;
        this.currentUsername = currentUsername;
    }

    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorylist_viewholder, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.ViewHolder holder, int position) {
        String categoryText = categoryListsDomains.get(position).getTitle();
        holder.category.setText(categoryText);

            DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("inventory")
                    .child(currentUsername)
                    .child(departmentName)
                    .child(categoryText);

            categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int itemscount = (int) snapshot.getChildrenCount();
                        holder.itemvalues.setText(String.valueOf(itemscount));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }



    @Override
    public int getItemCount() {
        return categoryListsDomains.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView category;
        TextView itemvalues;
        ImageButton deletebtnCategory;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category);
            itemvalues = itemView.findViewById(R.id.itemvalues);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String categoryName = category.getText().toString();

                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentusername", currentUsername);
                    editor.putString("categoryName", categoryName);
                    editor.apply();

                    Context context = itemView.getContext();
                    // Create a new ItemListFragment instance //
                    ItemListFragment itemListFragment = new ItemListFragment();
                    // Get the FragmentManager //
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    // Start a FragmentTransaction //
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // Replace the current fragment with the ItemListFragment //
                    transaction.replace(R.id.fragment_container, itemListFragment);
                    transaction.commit();

                }
            });


        }
    }



}
