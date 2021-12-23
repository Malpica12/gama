/*******************************************************************************************************
 *
 * PostOffice.java, in gama.ext.libs, is part of the source code of the
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
 * The Class PostOffice.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "postOfficeName",
    "postOfficeNumber",
    "postalRoute",
    "postBox",
    "postalCode",
    "any"
})
@XmlRootElement(name = "PostOffice")
public class PostOffice implements Cloneable
{

    /** The address line. */
    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    
    /** The post office name. */
    @XmlElement(name = "PostOfficeName")
    protected List<PostOffice.PostOfficeName> postOfficeName;
    
    /** The post office number. */
    @XmlElement(name = "PostOfficeNumber")
    protected PostOffice.PostOfficeNumber postOfficeNumber;
    
    /** The postal route. */
    @XmlElement(name = "PostalRoute")
    protected PostalRoute postalRoute;
    
    /** The post box. */
    @XmlElement(name = "PostBox")
    protected PostBox postBox;
    
    /** The postal code. */
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    
    /** The any. */
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    
    /** The underscore. */
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    
    /** The indicator. */
    @XmlAttribute(name = "Indicator")
    @XmlSchemaType(name = "anySimpleType")
    protected String indicator;
    
    /** The other attributes. */
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Instantiates a new post office.
     */
    public PostOffice() {
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
     * Gets the post office name.
     *
     * @return the post office name
     */
    public List<PostOffice.PostOfficeName> getPostOfficeName() {
        if (postOfficeName == null) {
            postOfficeName = new ArrayList<PostOffice.PostOfficeName>();
        }
        return this.postOfficeName;
    }

    /**
     * Gets the post office number.
     *
     * @return     possible object is
     *     {@link PostOffice.PostOfficeNumber}
     */
    public PostOffice.PostOfficeNumber getPostOfficeNumber() {
        return postOfficeNumber;
    }

    /**
     * Sets the post office number.
     *
     * @param value     allowed object is
     *     {@link PostOffice.PostOfficeNumber}
     */
    public void setPostOfficeNumber(PostOffice.PostOfficeNumber value) {
        this.postOfficeNumber = value;
    }

    /**
     * Gets the postal route.
     *
     * @return     possible object is
     *     {@link PostalRoute}
     */
    public PostalRoute getPostalRoute() {
        return postalRoute;
    }

    /**
     * Sets the postal route.
     *
     * @param value     allowed object is
     *     {@link PostalRoute}
     */
    public void setPostalRoute(PostalRoute value) {
        this.postalRoute = value;
    }

    /**
     * Gets the post box.
     *
     * @return     possible object is
     *     {@link PostBox}
     */
    public PostBox getPostBox() {
        return postBox;
    }

    /**
     * Sets the post box.
     *
     * @param value     allowed object is
     *     {@link PostBox}
     */
    public void setPostBox(PostBox value) {
        this.postBox = value;
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
        result = ((prime*result)+((postOfficeName == null)? 0 :postOfficeName.hashCode()));
        result = ((prime*result)+((postOfficeNumber == null)? 0 :postOfficeNumber.hashCode()));
        result = ((prime*result)+((postalRoute == null)? 0 :postalRoute.hashCode()));
        result = ((prime*result)+((postBox == null)? 0 :postBox.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
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
        if ((obj instanceof PostOffice) == false) {
            return false;
        }
        PostOffice other = ((PostOffice) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (postOfficeName == null) {
            if (other.postOfficeName!= null) {
                return false;
            }
        } else {
            if (postOfficeName.equals(other.postOfficeName) == false) {
                return false;
            }
        }
        if (postOfficeNumber == null) {
            if (other.postOfficeNumber!= null) {
                return false;
            }
        } else {
            if (postOfficeNumber.equals(other.postOfficeNumber) == false) {
                return false;
            }
        }
        if (postalRoute == null) {
            if (other.postalRoute!= null) {
                return false;
            }
        } else {
            if (postalRoute.equals(other.postalRoute) == false) {
                return false;
            }
        }
        if (postBox == null) {
            if (other.postBox!= null) {
                return false;
            }
        } else {
            if (postBox.equals(other.postBox) == false) {
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
        if (indicator == null) {
            if (other.indicator!= null) {
                return false;
            }
        } else {
            if (indicator.equals(other.indicator) == false) {
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
     * Creates a new instance of {@link PostOffice.PostOfficeName} and adds it to postOfficeName.
     * This method is a short version for:
     * <code>
     * PostOfficeName postOfficeName = new PostOfficeName();
     * this.getPostOfficeName().add(postOfficeName); </code>
     *
     * @return the post office. post office name
     */
    public PostOffice.PostOfficeName createAndAddPostOfficeName() {
        PostOffice.PostOfficeName newValue = new PostOffice.PostOfficeName();
        this.getPostOfficeName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostOffice.PostOfficeNumber} and set it to postOfficeNumber.
     * 
     * This method is a short version for:
     * <code>
     * PostOfficeNumber postOfficeNumber = new PostOfficeNumber();
     * this.setPostOfficeNumber(postOfficeNumber); </code>
     *
     * @return the post office. post office number
     */
    public PostOffice.PostOfficeNumber createAndSetPostOfficeNumber() {
        PostOffice.PostOfficeNumber newValue = new PostOffice.PostOfficeNumber();
        this.setPostOfficeNumber(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalRoute} and set it to postalRoute.
     * 
     * This method is a short version for:
     * <code>
     * PostalRoute postalRoute = new PostalRoute();
     * this.setPostalRoute(postalRoute); </code>
     *
     * @param postalRouteName     required parameter
     * @param postalRouteNumber     required parameter
     * @return the postal route
     */
    public PostalRoute createAndSetPostalRoute(final List<PostalRoute.PostalRouteName> postalRouteName, final PostalRoute.PostalRouteNumber postalRouteNumber) {
        PostalRoute newValue = new PostalRoute(postalRouteName, postalRouteNumber);
        this.setPostalRoute(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostBox} and set it to postBox.
     * 
     * This method is a short version for:
     * <code>
     * PostBox postBox = new PostBox();
     * this.setPostBox(postBox); </code>
     *
     * @param postBoxNumber     required parameter
     * @return the post box
     */
    public PostBox createAndSetPostBox(final PostBox.PostBoxNumber postBoxNumber) {
        PostBox newValue = new PostBox(postBoxNumber);
        this.setPostBox(newValue);
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
    public PostOffice addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the postOfficeName property Objects of the following type(s) are allowed in the list List<PostOfficeName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPostOfficeName} instead.
     *
     * @param postOfficeName the new post office name
     */
    public void setPostOfficeName(final List<PostOffice.PostOfficeName> postOfficeName) {
        this.postOfficeName = postOfficeName;
    }

    /**
     * add a value to the postOfficeName property collection.
     *
     * @param postOfficeName     Objects of the following type are allowed in the list: {@link PostOffice.PostOfficeName}
     * @return     <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
     */
    public PostOffice addToPostOfficeName(final PostOffice.PostOfficeName postOfficeName) {
        this.getPostOfficeName().add(postOfficeName);
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
    public PostOffice addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param addressLine     required parameter
     * @return the post office
     * @see #setAddressLine(List<AddressLine>)
     */
    public PostOffice withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postOfficeName     required parameter
     * @return the post office
     * @see #setPostOfficeName(List<PostOfficeName>)
     */
    public PostOffice withPostOfficeName(final List<PostOffice.PostOfficeName> postOfficeName) {
        this.setPostOfficeName(postOfficeName);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postOfficeNumber     required parameter
     * @return the post office
     * @see #setPostOfficeNumber(PostOfficeNumber)
     */
    public PostOffice withPostOfficeNumber(final PostOffice.PostOfficeNumber postOfficeNumber) {
        this.setPostOfficeNumber(postOfficeNumber);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postalRoute     required parameter
     * @return the post office
     * @see #setPostalRoute(PostalRoute)
     */
    public PostOffice withPostalRoute(final PostalRoute postalRoute) {
        this.setPostalRoute(postalRoute);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postBox     required parameter
     * @return the post office
     * @see #setPostBox(PostBox)
     */
    public PostOffice withPostBox(final PostBox postBox) {
        this.setPostBox(postBox);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param postalCode     required parameter
     * @return the post office
     * @see #setPostalCode(PostalCode)
     */
    public PostOffice withPostalCode(final PostalCode postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param any     required parameter
     * @return the post office
     * @see #setAny(List<Object>)
     */
    public PostOffice withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param underscore     required parameter
     * @return the post office
     * @see #setUnderscore(String)
     */
    public PostOffice withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter.
     *
     * @param indicator     required parameter
     * @return the post office
     * @see #setIndicator(String)
     */
    public PostOffice withIndicator(final String indicator) {
        this.setIndicator(indicator);
        return this;
    }

    @Override
    public PostOffice clone() {
        PostOffice copy;
        try {
            copy = ((PostOffice) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.postOfficeName = new ArrayList<PostOffice.PostOfficeName>((getPostOfficeName().size()));
        for (PostOffice.PostOfficeName iter: postOfficeName) {
            copy.postOfficeName.add(iter.clone());
        }
        copy.postOfficeNumber = ((postOfficeNumber == null)?null:((PostOffice.PostOfficeNumber) postOfficeNumber.clone()));
        copy.postalRoute = ((postalRoute == null)?null:((PostalRoute) postalRoute.clone()));
        copy.postBox = ((postBox == null)?null:((PostBox) postBox.clone()));
        copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }


    /**
     * The Class PostOfficeName.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "PostOfficeName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostOfficeName implements Cloneable
    {

        /** The content. */
        @XmlValue
        protected String content;
        
        /** The underscore. */
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        
        /** The code. */
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        
        /** The other attributes. */
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Instantiates a new post office name.
         */
        public PostOfficeName() {
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
            if ((obj instanceof PostOffice.PostOfficeName) == false) {
                return false;
            }
            PostOffice.PostOfficeName other = ((PostOffice.PostOfficeName) obj);
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
         * @return the post office. post office name
         * @see #setContent(String)
         */
        public PostOffice.PostOfficeName withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param underscore     required parameter
         * @return the post office. post office name
         * @see #setUnderscore(String)
         */
        public PostOffice.PostOfficeName withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param code     required parameter
         * @return the post office. post office name
         * @see #setCode(String)
         */
        public PostOffice.PostOfficeName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public PostOffice.PostOfficeName clone() {
            PostOffice.PostOfficeName copy;
            try {
                copy = ((PostOffice.PostOfficeName) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }


    /**
     * The Class PostOfficeNumber.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "PostOfficeNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostOfficeNumber implements Cloneable
    {

        /** The content. */
        @XmlValue
        protected String content;
        
        /** The indicator. */
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        
        /** The indicator occurrence. */
        @XmlAttribute(name = "IndicatorOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String indicatorOccurrence;
        
        /** The code. */
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        
        /** The other attributes. */
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Instantiates a new post office number.
         */
        public PostOfficeNumber() {
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
         * Gets the indicator occurrence.
         *
         * @return     possible object is
         *     {@link String}
         */
        public String getIndicatorOccurrence() {
            return indicatorOccurrence;
        }

        /**
         * Sets the indicator occurrence.
         *
         * @param value     allowed object is
         *     {@link String}
         */
        public void setIndicatorOccurrence(String value) {
            this.indicatorOccurrence = value;
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
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
            result = ((prime*result)+((indicatorOccurrence == null)? 0 :indicatorOccurrence.hashCode()));
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
            if ((obj instanceof PostOffice.PostOfficeNumber) == false) {
                return false;
            }
            PostOffice.PostOfficeNumber other = ((PostOffice.PostOfficeNumber) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
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
            if (indicatorOccurrence == null) {
                if (other.indicatorOccurrence!= null) {
                    return false;
                }
            } else {
                if (indicatorOccurrence.equals(other.indicatorOccurrence) == false) {
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
         * @return the post office. post office number
         * @see #setContent(String)
         */
        public PostOffice.PostOfficeNumber withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param indicator     required parameter
         * @return the post office. post office number
         * @see #setIndicator(String)
         */
        public PostOffice.PostOfficeNumber withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param indicatorOccurrence     required parameter
         * @return the post office. post office number
         * @see #setIndicatorOccurrence(String)
         */
        public PostOffice.PostOfficeNumber withIndicatorOccurrence(final String indicatorOccurrence) {
            this.setIndicatorOccurrence(indicatorOccurrence);
            return this;
        }

        /**
         * fluent setter.
         *
         * @param code     required parameter
         * @return the post office. post office number
         * @see #setCode(String)
         */
        public PostOffice.PostOfficeNumber withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public PostOffice.PostOfficeNumber clone() {
            PostOffice.PostOfficeNumber copy;
            try {
                copy = ((PostOffice.PostOfficeNumber) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }

}