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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Domains.ItemsDomain;
import com.rod.adenvi.Fragments.CategoryListFragment;
import com.rod.adenvi.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<ItemsDomain> itemsDomains;
    private String currentUsername;

    public HomeAdapter(ArrayList<ItemsDomain> itemsDomains, String currentUsername) {
        this.itemsDomains = itemsDomains;
        this.currentUsername = currentUsername;
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_viewholder, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        String deptOfficeText = itemsDomains.get(position).getTitle();
        holder.deptofficeName.setText(deptOfficeText);


            DatabaseReference deptOfficeRef = FirebaseDatabase.getInstance().getReference()
                    .child("inventory")
                    .child(currentUsername)
                    .child(deptOfficeText);

            deptOfficeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int categoryCount = (int) dataSnapshot.getChildrenCount();
                        holder.categoryvalues.setText(String.valueOf(categoryCount));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }


    @Override
    public int getItemCount() {
        return itemsDomains.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deptofficeName;
        TextView categoryvalues;
        ImageButton removeDeptOffice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deptofficeName = itemView.findViewById(R.id.deptofficeName);
            categoryvalues = itemView.findViewById(R.id.categoryvalues);
            removeDeptOffice = itemView.findViewById(R.id.removeDeptOffice);

            removeDeptOffice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String departmentName = deptofficeName.getText().toString();

                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentusername", currentUsername);
                    editor.putString("departmentName", departmentName);
                    editor.apply();

                    Context context = itemView.getContext();
                    // Create a new CategoryListFragment instance //
                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                    // Get the FragmentManager //
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    // Start a FragmentTransaction //
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // Replace the current fragment with the CategoryListFragment //
                    transaction.replace(R.id.fragment_container, categoryListFragment);
                    transaction.commit();


                }

            });


        }

        private void deleteItem(int position) {


        }


    }







}
