package com.yanis.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsActivity extends AppCompatActivity {

    private EditText editNewName;
    private Button btnSave;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        editNewName = findViewById(R.id.edit_change_name);
        btnSave = findViewById(R.id.btn_save_settings);

        // 1. Afficher le nom actuel dans le champ de saisie
        if (user != null && user.getDisplayName() != null) {
            editNewName.setText(user.getDisplayName());
        }

        // 2. Action du bouton Enregistrer
        btnSave.setOnClickListener(v -> {
            String newName = editNewName.getText().toString().trim();

            if (newName.isEmpty()) {
                editNewName.setError("Le prénom ne peut pas être vide");
                return;
            }

            if (user != null) {
                // Mise à jour du profil Firebase
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Prénom mis à jour !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 3. BARRE DE NAVIGATION BASSE
        LinearLayout btnHome = findViewById(R.id.btn_nav_home);
        LinearLayout btnRooms = findViewById(R.id.btn_nav_rooms);
        LinearLayout btnSettings = findViewById(R.id.btn_nav_settings);

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        });

        btnRooms.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, PiecesActivity.class));
            finish();
        });

        // Déjà sur Settings, on ne fait rien
    }
}