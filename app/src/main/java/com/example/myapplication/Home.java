package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    private EditText plainTextThoughts;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PublicacionAdapter adapter;
    private List<Publicacion> publicacionesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        publicacionesList = new ArrayList<>();

        plainTextThoughts = findViewById(R.id.plainTextThoughts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                item.setChecked(true);
            } else if (itemId == R.id.action_chat) {
                Intent chatIntent = new Intent(Home.this, Chat.class);
                startActivity(chatIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (itemId == R.id.action_profile) {
                Intent homeIntent = new Intent(Home.this, Perfil.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (itemId == R.id.action_search) {
                Intent homeIntent = new Intent(Home.this, UserSearchActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        obtenerYMostrarNombreDeUsuario();

        RecyclerView recyclerView = findViewById(R.id.publicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Establecer el LinearLayoutManager
        adapter = new PublicacionAdapter(publicacionesList);
        recyclerView.setAdapter(adapter);

        cargarPublicaciones();

        findViewById(R.id.buttonPublish).setOnClickListener(v -> {
            String thoughts = plainTextThoughts.getText().toString().trim();
            if (!thoughts.isEmpty()) {
                guardarPublicacion(thoughts);
            } else {
                Toast.makeText(Home.this, "Ingresa algo antes de publicar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPublicaciones() {
        db.collection("Publicaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        publicacionesList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String usuario = document.getString("usuario");
                            String fecha = document.getString("fecha"); // Asegúrate de que coincida el nombre del campo
                            String opinion = document.getString("opinion");
                            Long likesLong = document.getLong("likes");
                            int likes = (likesLong != null) ? likesLong.intValue() : 0;

                            Publicacion publicacion = new Publicacion(usuario, fecha, opinion, likes);
                            publicacionesList.add(publicacion);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Home.this, "Error al cargar las publicaciones: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void guardarPublicacion(String thoughts) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String fechaActual = obtenerFechaActual(); // Obtener la fecha actual

            // Obtener el nombre del usuario desde Firestore
            db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String username = task.getResult().getString("name");
                    if (username != null) {
                        Map<String, Object> publicacion = new HashMap<>();
                        publicacion.put("opinion", thoughts);
                        publicacion.put("usuario", username); // Agregar el nombre del usuario
                        publicacion.put("fecha", fechaActual);

                        String publicacionId = db.collection("Publicaciones").document().getId();

                        db.collection("Publicaciones").document(publicacionId)
                                .set(publicacion)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Home.this, "Publicación exitosa", Toast.LENGTH_SHORT).show();
                                    plainTextThoughts.setText("");
                                    cargarPublicaciones();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(Home.this, "Error al publicar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }


    // Método para obtener la fecha actual
    private String obtenerFechaActual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    private void obtenerYMostrarNombreDeUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String usernameFirestore = task.getResult().getString("name");
                    String imageUrl = task.getResult().getString("profileImageUrl"); // Obtener la URL de la imagen del usuario

                    if (usernameFirestore != null && !usernameFirestore.isEmpty()) {
                        getSupportActionBar().setTitle("Bienvenido, " + usernameFirestore);
                    }

                    // Cargar la imagen usando alguna librería como Picasso o Glide
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        cargarImagenPerfil(imageUrl); // Método para cargar la imagen del usuario
                    }
                }
            });
        }
    }

    private void cargarImagenPerfil(String imageUrl) {
        // Usa Picasso o Glide para cargar la imagen del usuario en el CircleImageView
        // Por ejemplo, usando Picasso:
        CircleImageView profileImageView = findViewById(R.id.profileImageView);

        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_profile).into(profileImageView);
        // Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_profile).into(profileImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            actualizarEstado("desconectado", task -> {
                if (task.isSuccessful()) {
                    mAuth.signOut();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Home.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Home.this, "Error al actualizar el estado.", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actualizarEstado(String estado, OnCompleteListener<Void> onCompleteListener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> userUpdate = new HashMap<>();
            userUpdate.put("estado", estado);

            db.collection("Users").document(userId).update(userUpdate)
                    .addOnCompleteListener(onCompleteListener);
        }
    }
}
