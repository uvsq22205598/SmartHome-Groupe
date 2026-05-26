package com.yanis.smarthomeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecurityActivity extends AppCompatActivity {

    private DatabaseReference entreeRef, cmdOuvrir, cmdFermer, cmdDesactiver;

    private TextView txtPorte, txtPresence, txtDistance, txtAlarme, txtTentatives, txtEvenement;
    private CardView cardAlarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        findViewById(R.id.btn_security_retour).setOnClickListener(v -> finish());

        txtPorte = findViewById(R.id.txt_security_porte);
        txtPresence = findViewById(R.id.txt_security_presence);
        txtDistance = findViewById(R.id.txt_security_distance);
        txtAlarme = findViewById(R.id.txt_security_alarme);
        txtTentatives = findViewById(R.id.txt_security_tentatives);
        txtEvenement = findViewById(R.id.txt_security_evenement);
        cardAlarme = findViewById(R.id.card_alarme);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        entreeRef = db.getReference("maison/entree");
        cmdOuvrir = db.getReference("maison/commandes/entree/ouvrir_porte");
        cmdFermer = db.getReference("maison/commandes/entree/fermer_porte");
        cmdDesactiver = db.getReference("maison/commandes/entree/desactiver_alarme");

        entreeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String porte = snapshot.child("porte").getValue(String.class);
                Boolean presence = snapshot.child("presence").getValue(Boolean.class);
                Long distance = snapshot.child("distance_cm").getValue(Long.class);
                Boolean alarme = snapshot.child("alarme_intrusion").getValue(Boolean.class);
                Long tentatives = snapshot.child("tentatives_ratees").getValue(Long.class);
                String evenement = snapshot.child("dernier_evenement").getValue(String.class);

                if (porte != null) {
                    txtPorte.setText(porte);
                    txtPorte.setTextColor(porte.equals("ouverte") ?
                            getColor(R.color.warm_orange) : getColor(R.color.accent_blue));
                }

                if (presence != null && presence) {
                    txtPresence.setText("DÉTECTÉE");
                    txtPresence.setTextColor(getColor(R.color.warm_orange));
                } else {
                    txtPresence.setText("Aucune");
                    txtPresence.setTextColor(getColor(R.color.text_hint));
                }

                if (distance != null) {
                    txtDistance.setText(distance + " cm");
                } else {
                    txtDistance.setText("--");
                }

                boolean alarmeActive = (alarme != null && alarme);
                if (alarmeActive) {
                    txtAlarme.setText("ACTIVE");
                    txtAlarme.setTextColor(Color.parseColor("#F44336"));
                    cardAlarme.setCardBackgroundColor(Color.parseColor("#2D1E1E"));
                } else {
                    txtAlarme.setText("Inactive");
                    txtAlarme.setTextColor(getColor(R.color.text_hint));
                    cardAlarme.setCardBackgroundColor(getColor(R.color.card_grey));
                }

                long t = (tentatives != null) ? tentatives : 0;
                txtTentatives.setText(String.valueOf(t));
                txtTentatives.setTextColor(t >= 3 ?
                        Color.parseColor("#F44336") : getColor(R.color.text_hint));

                if (evenement != null && !evenement.equals("aucun")) {
                    txtEvenement.setText("Dernier événement : " + evenement);
                    txtEvenement.setVisibility(View.VISIBLE);
                } else {
                    txtEvenement.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SecurityActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_ouvrir_porte).setOnClickListener(v -> {
            cmdOuvrir.setValue(true);
            Toast.makeText(this, "Commande : ouvrir porte", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_fermer_porte).setOnClickListener(v -> {
            cmdFermer.setValue(true);
            Toast.makeText(this, "Commande : fermer porte", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_desactiver_alarme).setOnClickListener(v -> {
            cmdDesactiver.setValue(true);
            Toast.makeText(this, "Commande : désactiver alarme", Toast.LENGTH_SHORT).show();
        });
    }
}
