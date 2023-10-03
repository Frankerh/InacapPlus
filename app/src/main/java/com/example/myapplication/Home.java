package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Configura el Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Asegúrate de que el Toolbar esté presente en tu layout

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    item.setChecked(true);

                } else if (itemId == R.id.action_chat) {
                    // Abre la actividad Chat
                    Intent chatIntent = new Intent(Home.this, Chat.class);
                    startActivity(chatIntent);overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else if (itemId == R.id.action_profile) {
                    Intent homeIntent = new Intent(Home.this, Perfil.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                     // Marcar la opción de perfil como seleccionada
                }
                return true;
            }
        });
        // Marcar la opción de perfil como seleccionada al inicio
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Infla el menú de opciones en el ActionBar (Toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu); // Asegúrate de que el nombre del archivo de menú sea "menu.xml" o el que corresponda
        return true;
    }

    // Maneja las acciones del menú de opciones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Cierra la sesión de Firebase Authentication
            mAuth.signOut();

            // Redirige al usuario a la pantalla de inicio de sesión (MainActivity en este caso)
            Intent intent = new Intent(Home.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual (Home)


            // Puedes redirigir al usuario a la pantalla de inicio de sesión o realizar otras acciones aquí.
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
