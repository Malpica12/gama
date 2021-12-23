/*******************************************************************************************************
 *
 * Premise.java, in gama.ext.libs, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package gama.ext.libs.kml.xal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * The Class Premise.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "premiseName",
    "premiseLocation",
    "premiseNumber",
    "premiseNumberRange",
    "premiseNumberPrefix",
    "premiseNumberSuffix",
    "buildingName",
    "subPremise",
    "firm",
    "mailStop",
    "postalCode",
    "premise",
    "any"
})
@XmlRootElement(name = "Premise")
public class Premise implements Cloneable
{

    /** The address line. */
    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    
    /** The premise name. */
    @XmlElement(name = "PremiseName")
    protected List<Premise.PremiseName> premiseName;
    
    /** The premise location. */
    @XmlElement(name = "PremiseLocation")
    protected Premise.PremiseLocation premiseLocation;
    
    /** The premise number. */
    @XmlElement(name = "PremiseNumber")
    protected List<PremiseNumber> premiseNumber;
    
    /** The premise number range. */
    @XmlElement(name = "PremiseNumberRange")
    protected Premise.PremiseNumberRange premiseNumberRange;
    
    /** The premise number prefix. */
    @XmlElement(name = "PremiseNumberPrefix")
    protected List<PremiseNumberPrefix> premiseNumberPrefix;
    
    /** The premise number suffix. */
    @XmlElement(name = "PremiseNumberSuffix")
    protected List<PremiseNumberSuffix> premiseNumberSuffix;
    
    /** The building name. */
    @XmlElement(name = "BuildingName")
    protected List<BuildingName> buildingName;
    
    /** The sub premise. */
    @XmlElement(name = "SubPremise")
    protected List<SubPremise> subPremise;
    
    /** The firm. */
    @XmlElement(name = "Firm")
    protected Firm firm;
    
    /** The mail stop. */
    @XmlElement(name = "MailStop")
    protected MailStop mailStop;
    
    /** The postal code. */
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    
    /** The premise. */
    @XmlElement(name = "Premise")
    protected Premise premise;
    
    /** The any. */
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    
    /** The underscore. */
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    
    /** The xal premise dependency. */
    @XmlAttribute(name = "PremiseDependency")
    @XmlSchemaType(name = "anySimpleType")
    protected String xalPremiseDependency;
    
    /** The premise dependency. */
    @XmlAttribute(name = "PremiseDependencyType")
    @XmlSchemaType(name = "anySimpleType")
    protected String premiseDependency;
    
    /** The premise thoroughfare connector. */
    @XmlAttribute(name = "PremiseThoroughfareConnector")
    @XmlSchemaType(name = "anySimpleType")
    protected String premiseThoroughfareConnector;
    
    /** The other attributes. */
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields.
     *
     * @param premiseLocation     required parameter
     * @param premiseNumber     required parameter
     * @param premiseNumberRange     required parameter
     */
    public Premise(final Premise.PremiseLocation premiseLocation, final List<PremiseNumber> premiseNumber, final Premise.PremiseNumberRange premiseNumberRange) {
        super();
        this.premiseLocation = premiseLocation;
        this.premiseNumber = premiseNumber;
        this.premiseNumberRange = premiseNumberRange;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private Premise() {
        super();
    }

    /**
     * Gets the address line.
     *
     * @return the address line
     */
    public List<AddressLine> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<AddressLine>();
        }
        return this.addressLine;
    }

    /**
     * Gets the premise name.
     *
     * @return the premise name
     */
    public List<Premise.PremiseName> getPremiseName() {
        if (premiseName == null) {
            premiseName = new ArrayList<Premise.PremiseName>();
        }
        return this.premiseName;
    }

    /**
     * Gets the premise location.
     *
     * @return     possible object is
     *     {@link Premise.PremiseLocation}
     */
    public Premise.PremiseLocation getPremiseLocation() {
        return premiseLocation;
    }

    /**
     * Sets the premise location.
     *
     * @param value     allowed object is
     *     {@link Premise.PremiseLocation}
     */
    public void setPremiseLocation(Premise.PremiseLocation value) {
        this.premiseLocation = value;
    }

    /**
     * Gets the premise number.
     *
     * @return the premise number
     */
    public List<PremiseNumber> getPremiseNumber() {
        if (premiseNumber == null) {
            premiseNumber = new ArrayList<PremiseNumber>();
        }
        return this.premiseNumber;
    }

    /**
     * Gets the premise number range.
     *
     * @return     possible object is
     *     {@link Premise.PremiseNumberRange}
     */
    public Premise.PremiseNumberRange getPremiseNumberRange() {
        return premiseNumberRange;
    }

    /**
     * Sets the premise number range.
     *
     * @param value     allowed object is
     *     {@link Premise.PremiseNumberRange}
     */
    public void setPremiseNumberRange(Premise.PremiseNumberRange value) {
        this.premiseNumberRange = value;
    }

    /**
     * Gets the premise number prefix.
     *
     * @return the premise number prefix
     */
    public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
        if (premiseNumberPrefix == null) {
            premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
        }
        return this.premiseNumberPrefix;
    }

    /**
     * Gets the premise number suffix.
     *
     * @return the premise number suffix
     */
    public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
        if (premiseNumberSuffix == null) {
            premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
        }
        return this.premiseNumberSuffix;
    }

    /**
     * Gets the building name.
     *
     * @return the building name
     */
    public List<BuildingName> getBuildingName() {
        if (buildingName == null) {
            buildingName = new ArrayList<BuildingName>();
        }
        return this.buildingName;
    }

    /**
     * Gets the sub premise.
     *
     * @return the sub premise
     */
    public List<SubPremise> getSubPremise() {
        if (subPremise == null) {
            subPremise = new ArrayList<SubPremise>();
        }
        return this.subPremise;
    }

    /**
     * Gets the firm.
     *
     * @return     possible object is
     *     {@link Firm}
     */
    public Firm getFirm() {
        return firm;
    }

    /**
     * Sets the firm.
     *
     * @param value     allowed object is
     *     {@link Firm}
     */
    public void setFirm(Firm value) {
        this.firm = value;
    }

    /**
     * Gets the mail stop.
     *
     * @return     possible object is
     *     {@link MailStop}
     */
    public MailStop getMailStop() {
        return mailStop;
    }

    /**
     * Sets the mail stop.
     *
     * @param value     allowed object is
     *     {@link MailStop}
     */
    public void setMailStop(MailStop value) {
        this.mailStop = value;
    }

    /**
     * Gets the postal code.
     *
     * @return     possible object is
     *     {@link PostalCode}
     */
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code.
     *
     * @param value     allowed object is
     *     {@link PostalCode}
     */
    public void setPostalCode(PostalCode value) {
        this.postalCode = value;
    }

    /**
     * Gets the premise.
     *
     * @return     possible object is
     *     {@link Premise}
     */
    public Premise getPremise() {
        return premise;
    }

    /**
     * Sets the premise.
     *
     * @param value     allowed object is
     *     {@link Premise}
     */
    public void setPremise(Premise value) {
        this.premise = value;
    }

    /**
     * Gets the any.
     *
     * @return the any
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the underscore.
     *
     * @return     possible object is
     *     {@link String}
     */
    public String getUnderscore() {
        return underscore;
    }

    /**
     * Sets the underscore.
     *
     * @param value     allowed object is
     *     {@link String}
     */
    public void setUnderscore(String value) {
        this.underscore = value;
    }

    /**
     * Gets the xal premise dependency.
     *
     * @return     possible object is
     *     {@link String}
     */
    public String getXalPremiseDependency() {
        return xalPremiseDependency;
    }

    /**
     * Sets the xal premise dependency.
     *
     * @param value     allowed object is
     *     {@link String}
     */
    public void setXalPremiseDependency(String value) {
        this.xalPremiseDependency = value;
    }

    /**
     * Gets the premise dependency.
     *
     * @return     possible object is
     *     {@link String}
     */
    public String getPremiseDependency() {
        return premiseDependency;
    }

    /**
     * Sets the premise dependency.
     *
     * @param value     allowed object is
     *     {@link String}
     */
    public void setPremiseDependency(String value) {
        this.premiseDependency = value;
    }

    /**
     * Gets the premise thoroughfare connector.
     *
     * @return     possible object is
     *     {@link String}
     */
    public String getPremiseThoroughfareConnector() {
        return premiseThoroughfareConnector;
    }

    /**
     * Sets the premise thoroughfare connector.
     *
     * @param value     allowed object is
     *     {@link String}
     */
    public void setPremiseThoroughfareConnector(String value) {
        this.premiseThoroughfareConnector = value;
    }

    /**
     * Gets the other attributes.
     *
     * @return     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
        result = ((prime*result)+((premiseName == null)? 0 :premiseName.hashCode()));
        result = ((prime*result)+((premiseLocation == null)? 0 :premiseLocation.hashCode()));
        result = ((prime*result)+((premiseNumber == null)? 0 :premiseNumber.hashCode()));
        result = ((prime*result)+((premiseNumberRange == null)? 0 :premiseNumberRange.hashCode()));
        result = ((prime*result)+((premiseNumberPrefix == null)? 0 :premiseNumberPrefix.hashCode()));
        result = ((prime*result)+((premiseNumberSuffix == null)? 0 :premiseNumberSuffix.hashCode()));
        result = ((prime*result)+((buildingName == null)? 0 :buildingName.hashCode()));
        result = ((prime*result)+((subPremise == null)? 0 :subPremise.hashCode()));
        result = ((prime*result)+((firm == null)? 0 :firm.hashCode()));
        result = ((prime*result)+((mailStop == null)? 0 :mailStop.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((premise == null)? 0 :premise.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((xalPremiseDependency == null)? 0 :xalPremiseDependency.hashCode()));
        result = ((prime*result)+((premiseDependency == null)? 0 :premiseDependency.hashCode()));
        result = ((prime*result)+((premiseThoroughfareConnector == null)? 0 :premiseThoroughfareConnector.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ((obj instanceof Premise) == false) {
            return false;
        }
        Premise other = ((Premise) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (premiseName == null) {
            if (other.premiseName!= null) {
                return false;
            }
        } else {
            if (premiseName.equals(other.premiseName) == false) {
                return false;
            }
        }
        if (premiseLocation == null) {
            if (other.premiseLocation!= null) {
                return false;
            }
        } else {
            if (premiseLocation.equals(other.premiseLocation) == false) {
                return false;
            }
        }
        if (premiseNumber == null) {
            if (other.premiseNumber!= null) {
                return false;
            }
        } else {
            if (premiseNumber.equals(other.premiseNumber) == false) {
                return false;
            }
        }
        if (premiseNumberRange == null) {
            if (other.premiseNumberRange!= null) {
                return false;
            }
        } else {
            if (premiseNumberRange.equals(other.premiseNumberRange) == false) {
                return false;
            }
        }
        if (premiseNumberPrefix == null) {
            if (other.premiseNumberPrefix!= null) {
                return false;
            }
        } else {
            if (premiseNumberPrefix.equals(other.premiseNumberPrefix) == false) {
                return false;
            }
        }
        if (premiseNumberSuffix == null) {
            if (other.premiseNumberSuffix!= null) {
                return false;
            }
        } else {
            if (premiseNumberSuffix.equals(other.premiseNumberSuffix) == false) {
                return false;
            }
        }
        if (buildingName == null) {
            if (other.buildingName!= null) {
                return false;
            }
        } else {
            if (buildingName.equals(other.buildingName) == false) {
                return false;
            }
        }
        if (subPremise == null) {
            if (other.subPremise!= null) {
                return false;
            }
        } else {
            if (subPremise.equals(other.subPremise) == false) {
                return false;
            }
        }
        if (firm == null) {
            if (other.firm!= null) {
                return false;
            }
        } else {
            if (firm.equals(other.firm) == false) {
                return false;
            }
        }
        if (mailStop == null) {
            if (other.mailStop!= null) {
                return false;
            }
        } else {
            if (mailStop.equals(other.mailStop) == false) {
                return false;
            }
        }
        if (postalCode == null) {
            if (other.postalCode!= null) {
                return false;
            }
        } else {
            if (postalCode.equals(other.postalCode) == false) {
                return false;
            }
        }
        if (premise == null) {
            if (other.premise!= null) {
                return false;
            }
        } else {
            if (premise.equals(other.premise) == false) {
                return false;
            }
        }
        if (any == null) {
            if (other.any!= null) {
                return false;
            }
        } else {
            if (any.equals(other.any) == false) {
                return false;
            }
        }
        if (underscore == null) {
            if (other.underscore!= null) {
                return false;
            }
        } else {
            if (underscore.equals(other.underscore) == false) {
                return false;
            }
        }
        if (xalPremiseDependency == null) {
            if (other.xalPremiseDependency!= null) {
                return false;
            }
        } else {
            if (xalPremiseDependency.equals(other.xalPremiseDependency) == false) {
                return false;
            }
        }
        if (premiseDependency == null) {
            if (other.premiseDependency!= null) {
                return false;
            }
        } else {
            if (premiseDependency.equals(other.premiseDependency) == false) {
                return false;
            }
        }
        if (premiseThoroughfareConnector == null) {
            if (other.premiseThoroughfareConnector!= null) {
                return false;
            }
        } else {
            if (premiseThoroughfareConnector.equals(other.premiseThoroughfareConnector) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link AddressLine} and adds it to addressLine.
     * This method is a short version for:
     * <code>
     * AddressLine addressLine = new AddressLine();
     * this.getAddressLine().add(addressLine); </code>
     *
     * @return the address line
     */
    public AddressLine createAndAddAddressLine() {
        AddressLine newValue = new AddressLine();
        this.getAddressLine().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Premise.PremiseName} and adds it to premiseName.
     * This method is a short version for:
     * <code>
     * PremiseName premiseName = new PremiseName();
     * this.getPremiseName().add(premiseName); </code>
     *
     * @return the premise. premise name
     */
    public Premise.PremiseName createAndAddPremiseName() {
        Premise.PremiseName newValue = new Premise.PremiseName();
        this.getPremiseName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Premise.PremiseLocation} and set it to premiseLocation.
     * 
     * This method is a short version for:
     * <code>
     * PremiseLocation premiseLocation = new PremiseLocation();
     * this.setPremiseLocation(premiseLocation); </code>
     *
     * @return the premise. premise location
     */
    public Premise.PremiseLocation createAndSetPremiseLocation() {
        Premise.PremiseLocation newValue = new Premise.PremiseLocation();
        this.setPremiseLocation(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PremiseNumber} and adds it to premiseNumber.
     * This method is a short version for:
     * <code>
     * PremiseNumber premiseNumber = new PremiseNumber();
     * this.getPremiseNumber().add(premiseNumber); </code>
     *
     * @return the premise number
     */
    public PremiseNumber createAndAddPremiseNumber() {
        PremiseNumber newValue = new PremiseNumber();
        this.getPremiseNumber().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Premise.PremiseNumberRange} and set it to premiseNumberRange.
     * 
     * This method is a short version for:
     * <code>
     * PremiseNumberRange premiseNumberRange = new PremiseNumberRange();
     * this.setPremiseNumberRange(premiseNumberRange); </code>
     *
     * @param premiseNumberRangeFrom     required parameter
     * @param premiseNumberRangeTo     required parameter
     * @return the premise. premise number range
     */
    public Premise.PremiseNumberRange createAndSetPremiseNumberRange(final Premise.PremiseNumberRange.PremiseNumberRangeFrom premiseNumberRangeFrom, final Premise.PremiseNumberRange.PremiseNumberRangeTo premiseNumberRangeTo) {
        Premise.PremiseNumberRange newValue = new Premise.PremiseNumberRange(premiseNumberRangeFrom, premiseNumberRangeTo);
        this.setPremiseNumberRange(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PremiseNumberPrefix} and adds it to premiseNumberPrefix.
     * This method is a short version for:
     * <code>
     * PremiseNumberPrefix premiseNumberPrefix = new PremiseNumberPrefix();
     * this.getPremiseNumberPrefix().add(premiseNumberPrefix); </code>
     *
     * @return the premise number prefix
     */
    public PremiseNumberPrefix createAndAddPremiseNumberPrefix() {
        PremiseNumberPrefix newValue = new PremiseNumberPrefix();
        this.getPremiseNumberPrefix().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PremiseNumberSuffix} and adds it to premiseNumberSuffix.
     * This method is a short version for:
     * <code>
     * PremiseNumberSuffix premiseNumberSuffix = new PremiseNumberSuffix();
     * this.getPremiseNumberSuffix().add(premiseNumberSuffix); </code>
     *
     * @return the premise number suffix
     */
    public PremiseNumberSuffix createAndAddPremiseNumberSuffix() {
        PremiseNumberSuffix newValue = new PremiseNumberSuffix();
        this.getPremiseNumberSuffix().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link BuildingName} and adds it to buildingName.
     * This method is a short version for:
     * <code>
     * BuildingName buildingName = new BuildingName();
     * this.getBuildingName().add(buildingName); </code>
     *
     * @return the building name
     */
    public BuildingName createAndAddBuildingName() {
        BuildingName newValue = new BuildingName();
        this.getBuildingName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SubPremise} and adds it to subPremise.
     * This method is a short version for:
     * <code>
     * SubPremise subPremise = new SubPremise();
     * this.getSubPremise().add(subPremise); </code>
     *
     * @param subPremiseLocation     required parameter
     * @return the sub premise
     */
    public SubPremise createAndAddSubPremise(final SubPremise.SubPremiseLocation subPremiseLocation) {
        SubPremise newValue = new SubPremise(subPremiseLocation);
        this.getSubPremise().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Firm} and set it to firm.
     * 
     * This method is a short version for:
     * <code>
     * Firm firm = new Firm();
     * this.setFirm(firm); </code>
     *
     * @return the firm
     */
    public Firm createAndSetFirm() {
        Firm newValue = new Firm();
        this.setFirm(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link MailStop} and set it to mailStop.
     * 
     * This method is a short version for:
     * <code>
     * MailStop mailStop = new MailStop();
     * this.setMailStop(mailStop); </code>
     *
     * @return the mail stop
     */
    public MailStop createAndSetMailStop() {
        MailStop newValue = new MailStop();
        this.setMailStop(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalCode} and set it to postalCode.
     * 
     * This method is a short version for:
     * <code>
     * PostalCode postalCode = new PostalCode();
     * this.setPostalCode(postalCode); </code>
     *
     * @return the postal code
     */
    public PostalCode createAndSetPostalCode() {
        PostalCode newValue = new PostalCode();
        this.setPostalCode(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Premise} and set it to premise.
     * 
     * This method is a short version for:
     * <code>
     * Premise premise = new Premise();
     * this.setPremise(premise); </code>
     *
     * @param premiseLocation     required parameter
     * @param premiseNumber     required parameter
     * @param premiseNumberRange     required parameter
     * @return the premise
     */
    public Premise createAndSetPremise(final Premise.PremiseLocation premiseLocation, final List<PremiseNumber> premiseNumber, final Premise.PremiseNumberRange premiseNumberRange) {
        Premise newValue = new Premise(premiseLocation, premiseNumber, premiseNumberRange);
        this.setPremise(newValue);
        return newValue;
    }

    /**
     * Sets the value of the addressLine property Objects of the following type(s) are allowed in the list List<AddressLine>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressLine} instead.
     *
     * @param addressLine the new address line
     */
    public void setAddressLine(final List<AddressLine> addressLine) {
        this.addressLine = addressLine;
    }

    /**
     * add a value to the addressLine property collection.
     *
     * @param addressLine     Objects of the following type are allowed in the list: {@link AddressLine}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the premiseName property Objects of the following type(s) are allowed in the list List<PremiseName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseName} instead.
     *
     * @param premiseName the new premise name
     */
    public void setPremiseName(final List<Premise.PremiseName> premiseName) {
        this.premiseName = premiseName;
    }

    /**
     * add a value to the premiseName property collection.
     *
     * @param premiseName     Objects of the following type are allowed in the list: {@link Premise.PremiseName}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToPremiseName(final Premise.PremiseName premiseName) {
        this.getPremiseName().add(premiseName);
        return this;
    }

    /**
     * Sets the value of the premiseNumber property Objects of the following type(s) are allowed in the list List<PremiseNumber>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumber} instead.
     *
     * @param premiseNumber the new premise number
     */
    public void setPremiseNumber(final List<PremiseNumber> premiseNumber) {
        this.premiseNumber = premiseNumber;
    }

    /**
     * add a value to the premiseNumber property collection.
     *
     * @param premiseNumber     Objects of the following type are allowed in the list: {@link PremiseNumber}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToPremiseNumber(final PremiseNumber premiseNumber) {
        this.getPremiseNumber().add(premiseNumber);
        return this;
    }

    /**
     * Sets the value of the premiseNumberPrefix property Objects of the following type(s) are allowed in the list List<PremiseNumberPrefix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberPrefix} instead.
     *
     * @param premiseNumberPrefix the new premise number prefix
     */
    public void setPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
        this.premiseNumberPrefix = premiseNumberPrefix;
    }

    /**
     * add a value to the premiseNumberPrefix property collection.
     *
     * @param premiseNumberPrefix     Objects of the following type are allowed in the list: {@link PremiseNumberPrefix}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToPremiseNumberPrefix(final PremiseNumberPrefix premiseNumberPrefix) {
        this.getPremiseNumberPrefix().add(premiseNumberPrefix);
        return this;
    }

    /**
     * Sets the value of the premiseNumberSuffix property Objects of the following type(s) are allowed in the list List<PremiseNumberSuffix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberSuffix} instead.
     *
     * @param premiseNumberSuffix the new premise number suffix
     */
    public void setPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
        this.premiseNumberSuffix = premiseNumberSuffix;
    }

    /**
     * add a value to the premiseNumberSuffix property collection.
     *
     * @param premiseNumberSuffix     Objects of the following type are allowed in the list: {@link PremiseNumberSuffix}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToPremiseNumberSuffix(final PremiseNumberSuffix premiseNumberSuffix) {
        this.getPremiseNumberSuffix().add(premiseNumberSuffix);
        return this;
    }

    /**
     * Sets the value of the buildingName property Objects of the following type(s) are allowed in the list List<BuildingName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withBuildingName} instead.
     *
     * @param buildingName the new building name
     */
    public void setBuildingName(final List<BuildingName> buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * add a value to the buildingName property collection.
     *
     * @param buildingName     Objects of the following type are allowed in the list: {@link BuildingName}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToBuildingName(final BuildingName buildingName) {
        this.getBuildingName().add(buildingName);
        return this;
    }

    /**
     * Sets the value of the subPremise property Objects of the following type(s) are allowed in the list List<SubPremise>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubPremise} instead.
     *
     * @param subPremise the new sub premise
     */
    public void setSubPremise(final List<SubPremise> subPremise) {
        this.subPremise = subPremise;
    }

    /**
     * add a value to the subPremise property collection.
     *
     * @param subPremise     Objects of the following type are allowed in the list: {@link SubPremise}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToSubPremise(final SubPremise subPremise) {
        this.getSubPremise().add(subPremise);
        return this;
    }

    /**
     * Sets the value of the any property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAny} instead.
     *
     * @param any the new any
     */
    public void setAny(final List<Object> any) {
        this.any = any;
    }

    /**
     * add a value to the any property collection.
     *
     * @param any     Objects of the following type are allowed in the list: {@link Object}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public Premise addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param addressLine     required parameter
     * @return the premise
     * @see #setAddressLine(List<AddressLine>)
     */
    public Premise withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premiseName     required parameter
     * @return the premise
     * @see #setPremiseName(List<PremiseName>)
     */
    public Premise withPremiseName(final List<Premise.PremiseName> premiseName) {
        this.setPremiseName(premiseName);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premiseNumberPrefix     required parameter
     * @return the premise
     * @see #setPremiseNumberPrefix(List<PremiseNumberPrefix>)
     */
    public Premise withPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
        this.setPremiseNumberPrefix(premiseNumberPrefix);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premiseNumberSuffix     required parameter
     * @return the premise
     * @see #setPremiseNumberSuffix(List<PremiseNumberSuffix>)
     */
    public Premise withPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
        this.setPremiseNumberSuffix(premiseNumberSuffix);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param buildingName     required parameter
     * @return the premise
     * @see #setBuildingName(List<BuildingName>)
     */
    public Premise withBuildingName(final List<BuildingName> buildingName) {
        this.setBuildingName(buildingName);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param subPremise     required parameter
     * @return the premise
     * @see #setSubPremise(List<SubPremise>)
     */
    public Premise withSubPremise(final List<SubPremise> subPremise) {
        this.setSubPremise(subPremise);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param firm     required parameter
     * @return the premise
     * @see #setFirm(Firm)
     */
    public Premise withFirm(final Firm firm) {
        this.setFirm(firm);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param mailStop     required parameter
     * @return the premise
     * @see #setMailStop(MailStop)
     */
    public Premise withMailStop(final MailStop mailStop) {
        this.setMailStop(mailStop);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postalCode     required parameter
     * @return the premise
     * @see #setPostalCode(PostalCode)
     */
    public Premise withPostalCode(final PostalCode postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premise     required parameter
     * @return the premise
     * @see #setPremise(Premise)
     */
    public Premise withPremise(final Premise premise) {
        this.setPremise(premise);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param any     required parameter
     * @return the premise
     * @see #setAny(List<Object>)
     */
    public Premise withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param underscore     required parameter
     * @return the premise
     * @see #setUnderscore(String)
     */
    public Premise withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param xalPremiseDependency     required parameter
     * @return the premise
     * @see #setXalPremiseDependency(String)
     */
    public Premise withXalPremiseDependency(final String xalPremiseDependency) {
        this.setXalPremiseDependency(xalPremiseDependency);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premiseDependency     required parameter
     * @return the premise
     * @see #setPremiseDependency(String)
     */
    public Premise withPremiseDependency(final String premiseDependency) {
        this.setPremiseDependency(premiseDependency);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param premiseThoroughfareConnector     required parameter
     * @return the premise
     * @see #setPremiseThoroughfareConnector(String)
     */
    public Premise withPremiseThoroughfareConnector(final String premiseThoroughfareConnector) {
        this.setPremiseThoroughfareConnector(premiseThoroughfareConnector);
        return this;
    }

    @Override
    public Premise clone() {
        Premise copy;
        try {
            copy = ((Premise) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.premiseName = new ArrayList<Premise.PremiseName>((getPremiseName().size()));
        for (Premise.PremiseName iter: premiseName) {
            copy.premiseName.add(iter.clone());
        }
        copy.premiseLocation = ((premiseLocation == null)?null:((Premise.PremiseLocation) premiseLocation.clone()));
        copy.premiseNumber = new ArrayList<PremiseNumber>((getPremiseNumber().size()));
        for (PremiseNumber iter: premiseNumber) {
            copy.premiseNumber.add(iter.clone());
        }
        copy.premiseNumberRange = ((premiseNumberRange == null)?null:((Premise.PremiseNumberRange) premiseNumberRange.clone()));
        copy.premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>((getPremiseNumberPrefix().size()));
        for (PremiseNumberPrefix iter: premiseNumberPrefix) {
            copy.premiseNumberPrefix.add(iter.clone());
        }
        copy.premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>((getPremiseNumberSuffix().size()));
        for (PremiseNumberSuffix iter: premiseNumberSuffix) {
            copy.premiseNumberSuffix.add(iter.clone());
        }
        copy.buildingName = new ArrayList<BuildingName>((getBuildingName().size()));
        for (BuildingName iter: buildingName) {
            copy.buildingName.add(iter.clone());
        }
        copy.subPremise = new ArrayList<SubPremise>((getSubPremise().size()));
        for (SubPremise iter: subPremise) {
            copy.subPremise.add(iter.clone());
        }
        copy.firm = ((firm == null)?null:((Firm) firm.clone()));
        copy.mailStop = ((mailStop == null)?null:((MailStop) mailStop.clone()));
        copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
        copy.premise = ((premise == null)?null:((Premise) premise.clone()));
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }


    /**
     * The Class PremiseLocation.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "PremiseLocation", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PremiseLocation implements Cloneable
    {

        /** The content. */
        @XmlValue
        protected String content;
        
        /** The code. */
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        
        /** The other attributes. */
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Instantiates a new premise location.
         */
        public PremiseLocation() {
            super();
        }

        /**
         * Gets the content.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the content.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setContent(String value) {
            this.content = value;
        }

        /**
         * Gets the code.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the code.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setCode(String value) {
            this.code = value;
        }

        /**
         * Gets the other attributes.
         *
         * @return     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = ((prime*result)+((content == null)? 0 :content.hashCode()));
            result = ((prime*result)+((code == null)? 0 :code.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((obj instanceof Premise.PremiseLocation) == false) {
                return false;
            }
            Premise.PremiseLocation other = ((Premise.PremiseLocation) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
                    return false;
                }
            }
            if (code == null) {
                if (other.code!= null) {
                    return false;
                }
            } else {
                if (code.equals(other.code) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * fluent setter.
         *
         * @param content     required parameter
         * @return the premise. premise location
         * @see #setContent(String)
         */
        public Premise.PremiseLocation withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param code     required parameter
         * @return the premise. premise location
         * @see #setCode(String)
         */
        public Premise.PremiseLocation withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public Premise.PremiseLocation clone() {
            Premise.PremiseLocation copy;
            try {
                copy = ((Premise.PremiseLocation) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }


    /**
     * The Class PremiseName.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "PremiseName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PremiseName implements Cloneable
    {

        /** The content. */
        @XmlValue
        protected String content;
        
        /** The underscore. */
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        
        /** The type occurrence. */
        @XmlAttribute(name = "TypeOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String typeOccurrence;
        
        /** The code. */
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        
        /** The other attributes. */
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Instantiates a new premise name.
         */
        public PremiseName() {
            super();
        }

        /**
         * Gets the content.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the content.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setContent(String value) {
            this.content = value;
        }

        /**
         * Gets the underscore.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getUnderscore() {
            return underscore;
        }

        /**
         * Sets the underscore.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setUnderscore(String value) {
            this.underscore = value;
        }

        /**
         * Gets the type occurrence.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getTypeOccurrence() {
            return typeOccurrence;
        }

        /**
         * Sets the type occurrence.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setTypeOccurrence(String value) {
            this.typeOccurrence = value;
        }

        /**
         * Gets the code.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the code.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setCode(String value) {
            this.code = value;
        }

        /**
         * Gets the other attributes.
         *
         * @return     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = ((prime*result)+((content == null)? 0 :content.hashCode()));
            result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
            result = ((prime*result)+((typeOccurrence == null)? 0 :typeOccurrence.hashCode()));
            result = ((prime*result)+((code == null)? 0 :code.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((obj instanceof Premise.PremiseName) == false) {
                return false;
            }
            Premise.PremiseName other = ((Premise.PremiseName) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
                    return false;
                }
            }
            if (underscore == null) {
                if (other.underscore!= null) {
                    return false;
                }
            } else {
                if (underscore.equals(other.underscore) == false) {
                    return false;
                }
            }
            if (typeOccurrence == null) {
                if (other.typeOccurrence!= null) {
                    return false;
                }
            } else {
                if (typeOccurrence.equals(other.typeOccurrence) == false) {
                    return false;
                }
            }
            if (code == null) {
                if (other.code!= null) {
                    return false;
                }
            } else {
                if (code.equals(other.code) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * fluent setter.
         *
         * @param content     required parameter
         * @return the premise. premise name
         * @see #setContent(String)
         */
        public Premise.PremiseName withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param underscore     required parameter
         * @return the premise. premise name
         * @see #setUnderscore(String)
         */
        public Premise.PremiseName withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param typeOccurrence     required parameter
         * @return the premise. premise name
         * @see #setTypeOccurrence(String)
         */
        public Premise.PremiseName withTypeOccurrence(final String typeOccurrence) {
            this.setTypeOccurrence(typeOccurrence);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param code     required parameter
         * @return the premise. premise name
         * @see #setCode(String)
         */
        public Premise.PremiseName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public Premise.PremiseName clone() {
            Premise.PremiseName copy;
            try {
                copy = ((Premise.PremiseName) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }


    /**
     * The Class PremiseNumberRange.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "premiseNumberRangeFrom",
        "premiseNumberRangeTo"
    })
    @XmlRootElement(name = "PremiseNumberRange", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PremiseNumberRange implements Cloneable
    {

        /** The premise number range from. */
        @XmlElement(name = "PremiseNumberRangeFrom", required = true)
        protected Premise.PremiseNumberRange.PremiseNumberRangeFrom premiseNumberRangeFrom;
        
        /** The premise number range to. */
        @XmlElement(name = "PremiseNumberRangeTo", required = true)
        protected Premise.PremiseNumberRange.PremiseNumberRangeTo premiseNumberRangeTo;
        
        /** The range. */
        @XmlAttribute(name = "RangeType")
        @XmlSchemaType(name = "anySimpleType")
        protected String range;
        
        /** The indicator. */
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        
        /** The separator. */
        @XmlAttribute(name = "Separator")
        @XmlSchemaType(name = "anySimpleType")
        protected String separator;
        
        /** The underscore. */
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        
        /** The indicator occurence. */
        @XmlAttribute(name = "IndicatorOccurence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String indicatorOccurence;
        
        /** The number range occurence. */
        @XmlAttribute(name = "NumberRangeOccurence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String numberRangeOccurence;

        /**
         * Value constructor with only mandatory fields.
         *
         * @param premiseNumberRangeFrom     required parameter
         * @param premiseNumberRangeTo     required parameter
         */
        public PremiseNumberRange(final Premise.PremiseNumberRange.PremiseNumberRangeFrom premiseNumberRangeFrom, final Premise.PremiseNumberRange.PremiseNumberRangeTo premiseNumberRangeTo) {
            super();
            this.premiseNumberRangeFrom = premiseNumberRangeFrom;
            this.premiseNumberRangeTo = premiseNumberRangeTo;
        }

        /**
         * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
         * 
         */
        @Deprecated
        private PremiseNumberRange() {
            super();
        }

        /**
         * Gets the premise number range from.
         *
         * @return     possible object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeFrom}
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeFrom getPremiseNumberRangeFrom() {
            return premiseNumberRangeFrom;
        }

        /**
         * Sets the premise number range from.
         *
         * @param value     allowed object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeFrom}
         */
        public void setPremiseNumberRangeFrom(Premise.PremiseNumberRange.PremiseNumberRangeFrom value) {
            this.premiseNumberRangeFrom = value;
        }

        /**
         * Gets the premise number range to.
         *
         * @return     possible object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeTo}
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeTo getPremiseNumberRangeTo() {
            return premiseNumberRangeTo;
        }

        /**
         * Sets the premise number range to.
         *
         * @param value     allowed object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeTo}
         */
        public void setPremiseNumberRangeTo(Premise.PremiseNumberRange.PremiseNumberRangeTo value) {
            this.premiseNumberRangeTo = value;
        }

        /**
         * Gets the range.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getRange() {
            return range;
        }

        /**
         * Sets the range.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setRange(String value) {
            this.range = value;
        }

        /**
         * Gets the indicator.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getIndicator() {
            return indicator;
        }

        /**
         * Sets the indicator.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setIndicator(String value) {
            this.indicator = value;
        }

        /**
         * Gets the separator.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getSeparator() {
            return separator;
        }

        /**
         * Sets the separator.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setSeparator(String value) {
            this.separator = value;
        }

        /**
         * Gets the underscore.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getUnderscore() {
            return underscore;
        }

        /**
         * Sets the underscore.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setUnderscore(String value) {
            this.underscore = value;
        }

        /**
         * Gets the indicator occurence.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getIndicatorOccurence() {
            return indicatorOccurence;
        }

        /**
         * Sets the indicator occurence.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setIndicatorOccurence(String value) {
            this.indicatorOccurence = value;
        }

        /**
         * Gets the number range occurence.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getNumberRangeOccurence() {
            return numberRangeOccurence;
        }

        /**
         * Sets the number range occurence.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setNumberRangeOccurence(String value) {
            this.numberRangeOccurence = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = ((prime*result)+((premiseNumberRangeFrom == null)? 0 :premiseNumberRangeFrom.hashCode()));
            result = ((prime*result)+((premiseNumberRangeTo == null)? 0 :premiseNumberRangeTo.hashCode()));
            result = ((prime*result)+((range == null)? 0 :range.hashCode()));
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
            result = ((prime*result)+((separator == null)? 0 :separator.hashCode()));
            result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
            result = ((prime*result)+((indicatorOccurence == null)? 0 :indicatorOccurence.hashCode()));
            result = ((prime*result)+((numberRangeOccurence == null)? 0 :numberRangeOccurence.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((obj instanceof Premise.PremiseNumberRange) == false) {
                return false;
            }
            Premise.PremiseNumberRange other = ((Premise.PremiseNumberRange) obj);
            if (premiseNumberRangeFrom == null) {
                if (other.premiseNumberRangeFrom!= null) {
                    return false;
                }
            } else {
                if (premiseNumberRangeFrom.equals(other.premiseNumberRangeFrom) == false) {
                    return false;
                }
            }
            if (premiseNumberRangeTo == null) {
                if (other.premiseNumberRangeTo!= null) {
                    return false;
                }
            } else {
                if (premiseNumberRangeTo.equals(other.premiseNumberRangeTo) == false) {
                    return false;
                }
            }
            if (range == null) {
                if (other.range!= null) {
                    return false;
                }
            } else {
                if (range.equals(other.range) == false) {
                    return false;
                }
            }
            if (indicator == null) {
                if (other.indicator!= null) {
                    return false;
                }
            } else {
                if (indicator.equals(other.indicator) == false) {
                    return false;
                }
            }
            if (separator == null) {
                if (other.separator!= null) {
                    return false;
                }
            } else {
                if (separator.equals(other.separator) == false) {
                    return false;
                }
            }
            if (underscore == null) {
                if (other.underscore!= null) {
                    return false;
                }
            } else {
                if (underscore.equals(other.underscore) == false) {
                    return false;
                }
            }
            if (indicatorOccurence == null) {
                if (other.indicatorOccurence!= null) {
                    return false;
                }
            } else {
                if (indicatorOccurence.equals(other.indicatorOccurence) == false) {
                    return false;
                }
            }
            if (numberRangeOccurence == null) {
                if (other.numberRangeOccurence!= null) {
                    return false;
                }
            } else {
                if (numberRangeOccurence.equals(other.numberRangeOccurence) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Creates a new instance of {@link Premise.PremiseNumberRange.PremiseNumberRangeFrom} and set it to premiseNumberRangeFrom.
         * 
         * This method is a short version for:
         * <code>
         * PremiseNumberRangeFrom premiseNumberRangeFrom = new PremiseNumberRangeFrom();
         * this.setPremiseNumberRangeFrom(premiseNumberRangeFrom); </code>
         *
         * @param premiseNumber     required parameter
         * @return the premise. premise number range. premise number range from
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeFrom createAndSetPremiseNumberRangeFrom(final List<PremiseNumber> premiseNumber) {
            Premise.PremiseNumberRange.PremiseNumberRangeFrom newValue = new Premise.PremiseNumberRange.PremiseNumberRangeFrom(premiseNumber);
            this.setPremiseNumberRangeFrom(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link Premise.PremiseNumberRange.PremiseNumberRangeTo} and set it to premiseNumberRangeTo.
         * 
         * This method is a short version for:
         * <code>
         * PremiseNumberRangeTo premiseNumberRangeTo = new PremiseNumberRangeTo();
         * this.setPremiseNumberRangeTo(premiseNumberRangeTo); </code>
         *
         * @param premiseNumber     required parameter
         * @return the premise. premise number range. premise number range to
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeTo createAndSetPremiseNumberRangeTo(final List<PremiseNumber> premiseNumber) {
            Premise.PremiseNumberRange.PremiseNumberRangeTo newValue = new Premise.PremiseNumberRange.PremiseNumberRangeTo(premiseNumber);
            this.setPremiseNumberRangeTo(newValue);
            return newValue;
        }

        /**
         * fluent setter.
         *
         * @param range     required parameter
         * @return the premise. premise number range
         * @see #setRange(String)
         */
        public Premise.PremiseNumberRange withRange(final String range) {
            this.setRange(range);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param indicator     required parameter
         * @return the premise. premise number range
         * @see #setIndicator(String)
         */
        public Premise.PremiseNumberRange withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param separator     required parameter
         * @return the premise. premise number range
         * @see #setSeparator(String)
         */
        public Premise.PremiseNumberRange withSeparator(final String separator) {
            this.setSeparator(separator);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param underscore     required parameter
         * @return the premise. premise number range
         * @see #setUnderscore(String)
         */
        public Premise.PremiseNumberRange withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param indicatorOccurence     required parameter
         * @return the premise. premise number range
         * @see #setIndicatorOccurence(String)
         */
        public Premise.PremiseNumberRange withIndicatorOccurence(final String indicatorOccurence) {
            this.setIndicatorOccurence(indicatorOccurence);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param numberRangeOccurence     required parameter
         * @return the premise. premise number range
         * @see #setNumberRangeOccurence(String)
         */
        public Premise.PremiseNumberRange withNumberRangeOccurence(final String numberRangeOccurence) {
            this.setNumberRangeOccurence(numberRangeOccurence);
            return this;
        }

        @Override
        public Premise.PremiseNumberRange clone() {
            Premise.PremiseNumberRange copy;
            try {
                copy = ((Premise.PremiseNumberRange) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.premiseNumberRangeFrom = ((premiseNumberRangeFrom == null)?null:((Premise.PremiseNumberRange.PremiseNumberRangeFrom) premiseNumberRangeFrom.clone()));
            copy.premiseNumberRangeTo = ((premiseNumberRangeTo == null)?null:((Premise.PremiseNumberRange.PremiseNumberRangeTo) premiseNumberRangeTo.clone()));
            return copy;
        }


        /**
         * The Class PremiseNumberRangeFrom.
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "addressLine",
            "premiseNumberPrefix",
            "premiseNumber",
            "premiseNumberSuffix"
        })
        @XmlRootElement(name = "PremiseNumberRangeFrom", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class PremiseNumberRangeFrom implements Cloneable
        {

            /** The address line. */
            @XmlElement(name = "AddressLine")
            protected List<AddressLine> addressLine;
            
            /** The premise number prefix. */
            @XmlElement(name = "PremiseNumberPrefix")
            protected List<PremiseNumberPrefix> premiseNumberPrefix;
            
            /** The premise number. */
            @XmlElement(name = "PremiseNumber", required = true)
            protected List<PremiseNumber> premiseNumber;
            
            /** The premise number suffix. */
            @XmlElement(name = "PremiseNumberSuffix")
            protected List<PremiseNumberSuffix> premiseNumberSuffix;

            /**
             * Value constructor with only mandatory fields.
             *
             * @param premiseNumber     required parameter
             */
            public PremiseNumberRangeFrom(final List<PremiseNumber> premiseNumber) {
                super();
                this.premiseNumber = premiseNumber;
            }

            /**
             * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
             * 
             */
            @Deprecated
            private PremiseNumberRangeFrom() {
                super();
            }

            /**
             * Gets the address line.
             *
             * @return the address line
             */
            public List<AddressLine> getAddressLine() {
                if (addressLine == null) {
                    addressLine = new ArrayList<AddressLine>();
                }
                return this.addressLine;
            }

            /**
             * Gets the premise number prefix.
             *
             * @return the premise number prefix
             */
            public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
                if (premiseNumberPrefix == null) {
                    premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
                }
                return this.premiseNumberPrefix;
            }

            /**
             * Gets the premise number.
             *
             * @return the premise number
             */
            public List<PremiseNumber> getPremiseNumber() {
                if (premiseNumber == null) {
                    premiseNumber = new ArrayList<PremiseNumber>();
                }
                return this.premiseNumber;
            }

            /**
             * Gets the premise number suffix.
             *
             * @return the premise number suffix
             */
            public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
                if (premiseNumberSuffix == null) {
                    premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
                }
                return this.premiseNumberSuffix;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
                result = ((prime*result)+((premiseNumberPrefix == null)? 0 :premiseNumberPrefix.hashCode()));
                result = ((prime*result)+((premiseNumber == null)? 0 :premiseNumber.hashCode()));
                result = ((prime*result)+((premiseNumberSuffix == null)? 0 :premiseNumberSuffix.hashCode()));
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if ((obj instanceof Premise.PremiseNumberRange.PremiseNumberRangeFrom) == false) {
                    return false;
                }
                Premise.PremiseNumberRange.PremiseNumberRangeFrom other = ((Premise.PremiseNumberRange.PremiseNumberRangeFrom) obj);
                if (addressLine == null) {
                    if (other.addressLine!= null) {
                        return false;
                    }
                } else {
                    if (addressLine.equals(other.addressLine) == false) {
                        return false;
                    }
                }
                if (premiseNumberPrefix == null) {
                    if (other.premiseNumberPrefix!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumberPrefix.equals(other.premiseNumberPrefix) == false) {
                        return false;
                    }
                }
                if (premiseNumber == null) {
                    if (other.premiseNumber!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumber.equals(other.premiseNumber) == false) {
                        return false;
                    }
                }
                if (premiseNumberSuffix == null) {
                    if (other.premiseNumberSuffix!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumberSuffix.equals(other.premiseNumberSuffix) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * Creates a new instance of {@link AddressLine} and adds it to addressLine.
             * This method is a short version for:
             * <code>
             * AddressLine addressLine = new AddressLine();
             * this.getAddressLine().add(addressLine); </code>
             *
             * @return the address line
             */
            public AddressLine createAndAddAddressLine() {
                AddressLine newValue = new AddressLine();
                this.getAddressLine().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumberPrefix} and adds it to premiseNumberPrefix.
             * This method is a short version for:
             * <code>
             * PremiseNumberPrefix premiseNumberPrefix = new PremiseNumberPrefix();
             * this.getPremiseNumberPrefix().add(premiseNumberPrefix); </code>
             *
             * @return the premise number prefix
             */
            public PremiseNumberPrefix createAndAddPremiseNumberPrefix() {
                PremiseNumberPrefix newValue = new PremiseNumberPrefix();
                this.getPremiseNumberPrefix().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumber} and adds it to premiseNumber.
             * This method is a short version for:
             * <code>
             * PremiseNumber premiseNumber = new PremiseNumber();
             * this.getPremiseNumber().add(premiseNumber); </code>
             *
             * @return the premise number
             */
            public PremiseNumber createAndAddPremiseNumber() {
                PremiseNumber newValue = new PremiseNumber();
                this.getPremiseNumber().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumberSuffix} and adds it to premiseNumberSuffix.
             * This method is a short version for:
             * <code>
             * PremiseNumberSuffix premiseNumberSuffix = new PremiseNumberSuffix();
             * this.getPremiseNumberSuffix().add(premiseNumberSuffix); </code>
             *
             * @return the premise number suffix
             */
            public PremiseNumberSuffix createAndAddPremiseNumberSuffix() {
                PremiseNumberSuffix newValue = new PremiseNumberSuffix();
                this.getPremiseNumberSuffix().add(newValue);
                return newValue;
            }

            /**
             * Sets the value of the addressLine property Objects of the following type(s) are allowed in the list List<AddressLine>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressLine} instead.
             *
             * @param addressLine the new address line
             */
            public void setAddressLine(final List<AddressLine> addressLine) {
                this.addressLine = addressLine;
            }

            /**
             * add a value to the addressLine property collection.
             *
             * @param addressLine     Objects of the following type are allowed in the list: {@link AddressLine}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom addToAddressLine(final AddressLine addressLine) {
                this.getAddressLine().add(addressLine);
                return this;
            }

            /**
             * Sets the value of the premiseNumberPrefix property Objects of the following type(s) are allowed in the list List<PremiseNumberPrefix>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberPrefix} instead.
             *
             * @param premiseNumberPrefix the new premise number prefix
             */
            public void setPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
                this.premiseNumberPrefix = premiseNumberPrefix;
            }

            /**
             * add a value to the premiseNumberPrefix property collection.
             *
             * @param premiseNumberPrefix     Objects of the following type are allowed in the list: {@link PremiseNumberPrefix}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom addToPremiseNumberPrefix(final PremiseNumberPrefix premiseNumberPrefix) {
                this.getPremiseNumberPrefix().add(premiseNumberPrefix);
                return this;
            }

            /**
             * Sets the value of the premiseNumber property Objects of the following type(s) are allowed in the list List<PremiseNumber>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumber} instead.
             *
             * @param premiseNumber the new premise number
             */
            public void setPremiseNumber(final List<PremiseNumber> premiseNumber) {
                this.premiseNumber = premiseNumber;
            }

            /**
             * add a value to the premiseNumber property collection.
             *
             * @param premiseNumber     Objects of the following type are allowed in the list: {@link PremiseNumber}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom addToPremiseNumber(final PremiseNumber premiseNumber) {
                this.getPremiseNumber().add(premiseNumber);
                return this;
            }

            /**
             * Sets the value of the premiseNumberSuffix property Objects of the following type(s) are allowed in the list List<PremiseNumberSuffix>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberSuffix} instead.
             *
             * @param premiseNumberSuffix the new premise number suffix
             */
            public void setPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
                this.premiseNumberSuffix = premiseNumberSuffix;
            }

            /**
             * add a value to the premiseNumberSuffix property collection.
             *
             * @param premiseNumberSuffix     Objects of the following type are allowed in the list: {@link PremiseNumberSuffix}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom addToPremiseNumberSuffix(final PremiseNumberSuffix premiseNumberSuffix) {
                this.getPremiseNumberSuffix().add(premiseNumberSuffix);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param addressLine     required parameter
             * @return the premise. premise number range. premise number range from
             * @see #setAddressLine(List<AddressLine>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom withAddressLine(final List<AddressLine> addressLine) {
                this.setAddressLine(addressLine);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param premiseNumberPrefix     required parameter
             * @return the premise. premise number range. premise number range from
             * @see #setPremiseNumberPrefix(List<PremiseNumberPrefix>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom withPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
                this.setPremiseNumberPrefix(premiseNumberPrefix);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param premiseNumberSuffix     required parameter
             * @return the premise. premise number range. premise number range from
             * @see #setPremiseNumberSuffix(List<PremiseNumberSuffix>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom withPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
                this.setPremiseNumberSuffix(premiseNumberSuffix);
                return this;
            }

            @Override
            public Premise.PremiseNumberRange.PremiseNumberRangeFrom clone() {
                Premise.PremiseNumberRange.PremiseNumberRangeFrom copy;
                try {
                    copy = ((Premise.PremiseNumberRange.PremiseNumberRangeFrom) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
                for (AddressLine iter: addressLine) {
                    copy.addressLine.add(iter.clone());
                }
                copy.premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>((getPremiseNumberPrefix().size()));
                for (PremiseNumberPrefix iter: premiseNumberPrefix) {
                    copy.premiseNumberPrefix.add(iter.clone());
                }
                copy.premiseNumber = new ArrayList<PremiseNumber>((getPremiseNumber().size()));
                for (PremiseNumber iter: premiseNumber) {
                    copy.premiseNumber.add(iter.clone());
                }
                copy.premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>((getPremiseNumberSuffix().size()));
                for (PremiseNumberSuffix iter: premiseNumberSuffix) {
                    copy.premiseNumberSuffix.add(iter.clone());
                }
                return copy;
            }

        }


        /**
         * The Class PremiseNumberRangeTo.
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "addressLine",
            "premiseNumberPrefix",
            "premiseNumber",
            "premiseNumberSuffix"
        })
        @XmlRootElement(name = "PremiseNumberRangeTo", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class PremiseNumberRangeTo implements Cloneable
        {

            /** The address line. */
            @XmlElement(name = "AddressLine")
            protected List<AddressLine> addressLine;
            
            /** The premise number prefix. */
            @XmlElement(name = "PremiseNumberPrefix")
            protected List<PremiseNumberPrefix> premiseNumberPrefix;
            
            /** The premise number. */
            @XmlElement(name = "PremiseNumber", required = true)
            protected List<PremiseNumber> premiseNumber;
            
            /** The premise number suffix. */
            @XmlElement(name = "PremiseNumberSuffix")
            protected List<PremiseNumberSuffix> premiseNumberSuffix;

            /**
             * Value constructor with only mandatory fields.
             *
             * @param premiseNumber     required parameter
             */
            public PremiseNumberRangeTo(final List<PremiseNumber> premiseNumber) {
                super();
                this.premiseNumber = premiseNumber;
            }

            /**
             * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
             * 
             */
            @Deprecated
            private PremiseNumberRangeTo() {
                super();
            }

            /**
             * Gets the address line.
             *
             * @return the address line
             */
            public List<AddressLine> getAddressLine() {
                if (addressLine == null) {
                    addressLine = new ArrayList<AddressLine>();
                }
                return this.addressLine;
            }

            /**
             * Gets the premise number prefix.
             *
             * @return the premise number prefix
             */
            public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
                if (premiseNumberPrefix == null) {
                    premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
                }
                return this.premiseNumberPrefix;
            }

            /**
             * Gets the premise number.
             *
             * @return the premise number
             */
            public List<PremiseNumber> getPremiseNumber() {
                if (premiseNumber == null) {
                    premiseNumber = new ArrayList<PremiseNumber>();
                }
                return this.premiseNumber;
            }

            /**
             * Gets the premise number suffix.
             *
             * @return the premise number suffix
             */
            public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
                if (premiseNumberSuffix == null) {
                    premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
                }
                return this.premiseNumberSuffix;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
                result = ((prime*result)+((premiseNumberPrefix == null)? 0 :premiseNumberPrefix.hashCode()));
                result = ((prime*result)+((premiseNumber == null)? 0 :premiseNumber.hashCode()));
                result = ((prime*result)+((premiseNumberSuffix == null)? 0 :premiseNumberSuffix.hashCode()));
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if ((obj instanceof Premise.PremiseNumberRange.PremiseNumberRangeTo) == false) {
                    return false;
                }
                Premise.PremiseNumberRange.PremiseNumberRangeTo other = ((Premise.PremiseNumberRange.PremiseNumberRangeTo) obj);
                if (addressLine == null) {
                    if (other.addressLine!= null) {
                        return false;
                    }
                } else {
                    if (addressLine.equals(other.addressLine) == false) {
                        return false;
                    }
                }
                if (premiseNumberPrefix == null) {
                    if (other.premiseNumberPrefix!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumberPrefix.equals(other.premiseNumberPrefix) == false) {
                        return false;
                    }
                }
                if (premiseNumber == null) {
                    if (other.premiseNumber!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumber.equals(other.premiseNumber) == false) {
                        return false;
                    }
                }
                if (premiseNumberSuffix == null) {
                    if (other.premiseNumberSuffix!= null) {
                        return false;
                    }
                } else {
                    if (premiseNumberSuffix.equals(other.premiseNumberSuffix) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * Creates a new instance of {@link AddressLine} and adds it to addressLine.
             * This method is a short version for:
             * <code>
             * AddressLine addressLine = new AddressLine();
             * this.getAddressLine().add(addressLine); </code>
             *
             * @return the address line
             */
            public AddressLine createAndAddAddressLine() {
                AddressLine newValue = new AddressLine();
                this.getAddressLine().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumberPrefix} and adds it to premiseNumberPrefix.
             * This method is a short version for:
             * <code>
             * PremiseNumberPrefix premiseNumberPrefix = new PremiseNumberPrefix();
             * this.getPremiseNumberPrefix().add(premiseNumberPrefix); </code>
             *
             * @return the premise number prefix
             */
            public PremiseNumberPrefix createAndAddPremiseNumberPrefix() {
                PremiseNumberPrefix newValue = new PremiseNumberPrefix();
                this.getPremiseNumberPrefix().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumber} and adds it to premiseNumber.
             * This method is a short version for:
             * <code>
             * PremiseNumber premiseNumber = new PremiseNumber();
             * this.getPremiseNumber().add(premiseNumber); </code>
             *
             * @return the premise number
             */
            public PremiseNumber createAndAddPremiseNumber() {
                PremiseNumber newValue = new PremiseNumber();
                this.getPremiseNumber().add(newValue);
                return newValue;
            }

            /**
             * Creates a new instance of {@link PremiseNumberSuffix} and adds it to premiseNumberSuffix.
             * This method is a short version for:
             * <code>
             * PremiseNumberSuffix premiseNumberSuffix = new PremiseNumberSuffix();
             * this.getPremiseNumberSuffix().add(premiseNumberSuffix); </code>
             *
             * @return the premise number suffix
             */
            public PremiseNumberSuffix createAndAddPremiseNumberSuffix() {
                PremiseNumberSuffix newValue = new PremiseNumberSuffix();
                this.getPremiseNumberSuffix().add(newValue);
                return newValue;
            }

            /**
             * Sets the value of the addressLine property Objects of the following type(s) are allowed in the list List<AddressLine>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressLine} instead.
             *
             * @param addressLine the new address line
             */
            public void setAddressLine(final List<AddressLine> addressLine) {
                this.addressLine = addressLine;
            }

            /**
             * add a value to the addressLine property collection.
             *
             * @param addressLine     Objects of the following type are allowed in the list: {@link AddressLine}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo addToAddressLine(final AddressLine addressLine) {
                this.getAddressLine().add(addressLine);
                return this;
            }

            /**
             * Sets the value of the premiseNumberPrefix property Objects of the following type(s) are allowed in the list List<PremiseNumberPrefix>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberPrefix} instead.
             *
             * @param premiseNumberPrefix the new premise number prefix
             */
            public void setPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
                this.premiseNumberPrefix = premiseNumberPrefix;
            }

            /**
             * add a value to the premiseNumberPrefix property collection.
             *
             * @param premiseNumberPrefix     Objects of the following type are allowed in the list: {@link PremiseNumberPrefix}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo addToPremiseNumberPrefix(final PremiseNumberPrefix premiseNumberPrefix) {
                this.getPremiseNumberPrefix().add(premiseNumberPrefix);
                return this;
            }

            /**
             * Sets the value of the premiseNumber property Objects of the following type(s) are allowed in the list List<PremiseNumber>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumber} instead.
             *
             * @param premiseNumber the new premise number
             */
            public void setPremiseNumber(final List<PremiseNumber> premiseNumber) {
                this.premiseNumber = premiseNumber;
            }

            /**
             * add a value to the premiseNumber property collection.
             *
             * @param premiseNumber     Objects of the following type are allowed in the list: {@link PremiseNumber}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo addToPremiseNumber(final PremiseNumber premiseNumber) {
                this.getPremiseNumber().add(premiseNumber);
                return this;
            }

            /**
             * Sets the value of the premiseNumberSuffix property Objects of the following type(s) are allowed in the list List<PremiseNumberSuffix>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPremiseNumberSuffix} instead.
             *
             * @param premiseNumberSuffix the new premise number suffix
             */
            public void setPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
                this.premiseNumberSuffix = premiseNumberSuffix;
            }

            /**
             * add a value to the premiseNumberSuffix property collection.
             *
             * @param premiseNumberSuffix     Objects of the following type are allowed in the list: {@link PremiseNumberSuffix}
             * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo addToPremiseNumberSuffix(final PremiseNumberSuffix premiseNumberSuffix) {
                this.getPremiseNumberSuffix().add(premiseNumberSuffix);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param addressLine     required parameter
             * @return the premise. premise number range. premise number range to
             * @see #setAddressLine(List<AddressLine>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo withAddressLine(final List<AddressLine> addressLine) {
                this.setAddressLine(addressLine);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param premiseNumberPrefix     required parameter
             * @return the premise. premise number range. premise number range to
             * @see #setPremiseNumberPrefix(List<PremiseNumberPrefix>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo withPremiseNumberPrefix(final List<PremiseNumberPrefix> premiseNumberPrefix) {
                this.setPremiseNumberPrefix(premiseNumberPrefix);
                return this;
            }

            /**
             * fluent setter.
             *
             * @param premiseNumberSuffix     required parameter
             * @return the premise. premise number range. premise number range to
             * @see #setPremiseNumberSuffix(List<PremiseNumberSuffix>)
             */
            public Premise.PremiseNumberRange.PremiseNumberRangeTo withPremiseNumberSuffix(final List<PremiseNumberSuffix> premiseNumberSuffix) {
                this.setPremiseNumberSuffix(premiseNumberSuffix);
                return this;
            }

            @Override
            public Premise.PremiseNumberRange.PremiseNumberRangeTo clone() {
                Premise.PremiseNumberRange.PremiseNumberRangeTo copy;
                try {
                    copy = ((Premise.PremiseNumberRange.PremiseNumberRangeTo) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
                for (AddressLine iter: addressLine) {
                    copy.addressLine.add(iter.clone());
                }
                copy.premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>((getPremiseNumberPrefix().size()));
                for (PremiseNumberPrefix iter: premiseNumberPrefix) {
                    copy.premiseNumberPrefix.add(iter.clone());
                }
                copy.premiseNumber = new ArrayList<PremiseNumber>((getPremiseNumber().size()));
                for (PremiseNumber iter: premiseNumber) {
                    copy.premiseNumber.add(iter.clone());
                }
                copy.premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>((getPremiseNumberSuffix().size()));
                for (PremiseNumberSuffix iter: premiseNumberSuffix) {
                    copy.premiseNumberSuffix.add(iter.clone());
                }
                return copy;
            }

        }

    }

}