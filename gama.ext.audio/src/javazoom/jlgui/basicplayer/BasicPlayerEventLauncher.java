/*******************************************************************************************************
 *
 * BasicPlayerEventLauncher.java, in gama.ext.audio, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package javazoom.jlgui.basicplayer;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class implements a threaded events launcher.
 */
public class BasicPlayerEventLauncher extends Thread
{
    
    /** The code. */
    private int code = -1;
    
    /** The position. */
    private int position = -1;
    
    /** The value. */
    private double value = 0.0;
    
    /** The description. */
    private Object description = null;
    
    /** The listeners. */
    private Collection listeners = null;
    
    /** The source. */
    private Object source = null;

    /**
     * Contructor.
     *
     * @param code the code
     * @param position the position
     * @param value the value
     * @param description the description
     * @param listeners the listeners
     * @param source the source
     */
    public BasicPlayerEventLauncher(int code, int position, double value, Object description, Collection listeners, Object source)
    {
        super();
        this.code = code;
        this.position = position;
        this.value = value;
        this.description = description;
        this.listeners = listeners;
        this.source = source;
    }

    public void run()
    {
        if (listeners != null)
        {
            Iterator it = listeners.iterator();
            while (it.hasNext())
            {
                BasicPlayerListener bpl = (BasicPlayerListener) it.next();
                BasicPlayerEvent event = new BasicPlayerEvent(source, code, position, value, description);
                bpl.stateUpdated(event);
            }
        }
    }
}