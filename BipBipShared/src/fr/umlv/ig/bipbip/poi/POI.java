package fr.umlv.ig.bipbip.poi;
import fr.umlv.ig.bipbip.*;
import java.util.*;
/** 
 *   Represent a point of interest.
 * 
 *   @author Damien Girard <dgirard@nativesoft.fr>
 *  */
public interface POI{
	/** 
	 *   Get the X position of the POI.
	 * 
	 *   @return The position.
	 *  */
	double getX();

	/** 
	 *   Get the Y position of the POI.
	 * 
	 *   @return The position.
	 *  */
	double getY();

	/** 
	 *   Get the type of the POI.
	 * 
	 *   @return The type of the POI.
	 *  */
	EventType getType();

	/** 
	 *   Get the date of the POI.
	 * 
	 *   @return The date of the POI.
	 *  */
	Date getDate();

	int getCount();

}

