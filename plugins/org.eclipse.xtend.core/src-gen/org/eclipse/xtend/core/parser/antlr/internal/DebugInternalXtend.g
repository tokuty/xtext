/*
* generated by Xtext
*/
grammar DebugInternalXtend ;

// Rule File
ruleFile :
	(
		'package' ruleQualifiedName ';'?
	)? ruleXImportSection? ruleType*
;

// Rule Type
ruleType :
	ruleXAnnotation* (
		'public'? 'abstract'? 'class' ruleValidID (
			'<' ruleJvmTypeParameter (
				',' ruleJvmTypeParameter
			)* '>'
		)? (
			'extends' ruleJvmParameterizedTypeReference
		)? (
			'implements' ruleJvmParameterizedTypeReference (
				',' ruleJvmParameterizedTypeReference
			)*
		)? '{' ruleMember* '}' |
		'annotation' ruleValidID '{' ruleAnnotationField* '}'
	)
;

// Rule AnnotationField
ruleAnnotationField :
	ruleXAnnotation* (
		ruleJvmTypeReference |
		(
			'val' |
			'var'
		) ruleJvmTypeReference?
	) ruleValidID (
		'=' ruleXExpression
	)? ';'?
;

// Rule Member
ruleMember :
	ruleXAnnotation* (
		ruleVisibility? (
			'extension' (
				'val' |
				'var'
			)? ruleJvmTypeReference ruleValidID? |
			'static'? (
				ruleJvmTypeReference |
				(
					'val' |
					'var'
				) ruleJvmTypeReference?
			) ruleValidID
		) (
			'=' ruleXExpression
		)? ';'? |
		(
			'def' |
			'override'
		) ruleVisibility? 'static'? 'dispatch'? (
			'<' ruleJvmTypeParameter (
				',' ruleJvmTypeParameter
			)* '>'
		)? (
			( (
			ruleJvmTypeReference ruleCreateExtensionInfo ruleValidID '('
			) => (
				ruleJvmTypeReference ruleCreateExtensionInfo ruleValidID '('
			) ) |
			( (
			ruleJvmTypeReference ruleValidID '('
			) => (
				ruleJvmTypeReference ruleValidID '('
			) ) |
			( (
			ruleCreateExtensionInfo ruleValidID '('
			) => (
				ruleCreateExtensionInfo ruleValidID '('
			) ) |
			ruleValidID '('
		) (
			ruleParameter (
				',' ruleParameter
			)*
		)? ')' (
			'throws' ruleJvmTypeReference (
				',' ruleJvmTypeReference
			)*
		)? (
			ruleXBlockExpression |
			ruleRichString
		)? |
		ruleVisibility? 'new' (
			'<' ruleJvmTypeParameter (
				',' ruleJvmTypeParameter
			)* '>'
		)? '(' (
			ruleParameter (
				',' ruleParameter
			)*
		)? ')' (
			'throws' ruleJvmTypeReference (
				',' ruleJvmTypeReference
			)*
		)? ruleXBlockExpression
	)
;

// Rule CreateExtensionInfo
ruleCreateExtensionInfo :
	'create' (
		ruleValidID ':'
	)? ruleXExpression
;

// Rule ValidID
ruleValidID :
	RULE_ID |
	'create' |
	'annotation'
;

// Rule Parameter
ruleParameter :
	ruleXAnnotation* ruleJvmTypeReference '...'? ruleValidID
;

// Rule XStringLiteral
ruleXStringLiteral :
	ruleSimpleStringLiteral |
	ruleRichString
;

// Rule SimpleStringLiteral
ruleSimpleStringLiteral :
	RULE_STRING
;

// Rule RichString
ruleRichString :
	ruleRichStringLiteral |
	ruleRichStringLiteralStart ruleRichStringPart? (
		ruleRichStringLiteralInbetween ruleRichStringPart?
	)* ruleRichStringLiteralEnd
;

// Rule RichStringLiteral
ruleRichStringLiteral :
	RULE_RICH_TEXT
;

// Rule RichStringLiteralStart
ruleRichStringLiteralStart :
	RULE_RICH_TEXT_START
;

// Rule RichStringLiteralInbetween
ruleRichStringLiteralInbetween :
	RULE_RICH_TEXT_INBETWEEN |
	RULE_COMMENT_RICH_TEXT_INBETWEEN
;

// Rule RichStringLiteralEnd
ruleRichStringLiteralEnd :
	RULE_RICH_TEXT_END |
	RULE_COMMENT_RICH_TEXT_END
;

// Rule InternalRichString
ruleInternalRichString :
	ruleRichStringLiteralInbetween (
		ruleRichStringPart? ruleRichStringLiteralInbetween
	)*
;

// Rule RichStringPart
ruleRichStringPart :
	ruleXExpressionInsideBlock |
	ruleRichStringForLoop |
	ruleRichStringIf
;

// Rule RichStringForLoop
ruleRichStringForLoop :
	'FOR' ruleJvmFormalParameter ':' ruleXExpression (
		'BEFORE' ruleXExpression
	)? (
		'SEPARATOR' ruleXExpression
	)? (
		'AFTER' ruleXExpression
	)? ruleInternalRichString 'ENDFOR'
;

// Rule RichStringIf
ruleRichStringIf :
	'IF' ruleXExpression ruleInternalRichString ruleRichStringElseIf* (
		'ELSE' ruleInternalRichString
	)? 'ENDIF'
;

// Rule RichStringElseIf
ruleRichStringElseIf :
	'ELSEIF' ruleXExpression ruleInternalRichString
;

// Rule XAnnotation
ruleXAnnotation :
	'@' ruleQualifiedName (
		( (
		'('
		) => '(' ) (
			ruleXAnnotationElementValuePair (
				',' ruleXAnnotationElementValuePair
			)* |
			ruleXAnnotationElementValue
		)? ')'
	)?
;

// Rule XAnnotationElementValuePair
ruleXAnnotationElementValuePair :
	ruleValidID '=' ruleXAnnotationElementValue
;

// Rule XAnnotationElementValueStringConcatenation
ruleXAnnotationElementValueStringConcatenation :
	ruleXAnnotationElementValue (
		'+' ruleXAnnotationElementValue
	)*
;

// Rule XAnnotationElementValue
ruleXAnnotationElementValue :
	ruleXAnnotation |
	ruleXAnnotationValueArray |
	ruleXStringLiteral |
	ruleXBooleanLiteral |
	ruleXNumberLiteral |
	ruleXTypeLiteral |
	ruleXAnnotationValueFieldReference |
	'(' ruleXAnnotationElementValueStringConcatenation ')'
;

// Rule XAnnotationValueFieldReference
ruleXAnnotationValueFieldReference :
	ruleStaticQualifier? ruleIdOrSuper
;

// Rule XAnnotationValueArray
ruleXAnnotationValueArray :
	'{' ruleXAnnotationElementValue (
		',' ruleXAnnotationElementValue
	)* '}'
;

// Rule XExpression
ruleXExpression :
	ruleXAssignment
;

// Rule XAssignment
ruleXAssignment :
	ruleValidID ruleOpSingleAssign ruleXAssignment |
	ruleXOrExpression (
		( (
		ruleOpMultiAssign
		) => ruleOpMultiAssign ) ruleXAssignment
	)?
;

// Rule OpSingleAssign
ruleOpSingleAssign :
	'='
;

// Rule OpMultiAssign
ruleOpMultiAssign :
	'+='
;

// Rule XOrExpression
ruleXOrExpression :
	ruleXAndExpression (
		( (
		ruleOpOr
		) => ruleOpOr ) ruleXAndExpression
	)*
;

// Rule OpOr
ruleOpOr :
	'||'
;

// Rule XAndExpression
ruleXAndExpression :
	ruleXEqualityExpression (
		( (
		ruleOpAnd
		) => ruleOpAnd ) ruleXEqualityExpression
	)*
;

// Rule OpAnd
ruleOpAnd :
	'&&'
;

// Rule XEqualityExpression
ruleXEqualityExpression :
	ruleXRelationalExpression (
		( (
		ruleOpEquality
		) => ruleOpEquality ) ruleXRelationalExpression
	)*
;

// Rule OpEquality
ruleOpEquality :
	'==' |
	'!='
;

// Rule XRelationalExpression
ruleXRelationalExpression :
	ruleXOtherOperatorExpression (
		( (
		'instanceof'
		) => 'instanceof' ) ruleJvmTypeReference |
		( (
		ruleOpCompare
		) => ruleOpCompare ) ruleXOtherOperatorExpression
	)*
;

// Rule OpCompare
ruleOpCompare :
	'>=' |
	'<=' |
	'>' |
	'<'
;

// Rule XOtherOperatorExpression
ruleXOtherOperatorExpression :
	ruleXAdditiveExpression (
		( (
		ruleOpOther
		) => ruleOpOther ) ruleXAdditiveExpression
	)*
;

// Rule OpOther
ruleOpOther :
	'->' |
	'..' |
	'=>' |
	'>' (
		( (
		'>' '>'
		) => (
			'>' '>'
		) ) |
		'>'
	) |
	'<' (
		( (
		'<' '<'
		) => (
			'<' '<'
		) ) |
		'<'
	) |
	'<>' |
	'?:' |
	'<=>'
;

// Rule XAdditiveExpression
ruleXAdditiveExpression :
	ruleXMultiplicativeExpression (
		( (
		ruleOpAdd
		) => ruleOpAdd ) ruleXMultiplicativeExpression
	)*
;

// Rule OpAdd
ruleOpAdd :
	'+' |
	'-'
;

// Rule XMultiplicativeExpression
ruleXMultiplicativeExpression :
	ruleXUnaryOperation (
		( (
		ruleOpMulti
		) => ruleOpMulti ) ruleXUnaryOperation
	)*
;

// Rule OpMulti
ruleOpMulti :
	'*' |
	'**' |
	'/' |
	'%'
;

// Rule XUnaryOperation
ruleXUnaryOperation :
	ruleOpUnary ruleXUnaryOperation |
	ruleXCastedExpression
;

// Rule OpUnary
ruleOpUnary :
	'!' |
	'-' |
	'+'
;

// Rule XCastedExpression
ruleXCastedExpression :
	ruleXMemberFeatureCall (
		( (
		'as'
		) => 'as' ) ruleJvmTypeReference
	)*
;

// Rule XMemberFeatureCall
ruleXMemberFeatureCall :
	ruleXPrimaryExpression (
		( (
		'.' ruleValidID ruleOpSingleAssign
		) => (
			'.' ruleValidID ruleOpSingleAssign
		) ) ruleXAssignment |
		( (
		'.' |
		'?.' |
		'*.'
		) => (
			'.' |
			'?.' |
			'*.'
		) ) (
			'<' ruleJvmArgumentTypeReference (
				',' ruleJvmArgumentTypeReference
			)* '>'
		)? ruleValidID (
			( (
			'('
			) => '(' ) (
				( (
				(
					ruleJvmFormalParameter (
						',' ruleJvmFormalParameter
					)*
				)? '|'
				) => ruleXShortClosure ) |
				ruleXExpression (
					',' ruleXExpression
				)*
			)? ')'
		)? ( (
		'['
		) => ruleXClosure )?
	)*
;

// Rule XPrimaryExpression
ruleXPrimaryExpression :
	ruleXConstructorCall |
	ruleXBlockExpression |
	ruleXSwitchExpression |
	ruleXFeatureCall |
	ruleXLiteral |
	ruleXIfExpression |
	ruleXForLoopExpression |
	ruleXWhileExpression |
	ruleXDoWhileExpression |
	ruleXThrowExpression |
	ruleXReturnExpression |
	ruleXTryCatchFinallyExpression |
	ruleXParenthesizedExpression
;

// Rule XLiteral
ruleXLiteral :
	( (
	'['
	) => ruleXClosure ) |
	ruleXBooleanLiteral |
	ruleXNumberLiteral |
	ruleXNullLiteral |
	ruleXStringLiteral |
	ruleXTypeLiteral
;

// Rule XClosure
ruleXClosure :
	( (
	'['
	) => '[' ) ( (
	(
		ruleJvmFormalParameter (
			',' ruleJvmFormalParameter
		)*
	)? '|'
	) => (
		(
			ruleJvmFormalParameter (
				',' ruleJvmFormalParameter
			)*
		)? '|'
	) )? ruleXExpressionInClosure ']'
;

// Rule XExpressionInClosure
ruleXExpressionInClosure :
	(
		ruleXExpressionInsideBlock ';'?
	)*
;

// Rule XShortClosure
ruleXShortClosure :
	( (
	(
		ruleJvmFormalParameter (
			',' ruleJvmFormalParameter
		)*
	)? '|'
	) => (
		(
			ruleJvmFormalParameter (
				',' ruleJvmFormalParameter
			)*
		)? '|'
	) ) ruleXExpression
;

// Rule XParenthesizedExpression
ruleXParenthesizedExpression :
	'(' ruleXExpression ')'
;

// Rule XIfExpression
ruleXIfExpression :
	'if' '(' ruleXExpression ')' ruleXExpression (
		( (
		'else'
		) => 'else' ) ruleXExpression
	)?
;

// Rule XSwitchExpression
ruleXSwitchExpression :
	'switch' (
		( (
		ruleValidID ':'
		) => (
			ruleValidID ':'
		) )? ruleXExpression |
		( (
		'(' ruleValidID ':'
		) => (
			'(' ruleValidID ':'
		) ) ruleXExpression ')'
	) '{' ruleXCasePart+ (
		'default' ':' ruleXExpression
	)? '}'
;

// Rule XCasePart
ruleXCasePart :
	ruleJvmTypeReference? (
		'case' ruleXExpression
	)? ':' ruleXExpression
;

// Rule XForLoopExpression
ruleXForLoopExpression :
	'for' '(' ruleJvmFormalParameter ':' ruleXExpression ')' ruleXExpression
;

// Rule XWhileExpression
ruleXWhileExpression :
	'while' '(' ruleXExpression ')' ruleXExpression
;

// Rule XDoWhileExpression
ruleXDoWhileExpression :
	'do' ruleXExpression 'while' '(' ruleXExpression ')'
;

// Rule XBlockExpression
ruleXBlockExpression :
	'{' (
		ruleXExpressionInsideBlock ';'?
	)* '}'
;

// Rule XExpressionInsideBlock
ruleXExpressionInsideBlock :
	ruleXVariableDeclaration |
	ruleXExpression
;

// Rule XVariableDeclaration
ruleXVariableDeclaration :
	(
		'var' |
		'val'
	) (
		( (
		ruleJvmTypeReference ruleValidID
		) => (
			ruleJvmTypeReference ruleValidID
		) ) |
		ruleValidID
	) (
		'=' ruleXExpression
	)?
;

// Rule JvmFormalParameter
ruleJvmFormalParameter :
	ruleJvmTypeReference? ruleValidID
;

// Rule FullJvmFormalParameter
ruleFullJvmFormalParameter :
	ruleJvmTypeReference ruleValidID
;

// Rule XFeatureCall
ruleXFeatureCall :
	ruleStaticQualifier? (
		'<' ruleJvmArgumentTypeReference (
			',' ruleJvmArgumentTypeReference
		)* '>'
	)? ruleIdOrSuper (
		( (
		'('
		) => '(' ) (
			( (
			(
				ruleJvmFormalParameter (
					',' ruleJvmFormalParameter
				)*
			)? '|'
			) => ruleXShortClosure ) |
			ruleXExpression (
				',' ruleXExpression
			)*
		)? ')'
	)? ( (
	'['
	) => ruleXClosure )?
;

// Rule IdOrSuper
ruleIdOrSuper :
	ruleValidID |
	'super'
;

// Rule StaticQualifier
ruleStaticQualifier :
	(
		ruleValidID '::'
	)+
;

// Rule XConstructorCall
ruleXConstructorCall :
	'new' ruleQualifiedName (
		( (
		'<'
		) => '<' ) ruleJvmArgumentTypeReference (
			',' ruleJvmArgumentTypeReference
		)* '>'
	)? (
		( (
		'('
		) => '(' ) (
			( (
			(
				ruleJvmFormalParameter (
					',' ruleJvmFormalParameter
				)*
			)? '|'
			) => ruleXShortClosure ) |
			ruleXExpression (
				',' ruleXExpression
			)*
		)? ')'
	)? ( (
	'['
	) => ruleXClosure )?
;

// Rule XBooleanLiteral
ruleXBooleanLiteral :
	'false' |
	'true'
;

// Rule XNullLiteral
ruleXNullLiteral :
	'null'
;

// Rule XNumberLiteral
ruleXNumberLiteral :
	ruleNumber
;

// Rule XTypeLiteral
ruleXTypeLiteral :
	'typeof' '(' ruleQualifiedName ruleArrayBrackets* ')'
;

// Rule XThrowExpression
ruleXThrowExpression :
	'throw' ruleXExpression
;

// Rule XReturnExpression
ruleXReturnExpression :
	'return' ( (
	ruleXExpression
	) => ruleXExpression )?
;

// Rule XTryCatchFinallyExpression
ruleXTryCatchFinallyExpression :
	'try' ruleXExpression (
		( (
		'catch'
		) => ruleXCatchClause )+ (
			( (
			'finally'
			) => 'finally' ) ruleXExpression
		)? |
		'finally' ruleXExpression
	)
;

// Rule XCatchClause
ruleXCatchClause :
	( (
	'catch'
	) => 'catch' ) '(' ruleFullJvmFormalParameter ')' ruleXExpression
;

// Rule QualifiedName
ruleQualifiedName :
	ruleValidID (
		( (
		'.'
		) => '.' ) ruleValidID
	)*
;

// Rule Number
ruleNumber :
	RULE_HEX |
	(
		RULE_INT |
		RULE_DECIMAL
	) (
		'.' (
			RULE_INT |
			RULE_DECIMAL
		)
	)?
;

// Rule JvmTypeReference
ruleJvmTypeReference :
	ruleJvmParameterizedTypeReference ( (
	ruleArrayBrackets
	) => ruleArrayBrackets )* |
	ruleXFunctionTypeRef
;

// Rule ArrayBrackets
ruleArrayBrackets :
	'[' ']'
;

// Rule XFunctionTypeRef
ruleXFunctionTypeRef :
	(
		'(' (
			ruleJvmTypeReference (
				',' ruleJvmTypeReference
			)*
		)? ')'
	)? '=>' ruleJvmTypeReference
;

// Rule JvmParameterizedTypeReference
ruleJvmParameterizedTypeReference :
	ruleQualifiedName (
		( (
		'<'
		) => '<' ) ruleJvmArgumentTypeReference (
			',' ruleJvmArgumentTypeReference
		)* '>'
	)?
;

// Rule JvmArgumentTypeReference
ruleJvmArgumentTypeReference :
	ruleJvmTypeReference |
	ruleJvmWildcardTypeReference
;

// Rule JvmWildcardTypeReference
ruleJvmWildcardTypeReference :
	'?' (
		ruleJvmUpperBound |
		ruleJvmLowerBound
	)?
;

// Rule JvmUpperBound
ruleJvmUpperBound :
	'extends' ruleJvmTypeReference
;

// Rule JvmUpperBoundAnded
ruleJvmUpperBoundAnded :
	'&' ruleJvmTypeReference
;

// Rule JvmLowerBound
ruleJvmLowerBound :
	'super' ruleJvmTypeReference
;

// Rule JvmTypeParameter
ruleJvmTypeParameter :
	ruleValidID (
		ruleJvmUpperBound ruleJvmUpperBoundAnded* |
		ruleJvmLowerBound
	)?
;

// Rule QualifiedNameWithWildCard
ruleQualifiedNameWithWildCard :
	ruleQualifiedName '.' '*'
;

// Rule XImportSection
ruleXImportSection :
	ruleXImportDeclaration+
;

// Rule XImportDeclaration
ruleXImportDeclaration :
	'import' (
		'static' 'extension'? ruleQualifiedName '.' '*' |
		ruleQualifiedName |
		ruleQualifiedNameWithWildCard
	) ';'?
;

// Rule Visibility
ruleVisibility :
	'public' |
	'protected' |
	'private'
;

RULE_RICH_TEXT :
	'\'\'\'' RULE_IN_RICH_STRING* (
		'\'\'\'' |
		(
			'\'' '\''?
		)? EOF
	)
;

RULE_RICH_TEXT_START :
	'\'\'\'' RULE_IN_RICH_STRING* (
		'\'' '\''?
	)? '\u00AB'
;

RULE_RICH_TEXT_END :
	'\u00BB' RULE_IN_RICH_STRING* (
		'\'\'\'' |
		(
			'\'' '\''?
		)? EOF
	)
;

RULE_RICH_TEXT_INBETWEEN :
	'\u00BB' RULE_IN_RICH_STRING* (
		'\'' '\''?
	)? '\u00AB'
;

RULE_COMMENT_RICH_TEXT_INBETWEEN :
	'\u00AB\u00AB' ~ (
		'\n' |
		'\r'
	)* (
		'\r'? '\n' RULE_IN_RICH_STRING* (
			'\'' '\''?
		)? '\u00AB'
	)?
;

RULE_COMMENT_RICH_TEXT_END :
	'\u00AB\u00AB' ~ (
		'\n' |
		'\r'
	)* (
		'\r'? '\n' RULE_IN_RICH_STRING* (
			'\'\'\'' |
			(
				'\'' '\''?
			)? EOF
		) |
		EOF
	)
;

fragment RULE_IN_RICH_STRING :
	'\'\'' ~ (
		'\u00AB' |
		'\''
	) |
	'\'' ~ (
		'\u00AB' |
		'\''
	) |
	~ (
		'\u00AB' |
		'\''
	)
;

RULE_HEX :
	(
		'0x' |
		'0X'
	) (
		'0' .. '9' |
		'a' .. 'f' |
		'A' .. 'F' |
		'_'
	)+ (
		'#' (
			(
				'b' |
				'B'
			) (
				'i' |
				'I'
			) |
			(
				'l' |
				'L'
			)
		)
	)?
;

RULE_INT :
	'0' .. '9' (
		'0' .. '9' |
		'_'
	)*
;

RULE_DECIMAL :
	RULE_INT (
		(
			'e' |
			'E'
		) (
			'+' |
			'-'
		)? RULE_INT
	)? (
		(
			'b' |
			'B'
		) (
			'i' |
			'I' |
			'd' |
			'D'
		) |
		(
			'l' |
			'L' |
			'd' |
			'D' |
			'f' |
			'F'
		)
	)?
;

RULE_ID :
	'^'? (
		'a' .. 'z' |
		'A' .. 'Z' |
		'$' |
		'_'
	) (
		'a' .. 'z' |
		'A' .. 'Z' |
		'$' |
		'_' |
		'0' .. '9'
	)*
;

RULE_STRING :
	'"' (
		'\\' (
			'b' |
			't' |
			'n' |
			'f' |
			'r' |
			'u' |
			'"' |
			'\'' |
			'\\'
		) |
		~ (
			'\\' |
			'"'
		)
	)* '"' |
	'\'' (
		'\\' (
			'b' |
			't' |
			'n' |
			'f' |
			'r' |
			'u' |
			'"' |
			'\'' |
			'\\'
		) |
		~ (
			'\\' |
			'\''
		)
	)* '\''
;

RULE_ML_COMMENT :
	'/*' (
		options { greedy = false ; } : .
	)* '*/' {skip();}
;

RULE_SL_COMMENT :
	'//' ~ (
		'\n' |
		'\r'
	)* (
		'\r'? '\n'
	)? {skip();}
;

RULE_WS :
	(
		' ' |
		'\t' |
		'\r' |
		'\n'
	)+ {skip();}
;

RULE_ANY_OTHER :
	.
;