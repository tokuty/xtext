package org.eclipse.xtend.macro.lang.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(MacroInjectorProvider))
class ValidationTest {
	
	@Inject extension CompilationTestHelper
	@Inject extension MacroTestExtensions
	
	@Test def void testValidationError() {
		val resourceSet = resourceSet(
		'''
			@MyAnnotation class Foo {}
		'''.xtend, 
		'''
			@MyAnnotation for class {
				foo // compile error
			}
		'''.macro)
		EcoreUtil::resolveAll(resourceSet)
		assertTrue( resourceSet.resources.findFirst[ URI.fileExtension == 'xtend'].errors.head.message.contains('macro'))
		assertFalse( resourceSet.resources.findFirst[ URI.fileExtension == 'macro'].errors.empty)
	}
	
	@Test def void testValidationError_01() {
		val resourceSet = resourceSet(
		'''
			@MyAnnotation class Foo {}
		'''.xtend, 
		'''
			@MyAnnotation for class {
				process {
					throw new NullPointerException("name : "+ it.head.name)
				}
			}
		'''.macro)
		EcoreUtil::resolveAll(resourceSet)
		val message = resourceSet.resources.findFirst[ URI.fileExtension == 'xtend'].errors.head.message
		assertTrue(message.contains('name : Foo'))
		assertTrue(message.contains('throw new NullPointerException'))
		assertTrue( resourceSet.resources.findFirst[ URI.fileExtension == 'macro'].errors.empty)
	}
	
	@Test def void testValidationError_02() {
		val resourceSet = resourceSet(
		'''
			@MyAnnotation class Foo {}
		'''.xtend, 
		'''
			@MyAnnotation for method {
				process {
				}
			}
		'''.macro)
		EcoreUtil::resolveAll(resourceSet)
		val message = resourceSet.resources.findFirst[ URI.fileExtension == 'xtend'].errors.head.message
		assertTrue(message.contains('MyAnnotation'))
		assertTrue(message.contains('method'))
		assertTrue( resourceSet.resources.findFirst[ URI.fileExtension == 'macro'].errors.empty)
	}
	
	@Test def void testValidationError_03() {
		val resourceSet = resourceSet(
		'''
			@MyAnnotation class Foo {}
		'''.xtend, 
		'''
			@MyAnnotation for class {
				process each {
					error( it , 'Bad name '+ name)
				}
			}
		'''.macro)
		EcoreUtil::resolveAll(resourceSet)
		val message = resourceSet.resources.findFirst[ URI.fileExtension == 'xtend'].errors.head.message
		assertTrue(message, message.contains('Bad name Foo'))
		assertTrue( resourceSet.resources.findFirst[ URI.fileExtension == 'macro'].errors.empty)
	}
}