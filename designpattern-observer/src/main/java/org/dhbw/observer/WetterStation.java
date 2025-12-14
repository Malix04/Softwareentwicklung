package org.dhbw.observer;

import java.util.ArrayList;
import java.util.List;

public class WetterStation implements Subject {

    private final List<Observer> observers = new ArrayList<>();
    private double temperatur;
    private double luftfeuchtigkeit;

    @Override
    public void registerObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(temperatur, luftfeuchtigkeit);
        }
    }

    public void setMesswerte(double temperatur, double luftfeuchtigkeit) {
        this.temperatur = temperatur;
        this.luftfeuchtigkeit = luftfeuchtigkeit;
        notifyObservers();
    }
}
