# Firebase Realtime Database — Smart Home

## Connexion
- **URL** : `https://smart-home-7bbd6-default-rtdb.europe-west1.firebasedatabase.app`
- **Projet** : `smart-home-7bbd6`
- **Package app** : `com.yanis.smarthomeapp`

---

## Structure complète

```
maison/
├── salon/                          ← ÉCRIT par le bridge (lecture seule app)
│   ├── lumiere: boolean            ← true = allumée, false = éteinte
│   ├── couleur: string             ← "blanc" | "rouge" | "vert" | "bleu" | "jaune"
│   ├── intensite: number (0-100)   ← luminosité (potentiomètre)
│   ├── tapotement_detecte: boolean
│   ├── vibration_detectee: boolean
│   └── dernier_evenement: string
│
├── cuisine/                        ← ÉCRIT par le bridge (lecture seule app)
│   ├── temperature: number         ← °C
│   ├── humidite: number            ← %
│   ├── alerte: boolean             ← true si seuil dépassé
│   ├── led_alerte: boolean         ← LED allumée
│   ├── buzzer_actif: boolean
│   ├── seuil_temperature: number   ← seuil réglable (défaut: 25.0)
│   ├── seuil_humidite: number      ← seuil réglable (défaut: 70.0)
│   └── dernier_evenement: string
│
├── entree/                         ← ÉCRIT par le bridge (lecture seule app)
│   ├── presence: boolean
│   ├── distance_cm: number
│   ├── porte: string               ← "ouverte" | "fermee"
│   ├── code_valide: boolean
│   ├── tentatives_ratees: number   ← 0-3
│   ├── alarme_intrusion: boolean
│   ├── led_rouge: boolean
│   ├── led_verte: boolean
│   └── dernier_evenement: string
│
├── alarmes/                        ← ÉCRIT par le bridge
│   ├── alarme_generale: boolean
│   ├── source: string              ← "aucune" | "cuisine" | "intrusion"
│   └── buzzer_global: boolean
│
├── systeme/                        ← ÉCRIT par le bridge
│   ├── arduino_connecte: boolean
│   ├── raspberry_connecte: boolean
│   ├── firebase_connecte: boolean
│   └── derniere_mise_a_jour: string (ISO 8601)
│
├── led_salon: string               ← "ON" | "OFF" (LED simple)
├── led_cuisine: string             ← "ON" | "OFF" (LED simple)
├── led_chambre: string             ← "ON" | "OFF" (LED simple)
│
└── commandes/                      ← ÉCRITURE par l'app (resetté par le bridge)
    ├── salon/
    │   ├── forcer_couleur: string   ← "blanc"/"rouge"/"vert"/"bleu"/"jaune" ou ""
    │   ├── forcer_intensite: number ← -1 = pas de changement
    │   ├── eteindre_lumiere: boolean
    │   └── allumer_lumiere: boolean
    ├── cuisine/
    │   ├── reset_alerte: boolean
    │   ├── seuil_temperature: number
    │   └── seuil_humidite: number
    └── entree/
        ├── ouvrir_porte: boolean
        ├── fermer_porte: boolean
        └── desactiver_alarme: boolean
```

---

## Comment les commandes fonctionnent (important !)

Le système `commandes/` est un **système à impulsion** :

1. L'**app** écrit `maison/commandes/salon/allumer_lumiere = true`
2. Le **bridge Raspberry Pi** lit ça (toutes les 1 seconde)
3. Le bridge envoie `LIGHT_ON` à l'Arduino via port série
4. Le bridge **remet la commande à `false`**
5. L'Arduino allume la LED et envoie son état
6. Le **bridge écrit** `maison/salon/lumiere = true`
7. L'**app lit** `maison/salon/lumiere` et affiche "ON"

**Donc :**
- Pour **lire** l'état → chemins sous `salon/`, `cuisine/`, `entree/`, `alarmes/`
- Pour **envoyer un ordre** → chemins sous `commandes/`

---

## Que lire et écrire dans chaque écran

### Page Température (TempActivity)
| Action | Chemin | Info |
|---|---|---|
| Lire température | `maison/cuisine/temperature` | `Double` |
| Lire alerte | `maison/cuisine/alerte` | `Boolean` |
| Lire événement | `maison/cuisine/dernier_evenement` | `String` |

### Page Humidité (HumidActivity)
| Action | Chemin | Info |
|---|---|---|
| Lire humidité | `maison/cuisine/humidite` | `Double` |
| Lire alerte | `maison/cuisine/alerte` | `Boolean` |
| Lire événement | `maison/cuisine/dernier_evenement` | `String` |

### Page Éclairage Salon (SalonActivity)
| Action | Chemin | Info |
|---|---|---|
| Lire état lumière | `maison/salon/lumiere` | `Boolean` |
| Lire couleur | `maison/salon/couleur` | `String` |
| Lire intensité | `maison/salon/intensite` | `Long` |
| **Allumer** | `maison/commandes/salon/allumer_lumiere = true` | Écrire |
| **Éteindre** | `maison/commandes/salon/eteindre_lumiere = true` | Écrire |
| **Changer couleur** | `maison/commandes/salon/forcer_couleur = "rouge"` | Écrire |

### Page Sécurité (SecurityActivity)
| Action | Chemin | Info |
|---|---|---|
| Lire porte | `maison/entree/porte` | `String` ("ouverte"/"fermee") |
| Lire présence | `maison/entree/presence` | `Boolean` |
| Lire distance | `maison/entree/distance_cm` | `Long` |
| Lire alarme intrusion | `maison/entree/alarme_intrusion` | `Boolean` |
| Lire tentatives | `maison/entree/tentatives_ratees` | `Long` |
| Lire événement | `maison/entree/dernier_evenement` | `String` |
| **Ouvrir porte** | `maison/commandes/entree/ouvrir_porte = true` | Écrire |
| **Fermer porte** | `maison/commandes/entree/fermer_porte = true` | Écrire |
| **Désactiver alarme** | `maison/commandes/entree/desactiver_alarme = true` | Écrire |

### Page Entrée / Code (EntreeActivity)
| Action | Chemin | Info |
|---|---|---|
| Lire porte | `maison/entree/porte` | `String` |
| Lire présence | `maison/entree/presence` | `Boolean` |
| Lire distance | `maison/entree/distance_cm` | `Long` |
| Lire code saisi | `maison/entree/code_saisi` | `String` (optionnel) |
| Lire code valide | `maison/entree/code_valide` | `Boolean` |
| Lire tentatives | `maison/entree/tentatives_ratees` | `Long` |
| Lire alarme intrusion | `maison/entree/alarme_intrusion` | `Boolean` |
| **Ouvrir/fermer porte** | `maison/commandes/entree/ouvrir_porte` ou `fermer_porte` | Écrire |
| **Désactiver alarme** | `maison/commandes/entree/desactiver_alarme = true` | Écrire |

### Page LEDs simples (LedActivity)
| Action | Chemin | Info |
|---|---|---|
| LED salon | `maison/led_salon` | `String` "ON"/"OFF" |
| LED cuisine | `maison/led_cuisine` | `String` "ON"/"OFF" |
| LED chambre | `maison/led_chambre` | `String` "ON"/"OFF" |

---

## Règles de sécurité (à copier dans la console Firebase)

```json
{
  "rules": {
    "maison": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

Pour les tests sans auth :
```json
{
  "rules": {
    "maison": {
      ".read": true,
      ".write": true
    }
  }
}
```

---

## Codes couleur disponibles

| Valeur | Couleur |
|---|---|
| `"blanc"` | Blanc |
| `"rouge"` | Rouge |
| `"vert"` | Vert |
| `"bleu"` | Bleu |
| `"jaune"` | Jaune |

---

## Codes Dernier Événement possibles

### Salon
- `"aucun"`, `"tapotement_detecte"`, `"lumiere_on"`, `"lumiere_off"`, `"couleur_forcee"`

### Cuisine
- `"normal"`, `"alerte_cuisine"`, `"erreur_dht11"`, `"alerte_reinitialisee"`, `"seuil_temperature_modifie"`, `"seuil_humidite_modifie"`

### Entrée
- `"presence_detectee"`, `"presence_absente"`, `"code_efface"`, `"acces_autorise"`, `"acces_refuse"`, `"alarme_intrusion"`, `"porte_ouverte"`, `"porte_fermee"`, `"alarme_desactivee"`

---

## Carte des activités Android

| Activité | Fichier |
|---|---|
| Inscription | `SignUpActivity.java` |
| Connexion | `LoginActivity.java` |
| Tableau de bord | `MainActivity.java` |
| Température | `TempActivity.java` |
| Humidité | `HumidActivity.java` |
| Éclairage salon | `SalonActivity.java` |
| LEDs simples | `LedActivity.java` |
| Sécurité | `SecurityActivity.java` |
| Entrée / Code | `EntreeActivity.java` |
| Cuisine | `CuisineActivity.java` |
| Pièces | `PiecesActivity.java` |
| Paramètres | `SettingsActivity.java` |
