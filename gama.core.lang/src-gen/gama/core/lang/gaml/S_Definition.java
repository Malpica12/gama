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
 * A representation of the model object '<em><b>SDefinition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gama.core.lang.gaml.S_Definition#getTkey <em>Tkey</em>}</li>
 *   <li>{@link gama.core.lang.gaml.S_Definition#getArgs <em>Args</em>}</li>
 * </ul>
 *
 * @see gama.core.lang.gaml.GamlPackage#getS_Definition()
 * @model
 * @generated
 */
public interface S_Definition extends S_Declaration, ActionDefinition
{
  /**
   * Returns the value of the '<em><b>Tkey</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tkey</em>' containment reference.
   * @see #setTkey(Expression)
   * @see gama.core.lang.gaml.GamlPackage#getS_Definition_Tkey()
   * @model containment="true"
   * @generated
   */
  Expression getTkey();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.S_Definition#getTkey <em>Tkey</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Tkey</em>' containment reference.
   * @see #getTkey()
   * @generated
   */
  void setTkey(Expression value);

  /**
   * Returns the value of the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Args</em>' containment reference.
   * @see #setArgs(ActionArguments)
   * @see gama.core.lang.gaml.GamlPackage#getS_Definition_Args()
   * @model containment="true"
   * @generated
   */
  ActionArguments getArgs();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.S_Definition#getArgs <em>Args</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Args</em>' containment reference.
   * @see #getArgs()
   * @generated
   */
  void setArgs(ActionArguments value);

} // S_Definition