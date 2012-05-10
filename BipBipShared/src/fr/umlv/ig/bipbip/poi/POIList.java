package fr.umlv.ig.bipbip.poi;

import java.util.*;

public class POIList implements Iterable<POI> {

    private ArrayList<POI> list;
    
    public void addPOIListener(POIListener listener) {
        // TODO add implementation
    }
    
    public void removePOIListener(POIListener listener) {
        // TODO add implementation
    }
    
    protected void firePOIListerner(POIEvent e) {
        // TODO add implementation
    }
    
    public void addPOI(POI p) {
        // TODO add implementation
    }
    
    public void removePOI(POI p) {
        // TODO add implementation
    }
    
    public POI getPOIAt(double X, double Y) {
        // TODO add implementation and return statement
    }
    
    @Override
    public Iterator<POI> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
