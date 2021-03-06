/*
* generated by Xtext
*/
grammar DebugInternalContextFinderTestLanguage ;

// Rule Model
ruleModel :
	'#0' ruleModel2 |
	'#1' (
		ruleAttributeExclusionTest1 |
		ruleAttributeExclusionTest2
	) |
	'#2' (
		ruleNestedTypeTest1 |
		ruleNestedTypeTest2
	) |
	'#3' (
		ruleNestedTypeRecursiveTest1 |
		ruleNestedTypeRecursiveTest2
	) |
	'#4' (
		ruleParentRefTest1 |
		ruleParentRefTest2
	) |
	'#5' (
		ruleQuantityExclusionTest1 |
		ruleQuantityExclusionTest2
	) |
	'#6' (
		ruleValueExclusionTest1 |
		ruleValueExclusionTest2
	) |
	'#7' RULE_STRING (
		ruleNodeExclusion1 |
		ruleNodeExclusion2
	) |
	'#8' RULE_STRING (
		ruleNodeExclusion1List |
		ruleNodeExclusion2List
	)
;

// Rule Model2
ruleModel2 :
	'model'
;

// Rule AttributeExclusionTest1
ruleAttributeExclusionTest1 :
	'kw1' RULE_ID
;

// Rule AttributeExclusionTest2
ruleAttributeExclusionTest2 :
	'kw2' RULE_ID
;

// Rule NestedTypeTest1
ruleNestedTypeTest1 :
	ruleNestedTypeChild1
;

// Rule NestedTypeTest2
ruleNestedTypeTest2 :
	ruleNestedTypeChild2
;

// Rule NestedTypeChild1
ruleNestedTypeChild1 :
	'kw1' RULE_ID
;

// Rule NestedTypeChild2
ruleNestedTypeChild2 :
	'kw2' RULE_ID
;

// Rule NestedTypeRecursiveTest1
ruleNestedTypeRecursiveTest1 :
	'kw1' '.'+
;

// Rule NestedTypeRecursiveTest2
ruleNestedTypeRecursiveTest2 :
	'kw2' '.'+
;

// Rule ParentRefTest1
ruleParentRefTest1 :
	'kw1' ruleParentRefTestChild1
;

// Rule ParentRefTest2
ruleParentRefTest2 :
	'kw2' ruleParentRefTestChild2
;

// Rule ParentRefTestChild1
ruleParentRefTestChild1 :
	'foo' ruleParentRefTestChild1?
;

// Rule ParentRefTestChild2
ruleParentRefTestChild2 :
	'foo' ruleParentRefTestChild2?
;

// Rule QuantityExclusionTest1
ruleQuantityExclusionTest1 :
	'kw1' RULE_ID RULE_ID?
;

// Rule QuantityExclusionTest2
ruleQuantityExclusionTest2 :
	'kw2' RULE_ID? RULE_ID
;

// Rule ValueExclusionTest1
ruleValueExclusionTest1 :
	ruleValueExclusionTestEn1
;

// Rule ValueExclusionTest2
ruleValueExclusionTest2 :
	ruleValueExclusionTestEn2
;

// Rule NodeExclusion1
ruleNodeExclusion1 :
	RULE_ID
;

// Rule NodeExclusion2
ruleNodeExclusion2 :
	RULE_STRING
;

// Rule NodeExclusion1List
ruleNodeExclusion1List :
	RULE_ID+
;

// Rule NodeExclusion2List
ruleNodeExclusion2List :
	RULE_STRING+
;

// Rule ValueExclusionTestEn1
ruleValueExclusionTestEn1 :
	'lit1'
;

// Rule ValueExclusionTestEn2
ruleValueExclusionTestEn2 :
	'lit2'
;

RULE_ID :
	'^'? (
		'a' .. 'z' |
		'A' .. 'Z' |
		'_'
	) (
		'a' .. 'z' |
		'A' .. 'Z' |
		'_' |
		'0' .. '9'
	)*
;

RULE_INT :
	'0' .. '9'+
;

RULE_STRING :
	'"' (
		'\\' . |
		~ (
			'\\' |
			'"'
		)
	)* '"' |
	'\'' (
		'\\' . |
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