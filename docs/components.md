# Komponentenbeschreibung

## Hedgehog / Roboter
Wir verwenden zwei Hedgehogs: *raspberrypi* und *hedgehog13*. Verbindung: Hedgehogs verbinden sich automatisch mit einem Netzwerk mit SSID *hedgehog* und Passwort *hedgehog*. Anschließend kann man über den hostnamen [raspberrypi.local]() (alternativ über IP-Adresse) oder [hedgehog13.local]() auf das Dashboard zugreifen. *raspberrypi* führt das Kontrollprogramm namens `Arm/main.py` aus.

### Kontrollprogramm

Das Kontrollprogramm kommuniziert via MQTT und kann einfache `goto`-Befehle, sowie eine Pfeiltastensteuerung entgegennehmen und beherrscht einen einfachen Mechanismus, der die Bewegung der Aktuatoren glättet, damit sie nicht so ruckartig ist.

### Aktuatoren

Die Aktuatoren sind Servos mit einem Bewegungsspielraum von ca. 180°. Die Belegung der Servos ist folgendermaßen:

**raspberrypi:**
* Servo0: Basis Rotation
* Servo1: Hauptarm Heben-Senken
* Servo2: Untertarm Heben-Senken
* Servo3: Armgelenk Heben-Senken

**hedgehog13:**
* Servo0: Armgelenk Rotatino
* Servo1: Greifer

Weil die beiden Hedgehogs drahtlos kommunizieren und der Controller auf *raspberrypi* läuft, kommt es zu einer etwas größeren Latenz bei Befehlen zu *hedgehog13*.

### Umrechnung von RAD zu DEG zu Servo
Im Vergleich zur Simulation, wo der Bewegungsspielraum 360° (`rad: [-Pi, Pi]`) ist, ist der Bewegungsradius der Servos nur 180° (`rad: [0, Pi]`). Hedgehogs bieten eine API zur Steuerung der Servos, bei dem die Position mit einem linearen Wert zwischen [0, 2000] angegeben werden, wobei ungefähr gilt: `0 = 0°deg, 2000 = 180°deg`. Dementsprechend findet sich im Programmfile `hedgehog.py` eine entsprechende Umrechnung von [0, Pi] zu [0, 2000] und umgekehrt, weil für die MQTT-API Werte in Radiant angegeben werden.

### Kalibrierung der Servo-Positionen

Um die Servo-Positionen mit der Simulation abzustimmen, ist es notwendig, gewisse . In der Grundposition sollen so alle `goto`-Befehle der MQTT-API auf `0.0` sein.

### Sensoren

Im Vergleich zur Simulation bietet die derzeitige Konfiguration mit den Servos keine Möglichkeit, die Servoposition zu messen. Um dennoch Sensorwerte zu bekommen, nehmen wir die letzte Position, des letzten Servo-Befehles als tatsächliche Position des Servos an.

### Start und Stopp von Hedgehog Programmen
Hedgehog Programme können entweder über die Web-IDE unter [raspberrypi.local]() gestartet werden, oder über die REST-API.

Ein Beispiel für die Verwendung der API findet sich in der Klasse `HedgehogController` im MQTTServer Projekt.

## MQTTServer

Der Server ist die Hauptkomponente des Systems. Das zugehörige Kotlin-Projekt ist `MQTTServer`. Gradle wird für die Automatisierung des Builds verwendet.

### API

Die REST API des Servers umfasst alle wesentlichen Mechanismen und inkludiert das Auswählen, Starten und Stoppen von Jobs, sowie die Abfrage der Systemzustände von Roboter, Conveyor und TestingRig.
ist in der Klasse `WebController`, in der Methode `setupRoutes()` definiert und dokumentiert.

### Zustandsmaschine

Unsere Zustandsmaschine kennt zwei Typen von Zuständen: `BasicState` und `ChoiceState`.

Die Klasse `StateObserver` überprüft in sehr kurzen Zeitabständen, ob der aktuell beobachtete Systemzustand mit einem definierten Zustand der Job-Beschreibung übereinstimmt. Es werden dabei nicht nur der oder die Folgezustände überprüft, sondern alle Zustände. Sollte es also zu einer Ausnahme kommen, wird das erkannt. Generell unterscheidet unsere Zustandsmaschine zwischen zwei Zustandstypen: `BasicState` und `ChoiceState`. Ein `ChoiceState` definiert zwei alternative Sequenzen aus `BasicState`. Je nach Erfüllung der Bedingung wird die erste, oder die zweite ausgewählt. Diese Bedingung ist folgendermaßen festgelegt. Vor jedem `ChoiceState` muss ein `BasicState` sein und dieser `BasicState` muss zwei Environments definiert haben: `state.environment` und `state.altEnvironment`, die sich, bis auf einen Sensorwert nicht unterscheiden. Dieser Sensorwert darf außerdem nicht durch Aktuatorenbefehle beeinflussbar sein. In unserem System sind das zwei Möglichkeiten: Wo ist das Objekt auf dem Conveyor? Dafür gibt es drei Möglichkeiten, die Außeneinflüssen überlassen sind und: Wie lautet die Produktklasse auf dem QR-Code? Auch das kann vom System nicht gesteuert werden. Stimmen dann der aktuell gemessene Systemzustand mit `state.environment` überein, wird die erste Sequenz im `ChoiceState` ausgewählt, stimmt er mit `state.altEnvironment` überein, die zweite Sequenz.
 
## Web Dashboard

Das Web Dashboard erlaubt ein einfaches Monitoring des Systemzustandes und das Ausführen sogenannter Jobs. Ein Job ist eine definierte Abfolge, die mit Zustandswechsel beschrieben wird. Das Web-Projekt wird von *MQTTServer* statisch gehosted, wenn dieser startet und ist dann unter [http://localhost:8080]() erreichbar.

### Technologie

Das Projekt ist mit dem Vue.js Framework und Vue-Components implementiert, die sich gut dafür eignen, von einem Code-Generator generiert zu werden und die es erlauben, Artefakte für den digitalen Zwilling (zB. SVG-Grafiken, Collada-Modelle) modular einzubinden. Die Library-Abhängigkeiten als npm-Module werden von Webpack verwaltet.

Links:

* Setup: [https://vuejs.org/v2/guide/installation.html]()
* Webpack + Vue: [https://github.com/vuejs-templates/webpack]()

### Build & Run

Das Projekt kann auf zwei Varianten gestartet werden. Entweder mit
* `npm run dev`, wenn man das Webprojekt entwickelt, um live Veränderungen im Code im Browser zu sehen oder mit
* `npm run build`, um eine Deployment-Version zu erstellen. Diese wird dann auch von MQTTServer gehostet. Alle Artefakte dieses Schrittes liegenn dann im `dist` Verzeichnis.

Diese zweite Variante ist auch beim Build von *MQTTServer* in Gradle automatisiert. Das bedeutet, wenn man `gradle run` ausführt, wird automatisch das Webprojekt gebaut. Im File `build.gradle` finden sich die entsprechenden Tasks `installWeb` und `buildWeb`, die die beiden notwendigen Schritte ausführen.

### WebGL und Collada 3D

Die Vue-Component *WebGL* lädt ein Collada Modell des Kuka-Roboters und erlaubt es, mit der Maus die einzelnen Komponenten zu selektieren. Die Szene kann dafür verwendet werden, beliebige 3D-Modelle anzuzeigen. Das *select*-Event wird für jede Änderung der Selektion mit der Maus gefeuert. Derzeit werden für gewisse definierte Komponenten (Base, First-Arm, Second-Arm, ...) die aktuellen Sensordaten angezeigt, wenn sie selektiert sind.

