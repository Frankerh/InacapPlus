package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<User> userList;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        userAdapter = new UserAdapter(userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Buscar usuarios");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    Intent homeIntent = new Intent(UserSearchActivity.this, Home.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_chat) {
                    Intent chatIntent = new Intent(UserSearchActivity.this, Chat.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_profile) {
                    // Abre la actividad Perfil
                    Intent profileIntent = new Intent(UserSearchActivity.this, Perfil.class);
                    startActivity(profileIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
            }
        });

        // Marcar la opción de perfil como seleccionada al inicio
        bottomNavigationView.setSelectedItemId(R.id.action_search);

        // Escuchar el evento de cierre del SearchView
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Ocultar solo el RecyclerView cuando se cierra la búsqueda
                recyclerView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        //action_search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    // Agregar la función onNavigationItemSelected para la opción de búsqueda en el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        // Agregar listener al SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        // Agregar listener al ícono de cierre (x) en el SearchView
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Ocultar solo el RecyclerView cuando se cierra la búsqueda
                recyclerView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        return true;
    }

    private void performSearch(String query) {
        userList.clear(); // Limpia la lista antes de agregar nuevos resultados
        userAdapter.notifyDataSetChanged();
        db.collection("Users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThan("name", query + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                // Evita agregar usuarios duplicados
                                if (!userList.contains(user)) {
                                    userList.add(user);
                                }
                            }
                            userAdapter.notifyDataSetChanged();

                            // Hacer visible el RecyclerView cuando haya resultados
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

        db.collection("Users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThan("name", query + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                // Comprueba si la imagen del usuario está disponible en Firestore
                                if (document.contains("profileImageUrl")) {
                                    String imageUrl = document.getString("profileImageUrl");
                                    // Agrega la URL de la imagen al usuario
                                    user.setProfileImageUrl(imageUrl);
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
