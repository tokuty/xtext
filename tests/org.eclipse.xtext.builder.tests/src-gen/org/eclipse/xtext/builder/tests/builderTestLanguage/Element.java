/**
 */
package org.eclipse.xtext.builder.tests.builderTestLanguage;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.xtext.builder.tests.builderTestLanguage.Element#getReferences <em>References</em>}</li>
 *   <li>{@link org.eclipse.xtext.builder.tests.builderTestLanguage.Element#getOtherRefs <em>Other Refs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.xtext.builder.tests.builderTestLanguage.BuilderTestLanguagePackage#getElement()
 * @model
 * @generated
 */
public interface Element extends NamedElement
{
  /**
   * Returns the value of the '<em><b>References</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>References</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>References</em>' reference.
   * @see #setReferences(Element)
   * @see org.eclipse.xtext.builder.tests.builderTestLanguage.BuilderTestLanguagePackage#getElement_References()
   * @model
   * @generated
   */
  Element getReferences();

  /**
   * Sets the value of the '{@link org.eclipse.xtext.builder.tests.builderTestLanguage.Element#getReferences <em>References</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>References</em>' reference.
   * @see #getReferences()
   * @generated
   */
  void setReferences(Element value);

  /**
   * Returns the value of the '<em><b>Other Refs</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.xtext.builder.tests.builderTestLanguage.Element}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Other Refs</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Other Refs</em>' reference list.
   * @see org.eclipse.xtext.builder.tests.builderTestLanguage.BuilderTestLanguagePackage#getElement_OtherRefs()
   * @model
   * @generated
   */
  EList<Element> getOtherRefs();

} // Element
