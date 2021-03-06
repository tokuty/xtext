/*
* generated by Xtext
*/
package org.eclipse.xtext.metamodelreferencing.tests.parseTreeConstruction;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parsetree.reconstr.IEObjectConsumer;

import org.eclipse.xtext.metamodelreferencing.tests.services.MetamodelRefTestLanguageGrammarAccess;

import com.google.inject.Inject;

@SuppressWarnings("all")
public class MetamodelRefTestLanguageParsetreeConstructor extends org.eclipse.xtext.parsetree.reconstr.impl.AbstractParseTreeConstructor {
		
	@Inject
	private MetamodelRefTestLanguageGrammarAccess grammarAccess;
	
	@Override
	protected AbstractToken getRootToken(IEObjectConsumer inst) {
		return new ThisRootNode(inst);	
	}
	
protected class ThisRootNode extends RootToken {
	public ThisRootNode(IEObjectConsumer inst) {
		super(inst);
	}
	
	@Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Foo_Group(this, this, 0, inst);
			case 1: return new NameRef_RuleAssignment(this, this, 1, inst);
			case 2: return new MyRule_NameAssignment(this, this, 2, inst);
			default: return null;
		}	
	}	
}
	

/************ begin Rule Foo ****************
 *
 * Foo:
 * 	name=ID nameRefs+=NameRef*;
 *
 **/

// name=ID nameRefs+=NameRef*
protected class Foo_Group extends GroupToken {
	
	public Foo_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getFooAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Foo_NameRefsAssignment_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Foo_NameAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getFooRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// name=ID
protected class Foo_NameAssignment_0 extends AssignmentToken  {
	
	public Foo_NameAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getFooAccess().getNameAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("name",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("name");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getFooAccess().getNameIDTerminalRuleCall_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getFooAccess().getNameIDTerminalRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// nameRefs+=NameRef*
protected class Foo_NameRefsAssignment_1 extends AssignmentToken  {
	
	public Foo_NameRefsAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getFooAccess().getNameRefsAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NameRef_RuleAssignment(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("nameRefs",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("nameRefs");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getNameRefRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getFooAccess().getNameRefsNameRefParserRuleCall_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Foo_NameRefsAssignment_1(lastRuleCallOrigin, next, actIndex, consumed);
			case 1: return new Foo_NameAssignment_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


/************ end Rule Foo ****************/


/************ begin Rule NameRef ****************
 *
 * NameRef returns xtext::RuleCall:
 * 	rule=[xtext::ParserRule];
 *
 **/

// rule=[xtext::ParserRule]
protected class NameRef_RuleAssignment extends AssignmentToken  {
	
	public NameRef_RuleAssignment(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNameRefAccess().getRuleAssignment();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNameRefRule().getType().getClassifier())
			return null;
		if((value = eObjectConsumer.getConsumable("rule",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("rule");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::CrossReferenceImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getNameRefAccess().getRuleParserRuleCrossReference_0().getType().getClassifier())) {
				type = AssignmentType.CROSS_REFERENCE;
				element = grammarAccess.getNameRefAccess().getRuleParserRuleCrossReference_0(); 
				return obj;
			}
		}
		return null;
	}

}

/************ end Rule NameRef ****************/


/************ begin Rule MyRule ****************
 *
 * MyRule returns xtext::ParserRule:
 * 	name=ID;
 *
 **/

// name=ID
protected class MyRule_NameAssignment extends AssignmentToken  {
	
	public MyRule_NameAssignment(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getMyRuleAccess().getNameAssignment();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getMyRuleRule().getType().getClassifier())
			return null;
		if((value = eObjectConsumer.getConsumable("name",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("name");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getMyRuleAccess().getNameIDTerminalRuleCall_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getMyRuleAccess().getNameIDTerminalRuleCall_0();
			return obj;
		}
		return null;
	}

}

/************ end Rule MyRule ****************/

}
