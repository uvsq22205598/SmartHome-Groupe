package com.yanis.smarthomeapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeTitle, avgTemp, avgHumid, lightsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        welcomeTitle = findViewById(R.id.text_main_welcome);
        avgTemp = findViewById(R.id.main_avg_temp);
        avgHumid = findViewById(R.id.main_avg_humid);
        lightsCount = findViewById(R.id.main_lights_on);


        if (user != null) {
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                welcomeTitle.setText("Bienvenue chez vous, " + name + " !");
            } else {
                welcomeTitle.setText("Bienvenue chez vous !");
            }
        }
     // --- 4. NAVIGATION DU CENTRE (CARTES DE CONTRÔLE) ---
        CardView cardTemp = findViewById(R.id.card_temp);
        CardView cardHumid = findViewById(R.id.card_humid);
        CardView cardLed = findViewById(R.id.card_led);
        CardView cardSecurity = findViewById(R.id.card_security);

        cardTemp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TempActivity.class)));
        cardHumid.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HumidActivity.class)));
        cardLed.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LedActivity.class)));
        cardSecurity.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SecurityActivity.class)));

        LinearLayout btnHome = findViewById(R.id.btn_nav_home);
        LinearLayout btnRooms = findViewById(R.id.btn_nav_rooms);
        LinearLayout btnSettings = findViewById(R.id.btn_nav_settings);

        btnHome.setOnClickListener(v -> {
        });

        btnRooms.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PiecesActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        updateGlobalDashboard("18°C", "65%", "3");
    }
    private void updateGlobalDashboard(String temp, String humid, String lights) {
        avgTemp.setText(temp);
        avgHumid.setText(humid);
        lightsCount.setText(lights);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            welcomeTitle.setText("Bienvenue chez vous, " + user.getDisplayName() + " !");
        }
    }
}