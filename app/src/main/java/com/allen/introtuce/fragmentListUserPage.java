package com.allen.introtuce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class fragmentListUserPage extends Fragment {

    private RecyclerView rv;
    public static final String TITLE = "Profile";

    public static fragmentListUserPage newInstance() {

        return new fragmentListUserPage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_user_page, container, false);

        rv = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(mLayoutManager);

        final DatabaseReference nm = FirebaseDatabase.getInstance("https://introtuce-officiallygod-default-rtdb" +
                ".firebaseio.com/").getReference().child("Users");
        nm.keepSynced(true);

        nm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    FirebaseRecyclerOptions<List_Data> options =
                            new FirebaseRecyclerOptions.Builder<List_Data>().setQuery(nm, List_Data.class).build();

                    FirebaseRecyclerAdapter<List_Data, UserHolder> adapter =
                            new FirebaseRecyclerAdapter<List_Data, UserHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull List_Data model) {
                                    final String userID = getRef(position).getKey();

                                    assert userID != null;
                                    nm.child(userID).addValueEventListener(new ValueEventListener(){

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                holder.textName.setText(model.getFirst_name() + " " + model.getLast_name());
                                                holder.textDesc.setText(model.getHometown() + " | " + model.getPhone());
                                                Picasso.get().load(model.getPhoto()).into(holder.imageView);

                                                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Log.d("DEBUG", "onClick: " + position +
                                                                " " + userID);

                                                        nm.child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data, parent,
                                            false);
                                    UserHolder viewHolder = new UserHolder(view);
                                    return viewHolder;
                                }
                            };
                    rv.setAdapter(adapter);
                    adapter.startListening();
                }
                else {
                    Toast.makeText(getActivity(), "There is no Data SED ;(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    public static class UserHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageView;
        private ImageView deleteBtn;
        private TextView textName, textDesc;
        public UserHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.mainName);
            textDesc = itemView.findViewById(R.id.mainDesc);
            imageView = itemView.findViewById(R.id.imageProfile);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}