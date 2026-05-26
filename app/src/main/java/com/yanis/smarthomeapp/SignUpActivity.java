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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, passField, confirmPassField, nameField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        nameField = findViewById(R.id.edit_name_signup);
        emailField = findViewById(R.id.edit_email_signup);
        passField = findViewById(R.id.edit_password_signup);
        confirmPassField = findViewById(R.id.edit_confirm_password);
        Button registerBtn = findViewById(R.id.btn_register);
        TextView goToLogin = findViewById(R.id.txt_go_to_login);

        // bouton S'inscrire
        registerBtn.setOnClickListener(v -> {
            String prenom = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String pass = passField.getText().toString().trim();
            String confirmPass = confirmPassField.getText().toString().trim();

            if (prenom.isEmpty()) {
                Toast.makeText(this, "Entre ton prénom !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Remplis tous les champs !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Les mots de passe sont différents !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(this, "Le mot de passe doit faire 6 caractères min.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création du compte Firebase
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Si c reussi on enregistre le prénom dans le profil
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(prenom)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                                    Toast.makeText(SignUpActivity.this, "Compte créé pour " + prenom, Toast.LENGTH_SHORT).show();
                                    //Direction vers l'accueil
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }
                        } else {
                            // Si ya echec : On affiche l'erreur
                            String error = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "Erreur : " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        //  aller vers la page de Connexion
        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}