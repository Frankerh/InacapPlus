package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Home;
import com.example.myapplication.Perfil;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Chat extends AppCompatActivity {

    private ScrollView messageScrollView;
    private LinearLayout messageContainer;
    private EditText messageEditText;
    private Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        // Inicializa las vistas
        messageScrollView = findViewById(R.id.messagescrollView);
        messageContainer = findViewById(R.id.messageContainer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Configura el evento clic para el botón de enviar
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Configura la navegación desde la actividad de chat
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    Intent homeIntent = new Intent(Chat.this, Home.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_chat) {
                    // No hagas nada, ya estás en la actividad Chat
                } else if (itemId == R.id.action_profile) {
                    // Abre la actividad Perfil
                    Intent profileIntent = new Intent(Chat.this, Perfil.class);
                    startActivity(profileIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
            }
        });

        // Marcar la opción de perfil como seleccionada al inicio
        bottomNavigationView.setSelectedItemId(R.id.action_chat);
    }

    // Método para enviar un mensaje
    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (!messageText.isEmpty()) {
            // Crea una vista de mensaje enviado
            View sentMessageView = getLayoutInflater().inflate(R.layout.item_sent_message, null);
            TextView sentMessageTextView = sentMessageView.findViewById(R.id.sentMessageTextView);
            sentMessageTextView.setText(messageText);

            // Agrega la vista al contenedor de mensajes
            messageContainer.addView(sentMessageView);

            // Limpia el campo de entrada
            messageEditText.setText("");

            // Desplázate automáticamente hacia abajo para mostrar el nuevo mensaje
            messageScrollView.post(new Runnable() {
                @Override
                public void run() {
                    messageScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}
