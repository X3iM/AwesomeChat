package com.android.greena.awesomechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static final int     RC_IMAGE_PICKER = 123;

    private MessageAdapter      adapter;

    private ListView            listView;
    private ProgressBar         progressBar;
    private ImageButton         sendPhotoButton;
    private EditText            messageEditText;
    private Button              sendMessageButton;

    private String              userName;
    private String              recipientUserId;

    private FirebaseAuth        auth;

    private FirebaseStorage     storage;
    private StorageReference    storageReference;

    private FirebaseDatabase    database;
    private DatabaseReference   databaseReference;
    private ChildEventListener  childEventListener;

    private DatabaseReference   usersDatabaseReference;
    private ChildEventListener  usersChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
        }
        else
            userName = "Default User";

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("messages");

        usersDatabaseReference = database.getReference().child("users");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("chat_images");

        auth = FirebaseAuth.getInstance();

        adapter = new MessageAdapter(this, R.layout.message_item, new ArrayList<Message>());
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        sendPhotoButton = findViewById(R.id.sendPhotoButton);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) sendMessageButton.setEnabled(true);
                else sendMessageButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        messageEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(500)});

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setName(userName);
                message.setText(messageEditText.getText().toString());
                message.setSender(auth.getCurrentUser().getUid());
                message.setRecipient(recipientUserId);

                databaseReference.push().setValue(message);
                messageEditText.setText("");
            }
        });

        sendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
            }
        });

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    userName = user.getName();
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

        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                if (message .getSender().equals(auth.getCurrentUser().getUid()) && message.getRecipient().equals(recipientUserId)) {
                    message.setMine(false);
                    adapter.add(message);
                } else if (message .getSender().equals(recipientUserId) && message.getRecipient().equals(auth.getCurrentUser().getUid())) {
                    message.setMine(true);
                    adapter.add(message);
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

        databaseReference.addChildEventListener(childEventListener);
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
            startActivity(new Intent(ChatActivity.this, LogInActivity.class));
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri imageUti = data.getData();
            final StorageReference imageRef = storageReference.child(imageUti.getLastPathSegment());

            UploadTask uploadTask = imageRef.putFile(imageUti);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Message message = new Message();

                        message.setImageUrl(downloadUri.toString());
                        message.setName(userName);
                        message.setSender(auth.getCurrentUser().getUid());
                        message.setRecipient(recipientUserId);
                        databaseReference.push().setValue(message);
                    }
                }
            });
        }

    }
}