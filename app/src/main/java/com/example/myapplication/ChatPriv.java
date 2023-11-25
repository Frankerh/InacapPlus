package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Messager;
import com.example.myapplication.MessagerAdapter;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatPriv extends AppCompatActivity {
    private RecyclerView messageRecyclerView;
    private List<Messager> messageList;
    private MessagerAdapter messageAdapter;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatpriv);

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = getIntent().getStringExtra("uid");

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageList = new ArrayList<>();
        messageAdapter = new MessagerAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(messageAdapter);

        Button sendButton = findViewById(R.id.sendButton);
        EditText messageEditText = findViewById(R.id.messageEditText);

        toolbar.setTitle("Nombre del usuario seleccionado");

        sendButton.setOnClickListener(view -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });

        loadMessages();

        db.collection("messages")
                .whereEqualTo("senderId", currentUser.getUid())
                .whereEqualTo("receiverId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Manejar errores
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Messager message = dc.getDocument().toObject(Messager.class);
                            messageList.add(message);
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void loadMessages() {
        db.collection("messages")
                .whereEqualTo("senderId", currentUser.getUid())
                .whereEqualTo("receiverId", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Messager message = document.toObject(Messager.class);
                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        // Manejar errores al cargar mensajes
                    }
                });
    }

    private void sendMessage(String messageText) {
        Messager message = new Messager(messageText, currentUser.getUid(), uid);
        db.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al enviar mensaje
                });
    }
}
