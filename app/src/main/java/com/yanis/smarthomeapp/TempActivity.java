package com.yanis.smarthomeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TempActivity extends AppCompatActivity {

    private TextView txtTemp, txtWelcome, txtAlerte, txtEvent;
    private DatabaseReference cuisineRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        txtTemp = findViewById(R.id.text_temp_valeur);
        txtWelcome = findViewById(R.id.text_bienvenue_temp);
        txtAlerte = findViewById(R.id.text_temp_alerte);
        txtEvent = findViewById(R.id.text_temp_evenement);
        ImageButton btnBack = findViewById(R.id.btn_back_temp);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (name != null) {
                txtWelcome.setText("Bienvenue " + name + " !");
            }
        }

        cuisineRef = FirebaseDatabase.getInstance().getReference("maison/cuisine");
        cuisineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Double temperature = snapshot.child("temperature").getValue(Double.class);
                Boolean alerte = snapshot.child("alerte").getValue(Boolean.class);
                String evenement = snapshot.child("dernier_evenement").getValue(String.class);

                if (temperature != null) {
                    txtTemp.setText(String.format("%.1f °C", temperature));
                } else {
                    txtTemp.setText("-- °C");
                }

                if (alerte != null && alerte) {
                    txtAlerte.setText("⚠ ALERTE");
                    txtAlerte.setTextColor(getColor(R.color.soft_red));
                    txtAlerte.setVisibility(View.VISIBLE);
                } else {
                    txtAlerte.setText("● Normal");
                    txtAlerte.setTextColor(getColor(R.color.warm_orange));
                    txtAlerte.setVisibility(View.VISIBLE);
                }

                if (evenement != null && !evenement.equals("aucun")) {
                    txtEvent.setText("Dernier : " + evenement);
                    txtEvent.setVisibility(View.VISIBLE);
                } else {
                    txtEvent.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
