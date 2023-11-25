package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CircleImageView profileImageView;
    private TextView usernameTextView;
    private Button followButton;
    private TextView descriptionTextView;
    private Button mensajeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profiel);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        followButton = findViewById(R.id.followButton);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        mensajeButton = findViewById(R.id.mensaje);

        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("uid");
            if (userId != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String username = document.getString("name");
                                    String description = document.getString("descripcion");
                                    String profileImageUrl = document.getString("profileImageUrl");

                                    usernameTextView.setText(username);
                                    descriptionTextView.setText(description);
                                    Picasso.get().load(profileImageUrl).into(profileImageView);
                                    // Load profile image from profileImageUrl (using Glide, Picasso, or Firebase Storage)

                                    followButton.setOnClickListener(view -> {
                                        // Logic to follow/unfollow the user
                                    });

                                    mensajeButton.setOnClickListener(view -> {
                                        // Logic to start a conversation with the user
                                    });
                                }
                            }
                        });
            }

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_home) {
                        // Abre la actividad Home
                        Intent homeIntent = new Intent(UserProfileActivity.this, Home.class);
                        startActivity(homeIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (itemId == R.id.action_chat) {
                        // Abre la actividad Chat
                        Intent chatIntent = new Intent(UserProfileActivity.this, Chat.class);
                        startActivity(chatIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (itemId == R.id.action_profile) {
                        // Esto debe abrir el perfil del usuario seleccionado, no del usuario actual
                        Intent profileIntent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                        // Aquí, en lugar de abrir UserProfileActivity nuevamente, deberías abrir la actividad del perfil del usuario seleccionado
                        profileIntent.putExtra("uid", userId); // Pasa el UID del usuario seleccionado
                        startActivity(profileIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (itemId == R.id.action_search) {
                        // Abre la actividad de búsqueda de usuarios
                        Intent searchIntent = new Intent(UserProfileActivity.this, UserSearchActivity.class);
                        startActivity(searchIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    return true;
                }
            });

            // Marcar la opción de perfil como seleccionada al inicio
        }
    }
}



