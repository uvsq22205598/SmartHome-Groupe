package com.yanis.smarthomeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntreeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, cmdOuvrir, cmdFermer, cmdDesactiver;
    private boolean isDoorOpen = false;

    private TextView txtPorte, txtPresence, txtDistance, txtTentatives, txtCodeStatut, txtCodeSaisi;
    private ImageView imgSecurityStatus, btnRetour;
    private Button btnPorte, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entree);

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("maison").child("entree");

        cmdOuvrir = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/entree/ouvrir_porte");
        cmdFermer = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/entree/fermer_porte");
        cmdDesactiver = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/entree/desactiver_alarme");

        txtPorte = findViewById(R.id.txt_entree_porte);
        txtPresence = findViewById(R.id.txt_entree_presence);
        txtDistance = findViewById(R.id.txt_entree_distance);
        txtTentatives = findViewById(R.id.txt_entree_tentatives);
        txtCodeStatut = findViewById(R.id.txt_entree_dernier_acces);
        txtCodeSaisi = findViewById(R.id.txt_entree_code_saisi);
        imgSecurityStatus = findViewById(R.id.img_security_status);
        btnPorte = findViewById(R.id.btn_toggle_porte);
        btnReset = findViewById(R.id.btn_reset_intrusion);
        btnRetour = findViewById(R.id.btn_entree_retour);

        btnRetour.setOnClickListener(v -> finish());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        Object presenceObj = snapshot.child("presence").getValue();
                        Object distanceObj = snapshot.child("distance_cm").getValue();

                        if (presenceObj != null) {
                            String presVal = String.valueOf(presenceObj);
                            if (presVal.equalsIgnoreCase("true")) {
                                txtPresence.setText("Présence : DÉTECTÉE");
                                txtPresence.setTextColor(Color.parseColor("#FFD700"));
                            } else {
                                txtPresence.setText("Présence : AUCUNE");
                                txtPresence.setTextColor(Color.WHITE);
                            }
                        }

                        if (distanceObj != null) {
                            txtDistance.setText(String.valueOf(distanceObj) + " cm");
                        }

                        // CODE SAISI
                        String codeSaisi = snapshot.child("code_saisi").getValue(String.class);
                        Boolean codeValide = snapshot.child("code_valide").getValue(Boolean.class);

                        if (codeSaisi != null && !codeSaisi.isEmpty()) {
                            txtCodeSaisi.setText("Code saisi : " + codeSaisi);
                        } else {
                            txtCodeSaisi.setText("Code saisi : ----");
                        }

                        if (codeValide != null) {
                            if (codeValide) {
                                txtCodeStatut.setText("Accès : AUTORISÉ");
                                txtCodeStatut.setTextColor(Color.GREEN);
                            } else {
                                txtCodeStatut.setText("Accès : REFUSÉ");
                                txtCodeStatut.setTextColor(Color.RED);
                            }
                        } else {
                            txtCodeStatut.setText("Accès : --");
                            txtCodeStatut.setTextColor(Color.WHITE);
                        }

                        // TENTATIVES ET ALARME
                        Long ratees = snapshot.child("tentatives_ratees").getValue(Long.class);
                        txtTentatives.setText("Tentatives ratées : " + (ratees != null ? ratees : 0));

                        Boolean intrusion = snapshot.child("alarme_intrusion").getValue(Boolean.class);
                        if (Boolean.TRUE.equals(intrusion)) {
                            imgSecurityStatus.setColorFilter(Color.RED);
                            btnReset.setVisibility(View.VISIBLE);
                        } else {
                            imgSecurityStatus.setColorFilter(Color.GREEN);
                            btnReset.setVisibility(View.GONE);
                        }

                        // PORTE
                        String porteTxt = snapshot.child("porte").getValue(String.class);
                        if (porteTxt != null) {
                            isDoorOpen = porteTxt.equalsIgnoreCase("ouverte");
                        }

                        if (isDoorOpen) {
                            txtPorte.setText("Porte Ouverte");
                            txtPorte.setTextColor(Color.parseColor("#03DAC5"));
                            btnPorte.setText("FERMER LA PORTE");
                        } else {
                            txtPorte.setText("Porte Fermée");
                            txtPorte.setTextColor(Color.RED);
                            btnPorte.setText("OUVRIR LA PORTE");
                        }
                    }
                } catch (Exception e) {
                    Log.e("EntreeActivity", "Erreur UI : " + e.getMessage());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnPorte.setOnClickListener(v -> {
            if (isDoorOpen) {
                cmdFermer.setValue(true);
                Toast.makeText(this, "Commande : Fermer la porte", Toast.LENGTH_SHORT).show();
            } else {
                cmdOuvrir.setValue(true);
                Toast.makeText(this, "Commande : Ouvrir la porte", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            cmdDesactiver.setValue(true);
            Toast.makeText(this, "Commande : Désactiver alarme", Toast.LENGTH_SHORT).show();
        });
    }
}
