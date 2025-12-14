package org.dhbw.observer;

public class FeuchtigkeitsAnzeige implements Observer {
    @Override
    public void update(double temperatur, double luftfeuchtigkeit) {
        System.out.println("FeuchtigkeitsAnzeige: Luftfeuchtigkeit = " + luftfeuchtigkeit + " %");
    }
}

