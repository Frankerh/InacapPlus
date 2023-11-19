package com.example.myapplication;
import com.example.myapplication.ChatMessage;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChatPriv extends AppCompatActivity {
    private FirebaseFirestore db;
    private String myUid;
    private String otherUid;
    private String otherUserName;
    List<ChatMessage> messageList = new ArrayList<>();
    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatpriv);

        db = FirebaseFirestore.getInstance();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUid = getIntent().getStringExtra("otherUid");

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        setToolbarTitle("Cargando..."); // Establece un título temporal mientras se carga el nombre del usuario

        sendButton.setOnClickListener(view -> sendMessage());

        // Obtener el nombre del usuario al que estás chateando
        db.collection("Users")
                .document(otherUid) // Utiliza el otherUid para obtener los datos del usuario
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            otherUserName = document.getString("name");
                            setToolbarTitle(otherUserName); // Establece el nombre como título del Toolbar
                        }
                    }
                });

        db.collection("ChatMessages")
                .whereEqualTo("senderUid", myUid)
                .whereEqualTo("receiverUid", otherUid)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Manejar errores
                        return;
                    }

                    messageList.clear(); // Limpia la lista antes de agregar nuevos mensajes

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ChatMessage message = doc.toObject(ChatMessage.class);  // Asegúrate de usar ChatMessage
                        messageList.add(message);
                    }
                    chatAdapter.notifyDataSetChanged();
                });
    }

    private void setToolbarTitle(String title) {
        MaterialToolbar chatToolbar = findViewById(R.id.chatToolbar);
        chatToolbar.setTitle(title);
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            ChatMessage message = new ChatMessage(myUid, otherUid, messageText, Timestamp.now());

            db.collection("ChatMessages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        messageEditText.setText("");
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error al enviar el mensaje
                    });
        }
    }
}
