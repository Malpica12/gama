/*******************************************************************************************************
 *
 * ColorMode.java, in gama.ext.libs, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package gama.ext.libs.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * ColorMode
 * <p>
 * normal, random 
 * </p>
 * 
 * See Also: 
 * See any element that extends <ColorStyle>.
 */
@XmlType(name = "colorModeEnumType")
@XmlEnum
public enum ColorMode {

    /** The normal. */
    @XmlEnumValue("normal")
    NORMAL("normal"),
    
    /** The random. */
    @XmlEnumValue("random")
    RANDOM("random");
    
    /** The value. */
    private final String value;

    /**
     * Instantiates a new color mode.
     *
     * @param v the v
     */
    ColorMode(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the color mode
     */
    public static ColorMode fromValue(String v) {
        for (ColorMode c: ColorMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}