package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.User;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        // Inicializar la lista de usuarios
        userList = new ArrayList<>();

        // Inicializar el RecyclerView
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setAdapter(userAdapter);

        // Obtener los datos de los usuarios desde Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String profileImageUrl = document.getString("profileImageUrl");

                            // Crear un objeto User con los datos
                            User user = new User(name, profileImageUrl);

                            // Agregar el usuario a la lista
                            userList.add(user);
                        }

                        // Notificar al adaptador que se han actualizado los datos
                        userAdapter.notifyDataSetChanged();
                    }
                });

        // Configurar la navegación desde la actividad de chat
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_chat) {
                    item.setChecked(true);
                } else if (itemId == R.id.action_home) {
                    Intent chatIntent = new Intent(Chat.this, Home.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_profile) {
                    Intent homeIntent = new Intent(Chat.this, Perfil.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else if (itemId == R.id.action_search) {
                    Intent homeIntent = new Intent(Chat.this, UserSearchActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_chat);
    }
}

