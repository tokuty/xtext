/*
* generated by Xtext
*/
package org.eclipse.xtext.testlanguages.parseTreeConstruction;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parsetree.reconstr.IEObjectConsumer;

import org.eclipse.xtext.testlanguages.services.ActionTestLanguage2GrammarAccess;

import com.google.inject.Inject;

@SuppressWarnings("all")
public class ActionTestLanguage2ParsetreeConstructor extends org.eclipse.xtext.parsetree.reconstr.impl.AbstractParseTreeConstructor {
		
	@Inject
	private ActionTestLanguage2GrammarAccess grammarAccess;
	
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
			case 0: return new ORing_Group(this, this, 0, inst);
			case 1: return new Value_ValueAssignment(this, this, 1, inst);
			default: return null;
		}	
	}	
}
	

/************ begin Rule ORing ****************
 *
 * // see https://www.eclipse.org/forums/index.php/mv/msg/798729/1407452/#msg_1407452
 * ORing:
 * 	Value ({ORing.disjuncts+=current} "|" disjuncts+=Value)*;
 *
 **/

// Value ({ORing.disjuncts+=current} "|" disjuncts+=Value)*
protected class ORing_Group extends GroupToken {
	
	public ORing_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getORingAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ORing_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new ORing_ValueParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getORingAccess().getORingDisjunctsAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getValueRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// Value
protected class ORing_ValueParserRuleCall_0 extends RuleCallToken {
	
	public ORing_ValueParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getORingAccess().getValueParserRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Value_ValueAssignment(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getValueRule().getType().getClassifier())
			return null;
		if(checkForRecursion(Value_ValueAssignment.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// ({ORing.disjuncts+=current} "|" disjuncts+=Value)*
protected class ORing_Group_1 extends GroupToken {
	
	public ORing_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getORingAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ORing_DisjunctsAssignment_1_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getORingAccess().getORingDisjunctsAction_1_0().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// {ORing.disjuncts+=current}
protected class ORing_ORingDisjunctsAction_1_0 extends ActionToken  {

	public ORing_ORingDisjunctsAction_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Action getGrammarElement() {
		return grammarAccess.getORingAccess().getORingDisjunctsAction_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ORing_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new ORing_ValueParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		Object val = eObjectConsumer.getConsumable("disjuncts", false);
		if(val == null) return null;
		if(!eObjectConsumer.isConsumedWithLastConsumtion("disjuncts")) return null;
		return createEObjectConsumer((EObject) val);
	}
}

// "|"
protected class ORing_VerticalLineKeyword_1_1 extends KeywordToken  {
	
	public ORing_VerticalLineKeyword_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Keyword getGrammarElement() {
		return grammarAccess.getORingAccess().getVerticalLineKeyword_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ORing_ORingDisjunctsAction_1_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// disjuncts+=Value
protected class ORing_DisjunctsAssignment_1_2 extends AssignmentToken  {
	
	public ORing_DisjunctsAssignment_1_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getORingAccess().getDisjunctsAssignment_1_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Value_ValueAssignment(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("disjuncts",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("disjuncts");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getORingAccess().getDisjunctsValueParserRuleCall_1_2_0(); 
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
			case 0: return new ORing_VerticalLineKeyword_1_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule ORing ****************/


/************ begin Rule Value ****************
 *
 * Value:
 * 	value="a";
 *
 **/

// value="a"
protected class Value_ValueAssignment extends AssignmentToken  {
	
	public Value_ValueAssignment(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getValueAccess().getValueAssignment();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getValueRule().getType().getClassifier())
			return null;
		if((value = eObjectConsumer.getConsumable("value",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("value");
		if(keywordSerializer.isValid(obj.getEObject(), grammarAccess.getValueAccess().getValueAKeyword_0(), value, null)) {
			type = AssignmentType.KEYWORD;
			element = grammarAccess.getValueAccess().getValueAKeyword_0();
			return obj;
		}
		return null;
	}

}

/************ end Rule Value ****************/

}
