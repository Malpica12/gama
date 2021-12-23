/**
 * *
 * This file has been automatically generated by XText and is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 * 
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 * 
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 */
package gama.core.lang.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gama.core.lang.gaml.Import#getImportURI <em>Import URI</em>}</li>
 * </ul>
 *
 * @see gama.core.lang.gaml.GamlPackage#getImport()
 * @model
 * @generated
 */
public interface Import extends VarDefinition
{
  /**
   * Returns the value of the '<em><b>Import URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Import URI</em>' attribute.
   * @see #setImportURI(String)
   * @see gama.core.lang.gaml.GamlPackage#getImport_ImportURI()
   * @model
   * @generated
   */
  String getImportURI();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.Import#getImportURI <em>Import URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Import URI</em>' attribute.
   * @see #getImportURI()
   * @generated
   */
  void setImportURI(String value);

} // Import