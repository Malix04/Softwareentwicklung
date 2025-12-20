# Observer Pattern – Wetterstation

## Prinzip
Das Observer Pattern beschreibt eine 1:n-Beziehung zwischen einem Subjekt (Subject) und mehreren Beobachtern (Observer).
Ändert sich der Zustand des Subjekts (z. B. neue Messwerte), werden alle registrierten Observer automatisch benachrichtigt
und über die Methode `update(temperatur, luftfeuchtigkeit)` aktualisiert. Dadurch bleiben Subject und Observer lose gekoppelt,
da sie nur über Interfaces miteinander interagieren.

## Vorteile
- Lose Kopplung: Subject kennt nur das Observer-Interface
- Erweiterbar: Neue Displays können ohne Änderung der WetterStation hinzugefügt werden
- Gute Eignung für ereignisgetriebene Systeme (Push-Prinzip)

## Nachteile
- Viele Observer können Performance beeinflussen (viele Benachrichtigungen)
- Debugging schwieriger durch indirekte Aufrufketten
- Reihenfolge der Benachrichtigung kann relevant werden, wenn Observer voneinander abhängen
