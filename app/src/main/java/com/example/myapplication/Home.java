package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView usernameTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mAuth = FirebaseAuth.getInstance();
        usernameTextView = findViewById(R.id.usernameTextView);
        db = FirebaseFirestore.getInstance();

        // Configura el Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                }
                else if (itemId == R.id.action_search) {
                    Intent homeIntent = new Intent(Home.this, UserSearchActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        // Obtener el nombre de usuario de Firestore y mostrarlo en el TextView
        obtenerYMostrarNombreDeUsuario();
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
            // Actualiza el estado a "desconectado" antes de cerrar sesión
            actualizarEstado("desconectado", new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mAuth.signOut();  // Cierra sesión solo si la actualización se realizó con éxito
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Home.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Home.this, "Error al actualizar el estado.", Toast.LENGTH_SHORT).show();
                    }
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
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> userUpdate = new HashMap<>();
            userUpdate.put("estado", estado);

            db.collection("Users").document(userId).update(userUpdate)
                    .addOnCompleteListener(onCompleteListener);
        }
    }

    private void obtenerYMostrarNombreDeUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("Users").document(uid);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String usernameFirestore = document.getString("name");
                        if (usernameFirestore != null && !usernameFirestore.isEmpty()) {
                            usernameTextView.setText("Bienvenido, " + usernameFirestore);
                        }
                    }
                }
            });
        }
    }
}