package org.eclipse.xtext.idea.resource;

import com.google.common.base.Objects;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.idea.resource.PsiToEcoreTransformationContext;
import org.eclipse.xtext.nodemodel.ICompositeNode;

@FinalFieldsConstructor
@SuppressWarnings("all")
public class ErrorContext {
  private final PsiToEcoreTransformationContext transformationContext;
  
  public EObject getCurrentContext() {
    EObject _xifexpression = null;
    ICompositeNode _currentNode = this.getCurrentNode();
    boolean _notEquals = (!Objects.equal(_currentNode, null));
    if (_notEquals) {
      ICompositeNode _currentNode_1 = this.getCurrentNode();
      _xifexpression = _currentNode_1.getSemanticElement();
    }
    return _xifexpression;
  }
  
  public ICompositeNode getCurrentNode() {
    return this.transformationContext.getCurrentNode();
  }
  
  public ErrorContext(final PsiToEcoreTransformationContext transformationContext) {
    super();
    this.transformationContext = transformationContext;
  }
}
