package com.android.greena.awesomechat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.greena.awesomechat.R;
import com.android.greena.awesomechat.model.User;
import com.android.greena.awesomechat.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends AppCompatActivity {

    private String                      mUserName;

    private FirebaseAuth                mAuth;
    private DatabaseReference           mUsersDbReference;
    private ChildEventListener          mUsersChildEventListener;

    private Toolbar                     mToolBar;
    private List<User>                  mUsers;
    private RecyclerView                mUserRecyclerView;
    private UserAdapter                 mUserAdapter;
    private RecyclerView.LayoutManager  mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        if (intent != null)
            mUserName = intent.getStringExtra(mUserName);

        mToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("ChatOberts");

        mAuth = FirebaseAuth.getInstance();
        mUsers = new ArrayList<>();
        buildRecyclerView();
        attachUserDatabaseListener();
    }

    private void attachUserDatabaseListener() {
        mUsersDbReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (mUsersChildEventListener == null) {
            mUsersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        user.setAvatarMockUpResources(R.drawable.person);
                        mUsers.add(user);
                        System.out.println("add users");
                        mUserAdapter.notifyDataSetChanged();
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

        mUsersDbReference.addChildEventListener(mUsersChildEventListener);
    }

    private void buildRecyclerView() {
        mUserRecyclerView = findViewById(R.id.userListRecyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.addItemDecoration(new DividerItemDecoration(mUserRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mLayoutManager = new LinearLayoutManager(this);
        mUserAdapter = new UserAdapter(mUsers);

        mUserRecyclerView.setLayoutManager(mLayoutManager);
        mUserRecyclerView.setAdapter(mUserAdapter);

        mUserAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClickListener(int pos) {
                goToChat(pos);
            }
        });
    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("recipientUserId", mUsers.get(position).getId());
        intent.putExtra("recipientName", mUsers.get(position).getName());
        intent.putExtra("userName", mUserName);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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