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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gama.core.lang.gaml.Statement#getKey <em>Key</em>}</li>
 *   <li>{@link gama.core.lang.gaml.Statement#getFirstFacet <em>First Facet</em>}</li>
 *   <li>{@link gama.core.lang.gaml.Statement#getExpr <em>Expr</em>}</li>
 *   <li>{@link gama.core.lang.gaml.Statement#getFacets <em>Facets</em>}</li>
 *   <li>{@link gama.core.lang.gaml.Statement#getBlock <em>Block</em>}</li>
 * </ul>
 *
 * @see gama.core.lang.gaml.GamlPackage#getStatement()
 * @model
 * @generated
 */
public interface Statement extends EObject
{
  /**
   * Returns the value of the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Key</em>' attribute.
   * @see #setKey(String)
   * @see gama.core.lang.gaml.GamlPackage#getStatement_Key()
   * @model
   * @generated
   */
  String getKey();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.Statement#getKey <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Key</em>' attribute.
   * @see #getKey()
   * @generated
   */
  void setKey(String value);

  /**
   * Returns the value of the '<em><b>First Facet</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>First Facet</em>' attribute.
   * @see #setFirstFacet(String)
   * @see gama.core.lang.gaml.GamlPackage#getStatement_FirstFacet()
   * @model
   * @generated
   */
  String getFirstFacet();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.Statement#getFirstFacet <em>First Facet</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First Facet</em>' attribute.
   * @see #getFirstFacet()
   * @generated
   */
  void setFirstFacet(String value);

  /**
   * Returns the value of the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expr</em>' containment reference.
   * @see #setExpr(Expression)
   * @see gama.core.lang.gaml.GamlPackage#getStatement_Expr()
   * @model containment="true"
   * @generated
   */
  Expression getExpr();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.Statement#getExpr <em>Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expr</em>' containment reference.
   * @see #getExpr()
   * @generated
   */
  void setExpr(Expression value);

  /**
   * Returns the value of the '<em><b>Facets</b></em>' containment reference list.
   * The list contents are of type {@link gama.core.lang.gaml.Facet}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Facets</em>' containment reference list.
   * @see gama.core.lang.gaml.GamlPackage#getStatement_Facets()
   * @model containment="true"
   * @generated
   */
  EList<Facet> getFacets();

  /**
   * Returns the value of the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Block</em>' containment reference.
   * @see #setBlock(Block)
   * @see gama.core.lang.gaml.GamlPackage#getStatement_Block()
   * @model containment="true"
   * @generated
   */
  Block getBlock();

  /**
   * Sets the value of the '{@link gama.core.lang.gaml.Statement#getBlock <em>Block</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Block</em>' containment reference.
   * @see #getBlock()
   * @generated
   */
  void setBlock(Block value);

} // Statement