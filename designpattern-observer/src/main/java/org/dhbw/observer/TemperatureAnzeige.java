package org.dhbw.observer;

public class TemperatureAnzeige implements Observer {
    @Override
    public void update(double temperatur, double luftfeuchtigkeit) {
        System.out.println("TemperaturAnzeige: Temperatur = " + temperatur + " °C");
    }
}

