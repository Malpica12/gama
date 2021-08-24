/*******************************************************************************************************
 *
 * DoubleLiteralImpl.java, in gama.core.lang, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.GamlPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Double Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class DoubleLiteralImpl extends TerminalExpressionImpl implements DoubleLiteral
{
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->.
   *
   * @generated 
   */
  protected DoubleLiteralImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return GamlPackage.Literals.DOUBLE_LITERAL;
  }

} //DoubleLiteralImpl
