/*******************************************************************************************************
 *
 * Facet.java, in gama.core.lang, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Facet</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Facet#getKey <em>Key</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Facet#getExpr <em>Expr</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Facet#getBlock <em>Block</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getFacet()
 * @model
 * @generated
 */
public interface Facet extends VarDefinition
{
  /**
   * Returns the value of the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Key</em>' attribute.
   * @see #setKey(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getFacet_Key()
   * @model
   * @generated
   */
  String getKey();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Facet#getKey <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Key</em>' attribute.
   * @see #getKey()
   * @generated
   */
  void setKey(String value);

  /**
   * Returns the value of the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expr</em>' containment reference.
   * @see #setExpr(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getFacet_Expr()
   * @model containment="true"
   * @generated
   */
  Expression getExpr();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Facet#getExpr <em>Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expr</em>' containment reference.
   * @see #getExpr()
   * @generated
   */
  void setExpr(Expression value);

  /**
   * Returns the value of the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Block</em>' containment reference.
   * @see #setBlock(Block)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getFacet_Block()
   * @model containment="true"
   * @generated
   */
  Block getBlock();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Facet#getBlock <em>Block</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Block</em>' containment reference.
   * @see #getBlock()
   * @generated
   */
  void setBlock(Block value);

} // Facet
