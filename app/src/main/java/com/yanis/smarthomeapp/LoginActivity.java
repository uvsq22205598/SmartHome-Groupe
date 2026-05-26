package com.yanis.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passField;
    private FirebaseAuth mAuth;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.edit_email_login);
        passField = findViewById(R.id.edit_password_login);
        loginBtn = findViewById(R.id.btn_login);
        TextView goToSignup = findViewById(R.id.txt_go_to_signup);

        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String pass = passField.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Remplis tout !", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🛑 Bloque le bouton pour éviter les clics multiples qui font bugger Firebase
            loginBtn.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Détruit le Login pour ne pas pouvoir y revenir avec le bouton retour du téléphone
                        } else {
                            loginBtn.setEnabled(true); // Réactive le bouton en cas d'échec
                            Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Si l'utilisateur est déjà connecté, on va directement à l'accueil
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}