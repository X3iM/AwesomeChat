package com.android.greena.awesomechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private String              userName;

    private FirebaseAuth        auth;
    private DatabaseReference   usersDbReference;
    private ChildEventListener  usersChildEventListener;

    private List<User>          users;
    private RecyclerView        userRecyclerView;
    private UserAdapter         userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        if (intent != null)
            userName = intent.getStringExtra(userName);

        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        buildRecyclerView();
        attachUserDatabaseListener();
    }

    private void attachUserDatabaseListener() {
        usersDbReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (usersChildEventListener == null) {
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(auth.getCurrentUser().getUid())) {
                        user.setAvatarMockUpResources(R.drawable.person);
                        users.add(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            };
        }

        usersDbReference.addChildEventListener(usersChildEventListener);
    }

    private void buildRecyclerView() {
        userRecyclerView = findViewById(R.id.userListRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(userRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(users);

        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClickListener(int pos) {
                goToChat(pos);
            }
        });
    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("recipientUserId", users.get(position).getId());
        intent.putExtra("userName", userName);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserListActivity.this, LogInActivity.class));
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
}