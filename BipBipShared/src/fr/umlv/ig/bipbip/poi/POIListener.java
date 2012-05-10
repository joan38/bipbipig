package fr.umlv.ig.bipbip.poi;
import java.util.*;
public interface POIListener extends EventListener{
	void poiAdded(POIEvent e);

	void poiRemoved(POIEvent e);

}

