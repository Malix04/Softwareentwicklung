package org.dhbw.observer;

public class Main {
    public static void main(String[] args) {
        WetterStation station = new WetterStation();

        Observer tempDisplay = new TemperatureAnzeige();
        Observer humidityDisplay = new FeuchtigkeitsAnzeige();

        station.registerObserver(tempDisplay);
        station.registerObserver(humidityDisplay);

        station.setMesswerte(21.5, 45.0);
        station.setMesswerte(22.0, 48.5);

        station.removeObserver(humidityDisplay);
        station.setMesswerte(23.2, 50.0);

    }
}
