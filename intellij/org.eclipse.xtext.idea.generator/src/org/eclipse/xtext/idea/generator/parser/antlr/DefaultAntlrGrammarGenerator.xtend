package org.eclipse.xtext.idea.generator.parser.antlr

import com.google.inject.Inject
import com.google.inject.Singleton
import org.eclipse.xtext.AbstractElement
import org.eclipse.xtext.AbstractRule
import org.eclipse.xtext.Action
import org.eclipse.xtext.Alternatives
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.CrossReference
import org.eclipse.xtext.EnumRule
import org.eclipse.xtext.Grammar
import org.eclipse.xtext.Group
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.ParserRule
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.TerminalRule
import org.eclipse.xtext.UnorderedGroup
import org.eclipse.xtext.generator.Naming
import org.eclipse.xtext.generator.Xtend2ExecutionContext
import org.eclipse.xtext.generator.grammarAccess.GrammarAccess
import org.eclipse.xtext.generator.parser.antlr.AntlrOptions

import static extension org.eclipse.xtext.GrammarUtil.*
import static extension org.eclipse.xtext.generator.parser.antlr.AntlrGrammarGenUtil.*
import static extension org.eclipse.xtext.generator.parser.antlr.TerminalRuleToLexerBody.*

@Singleton
class DefaultAntlrGrammarGenerator {
	
	@Inject
	protected extension Naming
	
	@Inject
	protected extension GrammarAccess
	
	@Inject
	protected extension NamingExtensions
	
	@Inject
	protected extension GrammarAccessExtensions
	
	@Inject
	protected extension XtextIDEAGeneratorExtensions

	def generate(Grammar it, AntlrOptions options, Xtend2ExecutionContext ctx) {
		ctx.writeFile(ctx.srcGenOutlet.name, grammarFileName.asPath + '.g', compile(options))
	}
	
	protected def getGrammarFileName(Grammar it) {
		getGrammarFileName('Debug')
	}
	
	protected def compile(Grammar it, AntlrOptions options) '''
		/*
		�fileHeader�
		*/
		grammar �grammarFileName.toSimpleName�;
		�compileOptions(options)�
		�compileTokens(options)�
		�compileLexerHeader(options)�
		�compileParserHeader(options)�
		�compileParserMembers(options)�
		�compileRuleCatch(options)�
		�compileRules(options)�
	'''
	
	protected def compileOptions(Grammar it, AntlrOptions options) {
		''
	}
	
	protected def compileTokens(Grammar it, AntlrOptions options) {
		''
	}
	
	protected def compileLexerHeader(Grammar it, AntlrOptions options) '''
		
		@lexer::header {
		package �grammarFileName.toPackageName�;
		�compileLexerImports(options)�
		}
	'''
	
	protected def compileLexerImports(Grammar it, AntlrOptions options) '''
		
		// Hack: Use our own Lexer superclass by means of import. 
		// Currently there is no other way to specify the superclass for the lexer.
		import org.eclipse.xtext.parser.antlr.Lexer;
	'''
	
	protected def compileParserHeader(Grammar it, AntlrOptions options) '''
		
		@parser::header {
		package �grammarFileName.toPackageName�;
		�compileParserImports(options)�
		}
	'''
	
	protected def compileParserImports(Grammar it, AntlrOptions options) {
		''
	}
	
	protected def compileParserMembers(Grammar it, AntlrOptions options) {
		''
	}
	
	protected def compileRuleCatch(Grammar it, AntlrOptions options) {
		''
	}
	
	protected def compileRules(Grammar it, AntlrOptions options) '''
		�FOR rule:allParserRules.filter[isCalled(grammar)]�

			�rule.compileRule(it, options)�
		�ENDFOR�
		�FOR rule:allEnumRules.filter[isCalled(grammar)]�

			�rule.compileRule(it, options)�
		�ENDFOR�
		�FOR rule:allTerminalRules�

			�rule.compileRule(it, options)�
		�ENDFOR�
	'''
	
	protected dispatch def compileRule(EnumRule it, Grammar grammar, AntlrOptions options) {
		compileEBNF(options)
	}
	
	protected dispatch def compileRule(ParserRule it, Grammar grammar, AntlrOptions options) {
		compileEBNF(options)
	}
	
	protected dispatch def compileRule(TerminalRule it, Grammar grammar, AntlrOptions options) '''
		�IF fragment�fragment �ENDIF��ruleName� : �toLexerBody��IF shouldBeSkipped(grammar)� {skip();}�ENDIF�;'''
		
	protected def shouldBeSkipped(TerminalRule it, Grammar grammar) {
		grammar.initialHiddenTokens.contains(ruleName)
	}
	
	protected def String compileEBNF(AbstractRule it, AntlrOptions options) '''
		// Rule �name�
		�ruleName��compileInit(options)�:
			�IF it instanceof ParserRule && datatypeRule�
				�dataTypeEbnf(alternatives, true)�
			�ELSE�
				�ebnf(alternatives, options, true)�
			�ENDIF�
		;
		�compileFinally(options)�
	'''
		
	protected def compileInit(AbstractRule it, AntlrOptions options) {
		''
	}
		
	protected def compileFinally(AbstractRule it, AntlrOptions options) {
		''
	}
		
	protected def String ebnf(AbstractElement it, AntlrOptions options, boolean supportActions) '''
		�IF mustBeParenthesized�(
			�ebnfPredicate(options)��ebnf2(options, supportActions)�
		)�ELSE��ebnf2(options, supportActions)��ENDIF��cardinality�
	'''
	
	protected def String ebnfPredicate(AbstractElement it, AntlrOptions options) '''
		�IF predicated() || firstSetPredicated�(�IF predicated()��predicatedElement.ebnf2(options, false)��ELSE��FOR e:firstSet SEPARATOR ' | '��e.ebnf2(options, false)��ENDFOR��ENDIF�)=>�ENDIF�
	'''
	
	protected def String dataTypeEbnf(AbstractElement it, boolean supportActions) '''
		�IF mustBeParenthesized�(
			�dataTypeEbnfPredicate��dataTypeEbnf2(supportActions)�
		)�ELSE��dataTypeEbnf2(supportActions)��ENDIF��cardinality�
	'''
	
	protected def String dataTypeEbnfPredicate(AbstractElement it) '''
		�IF predicated() || firstSetPredicated�(�IF predicated()��predicatedElement.dataTypeEbnf2(false)��ELSE��FOR e:firstSet SEPARATOR ' | '��e.dataTypeEbnf2(false)��ENDFOR��ENDIF�)=>�ENDIF�
	'''
	
	protected dispatch def String dataTypeEbnf2(AbstractElement it, boolean supportActions) '''ERROR �eClass.name� not matched'''

	protected dispatch def String dataTypeEbnf2(Alternatives it, boolean supportActions) '''
		�FOR e:elements SEPARATOR '\n    |'��e.dataTypeEbnf(supportActions)��ENDFOR�
	'''

	protected dispatch def String dataTypeEbnf2(Group it, boolean supportActions) '''
		�FOR e:elements��e.dataTypeEbnf(supportActions)��ENDFOR�
	'''
	
	protected dispatch def String dataTypeEbnf2(UnorderedGroup it, boolean supportActions) '''
		(�FOR e:elements SEPARATOR '\n    |'��e.dataTypeEbnf2(supportActions)��ENDFOR�)*
	'''
	
	protected dispatch def String dataTypeEbnf2(Keyword it, boolean supportActions) {
		"'" + value.toAntlrString + "'"
	}
	
	protected dispatch def String dataTypeEbnf2(RuleCall it, boolean supportActions) {
		rule.ruleName
	}
	
	protected dispatch def String ebnf2(AbstractElement it, AntlrOptions options, boolean supportActions) '''ERROR �eClass.name� not matched'''
	
	protected dispatch def String ebnf2(Alternatives it, AntlrOptions options, boolean supportActions) '''
		�FOR element:elements SEPARATOR '\n    |'��element.ebnf(options, supportActions)��ENDFOR�
	'''
	
	protected dispatch def String ebnf2(Group it, AntlrOptions options, boolean supportActions) '''
		�FOR element:elements��element.ebnf(options, supportActions)��ENDFOR�
	'''
	
	protected dispatch def String ebnf2(UnorderedGroup it, AntlrOptions options, boolean supportActions) '''
		(�FOR element:elements SEPARATOR '\n    |'��element.ebnf(options, supportActions)��ENDFOR�)*
	'''
	
	protected dispatch def String ebnf2(Assignment it, AntlrOptions options, boolean supportActions) '''
		(
			�terminal.assignmentEbnf(it, options, supportActions)�
		)
	'''
	
	protected dispatch def String ebnf2(Action it, AntlrOptions options, boolean supportActions) {
		''
	}
	
	protected dispatch def String ebnf2(Keyword it, AntlrOptions options, boolean supportActions) {
		"'" + value.toAntlrString + "'"
	}
	
	protected dispatch def String ebnf2(RuleCall it, AntlrOptions options, boolean supportActions) {
		rule.ruleName
	}
	
	protected dispatch def String crossrefEbnf(AbstractElement it, CrossReference ref, boolean supportActions) {
		throw new IllegalStateException("crossrefEbnf is not supported for " + it)
	}
	
	protected dispatch def String crossrefEbnf(Alternatives it, CrossReference ref, boolean supportActions) '''
		�FOR element:elements SEPARATOR '\n    |'��element.crossrefEbnf(ref, supportActions)��ENDFOR�
	'''
	
	protected dispatch def String crossrefEbnf(RuleCall it, CrossReference ref, boolean supportActions) {
		val rule = rule
		if (rule instanceof ParserRule) {
			if (!rule.datatypeRule) {
				throw new IllegalStateException("crossrefEbnf is not supported for ParserRule that is not a datatype rule")
			}
		}
		rule.crossrefEbnf(ref, supportActions)
	}
	
	protected dispatch def String crossrefEbnf(AbstractRule it, CrossReference ref, boolean supportActions) {
		switch it {
			EnumRule,
			ParserRule,
			TerminalRule: 
				ruleName
			default:
				throw new IllegalStateException("crossrefEbnf is not supported for " + it)
		}
	}
	
	protected dispatch def String assignmentEbnf(Group it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		throw new IllegalStateException("assignmentEbnf is not supported for " + it)
	}
	
	protected dispatch def String assignmentEbnf(Assignment it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		throw new IllegalStateException("assignmentEbnf is not supported for " + it)
	}
	
	protected dispatch def String assignmentEbnf(Action it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		throw new IllegalStateException("assignmentEbnf is not supported for " + it)
	}
	
	protected dispatch def String assignmentEbnf(Alternatives it, Assignment assignment, AntlrOptions options, boolean supportActions) '''
		(
			�FOR element:elements SEPARATOR '\n    |'��element.assignmentEbnf(assignment, options, supportActions)��ENDFOR�
		)
	'''
	
	protected dispatch def String assignmentEbnf(RuleCall it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		switch rule : rule {
			EnumRule,
			ParserRule,
			TerminalRule: 
				rule.ruleName
			default: 
				throw new IllegalStateException("assignmentEbnf is not supported for " + rule)
		}
	}
	
	protected dispatch def String assignmentEbnf(CrossReference it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		terminal.crossrefEbnf(it, supportActions)
	}
	
	protected dispatch def String assignmentEbnf(AbstractElement it, Assignment assignment, AntlrOptions options, boolean supportActions) {
		ebnf(options, supportActions)
	}

}