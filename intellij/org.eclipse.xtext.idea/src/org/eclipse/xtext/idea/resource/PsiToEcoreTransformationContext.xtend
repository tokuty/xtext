package org.eclipse.xtext.idea.resource

import com.google.inject.Inject
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafElement
import javax.inject.Provider
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.eclipse.xtext.Action
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.CrossReference
import org.eclipse.xtext.EnumRule
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.ParserRule
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.idea.nodemodel.IASTNodeAwareNodeModelBuilder
import org.eclipse.xtext.nodemodel.ICompositeNode
import org.eclipse.xtext.nodemodel.ILeafNode
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage
import org.eclipse.xtext.nodemodel.impl.RootNode
import org.eclipse.xtext.parser.IAstFactory
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider
import org.eclipse.xtext.parser.impl.DatatypeRuleToken
import org.eclipse.xtext.psi.IPsiModelAssociator
import org.eclipse.xtext.psi.impl.BaseXtextFile

import static extension org.eclipse.xtext.GrammarUtil.*

class PsiToEcoreTransformationContext {

	@Inject
	IAstFactory semanticModelBuilder

	@Inject
	IPsiModelAssociator psiModelAssociator

	@Inject
	ISyntaxErrorMessageProvider syntaxErrorProvider

	@Inject
	Provider<PsiToEcoreTransformationContext> psiToEcoreTransformationContextProvider
	
	@Accessors(PUBLIC_GETTER)
	BaseXtextFile xtextFile

	@Accessors(PUBLIC_GETTER)
	boolean hadErrors

	@Accessors(PUBLIC_GETTER)
	EObject current
	
	@Accessors
	RuleCall actionRuleCall

	@Accessors(PUBLIC_GETTER)
	DatatypeRuleToken datatypeRuleToken

	INode lastConsumedNode

	@Accessors(PUBLIC_GETTER)
	ICompositeNode currentNode

	extension IASTNodeAwareNodeModelBuilder nodeModelBuilder

	def getNodesMapping() {
		nodeModelBuilder.nodesMapping
	}
	
	def getReverseNodesMapping() {
		nodeModelBuilder.reverseNodesMapping
	}

	def branch() {
		val childTransformationContext = psiToEcoreTransformationContextProvider.get
		childTransformationContext.currentNode = currentNode
		childTransformationContext.lastConsumedNode = lastConsumedNode
		childTransformationContext.nodeModelBuilder = nodeModelBuilder
		childTransformationContext
	}

	def withDatatypeRule() {
		datatypeRuleToken = new AntlrDatatypeRuleToken
		this
	}

	def sync(PsiToEcoreTransformationContext childTransformationContext) {
		currentNode = childTransformationContext.currentNode
		lastConsumedNode = childTransformationContext.lastConsumedNode
		if (childTransformationContext.hadErrors) {
			hadErrors = true
		}
		this
	}

	def merge(PsiToEcoreTransformationContext childTransformationContext) {
		if (current == null) {
			current = childTransformationContext.current
		}
		if (datatypeRuleToken != null && childTransformationContext.datatypeRuleToken != null) {
			datatypeRuleToken.merge(childTransformationContext.datatypeRuleToken)
		}
		this
	}

	def compress() {
		val newCurrentNode = currentNode.compressAndReturnParent
		if (currentNode == lastConsumedNode) {
			lastConsumedNode = newCurrentNode
		}
		currentNode = newCurrentNode
		this
	}

	def void newLeafNode(LeafElement it, EObject grammarElement, String ruleName) {
		lastConsumedNode = newLeafNode(grammarElement, currentNode)
		mergeDatatypeRuleToken

		if (grammarElement.ensureModelElementCreated) {
			val assignment = grammarElement.containingAssignment
			if (assignment.booleanAssignment) {
				assign(true, grammarElement, ruleName)
			} else {
				assign(text, grammarElement, ruleName)
			}
		}
	}

	def void newLeafNode(ASTNode it) {
		newLeafNode(currentNode)
	}

	def void newCompositeNode(CompositeElement it) {
		currentNode = newCompositeNode(currentNode)
	}

	def ensureModelElementCreated(EObject grammarElement) {
		if (grammarElement == null) {
			return false
		}
		if (grammarElement instanceof Action) {
			current = semanticModelBuilder.create(grammarElement.type.classifier)
			associateWithSemanticElement(currentNode)
			return true
		}
		if (!grammarElement.assigned) {
			return false
		}
		if (current != null) {
			return true
		}
		val classifier = grammarElement.containingParserRule.type.classifier
		current = semanticModelBuilder.create(classifier)
		associateWithSemanticElement(
			switch grammarElement {
				case grammarElement.terminalRuleCall,
				case grammarElement instanceof Keyword,
				CrossReference case grammarElement.terminal.terminalRuleCall,
				CrossReference case grammarElement.terminal instanceof Keyword:
					currentNode
				default:
					currentNode.parent
			}
		)
		true
	}

	protected def void mergeDatatypeRuleToken(LeafElement it) {
		if (datatypeRuleToken != null) {
			datatypeRuleToken.merge(new AntlrDatatypeRuleToken => [ token |
				token.text = text
				token.startOffset = startOffset
			])
		}
	}

	def assign(EObject value, Action action) {
		if (action.feature != null) {
			current.assign(action.operator, action.feature, value, null, currentNode)
		}
	}

	def void assign(Object value, EObject grammarElement, String ruleName) {
		current.assign(value, grammarElement, ruleName)
	}

	protected def void assign(EObject parent, Object value, EObject grammarElement, String ruleName) {
		if (grammarElement.assigned) {
			val node = switch grammarElement {
				RuleCall case grammarElement.rule instanceof EnumRule || grammarElement.rule instanceof ParserRule:
					currentNode
				default:
					lastConsumedNode
			}
			parent.assign(
				grammarElement.containingAssignment,
				value,
				ruleName,
				node
			)
		}
	}

	protected def associateWithSemanticElement(ICompositeNode node) {
		node.associateWithSemanticElement(current)
		val astNode = reverseNodesMapping.get(node)?.last
		if (astNode != null) {
			psiModelAssociator.associate(current)[|astNode.psi]
		}
	}

	protected def assign(EObject parent, Assignment assignment, Object value, String ruleName, INode node) {
		parent.assign(assignment.operator, assignment.feature, value, ruleName, node)
	}

	protected def assign(EObject parent, String operator, String feature, Object value, String ruleName, INode node) {
		try {
			if (operator == '+=') {
				semanticModelBuilder.add(parent, feature, value, ruleName, node)
			} else {
				semanticModelBuilder.set(parent, feature, value, ruleName, node)
			}
		} catch (ValueConverterException vce) {
			handleValueConverterException(vce)
		}
	}

	def void appendSyntaxError(PsiErrorElement errorElement) {
		hadErrors = true;
		val errorContext = createErrorContext(errorElement)
		val error = syntaxErrorProvider.getSyntaxErrorMessage(errorContext)
		lastLeafNode.appendError(error)
	}

	def protected INode getLastLeafNode() {
		var iter = currentNode.asTreeIterable.iterator
		while (iter.hasPrevious) {
			var previous = iter.previous
			if (previous instanceof ILeafNode)
				return previous
		}
		if (currentNode instanceof RootNode) {
			currentNode.firstChild
		} else {
			currentNode
		}
	}

	protected def ISyntaxErrorMessageProvider.IParserErrorContext createErrorContext(PsiErrorElement errorElement) {
		new ParserErrorContext(this, errorElement)
	}

	protected def ISyntaxErrorMessageProvider.IValueConverterErrorContext createValueConverterErrorContext(
		ValueConverterException vce) {
		new ValueConverterErrorContext(this, vce)
	}

	protected def handleValueConverterException(ValueConverterException vce) {
		hadErrors = true;
		val cause = vce.cause as Exception
		if (vce != cause) {
			val errorContext = vce.createValueConverterErrorContext
			val error = syntaxErrorProvider.getSyntaxErrorMessage(errorContext)
			val node = vce.node ?: currentNode.lastChild
			node.appendError(error)
		} else {
			throw new RuntimeException(vce)
		}
	}

	protected def appendError(INode node, SyntaxErrorMessage error) {
		if (node.syntaxErrorMessage == null) {
			val newNode = node.syntaxError = error
			if (node == currentNode)
				currentNode = newNode as ICompositeNode
		}
	}

	protected def newRootNode(BaseXtextFile xtextFile) {
		hadErrors = false
		this.xtextFile = xtextFile
		currentNode = nodeModelBuilder.newRootNode(xtextFile.text)
	}

	static class PsiToEcoreTransformationContextProvider {

		@Inject
		Provider<IASTNodeAwareNodeModelBuilder> astNodeModelBuilderProvider

		@Inject
		Provider<PsiToEcoreTransformationContext> psiToEcoreTransformationContextProvider

		def newTransformationContext(BaseXtextFile xtextFile) {
			val context = psiToEcoreTransformationContextProvider.get
			context.nodeModelBuilder = astNodeModelBuilderProvider.get
			context.newRootNode(xtextFile)
			context
		}

	}

}

@FinalFieldsConstructor
class ErrorContext {

	val PsiToEcoreTransformationContext transformationContext

	def getCurrentContext() {
		if (currentNode != null)
			currentNode.semanticElement
	}

	def getCurrentNode() {
		transformationContext.currentNode
	}
}

@FinalFieldsConstructor
class ValueConverterErrorContext extends ErrorContext implements ISyntaxErrorMessageProvider.IValueConverterErrorContext {

	@Accessors
	val ValueConverterException valueConverterException

	override getDefaultMessage() {
		val cause = valueConverterException.cause as Exception
		var result = if(cause != null) cause.message else valueConverterException.message
		if (result == null)
			result = valueConverterException.message
		if (result == null)
			result == if(cause != null) cause.class.simpleName else valueConverterException.class.simpleName
		result
	}

}

@FinalFieldsConstructor
class ParserErrorContext extends ErrorContext implements ISyntaxErrorMessageProvider.IParserErrorContext {

	val PsiErrorElement errorElement

	override getRecognitionException() {
		null
	}

	override getTokenNames() {
		null
	}

	override getDefaultMessage() {
		errorElement.errorDescription
	}

}
