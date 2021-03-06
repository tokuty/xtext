package org.eclipse.xtend.ide.tests.compiler

import com.google.inject.Inject
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IMarker
import org.eclipse.core.resources.IResource
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.xtend.ide.tests.AbstractXtendUITestCase
import org.eclipse.xtend.ide.tests.WorkbenchTestHelper
import org.eclipse.xtext.junit4.internal.StopwatchRule
import org.eclipse.xtext.util.StringInputStream
import org.junit.After
import org.junit.Rule
import org.junit.Test

import static org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.*

import static extension org.eclipse.ui.texteditor.MarkerUtilities.*
import org.eclipse.xtext.junit4.ui.util.JavaProjectSetupUtil
import org.eclipse.jdt.core.JavaCore

class RebuildAffectedResourcesTest extends AbstractXtendUITestCase {

	@Inject 
	private WorkbenchTestHelper workbenchTestHelper;
	
	@Rule public StopwatchRule rule = new StopwatchRule(true)
	
	@After override void tearDown() {
		workbenchTestHelper.project.delete(true, null)
	}
	
	@Test def void testRemoveReferencedType() {
		val type1 = workbenchTestHelper.createFile('Type1.xtend', '''
			class Type1 {
			}
		''')
		val type2 = workbenchTestHelper.createFile('Type2.xtend', '''
			class Type2 {
				Type1 myRef
			}
		''')
		waitForAutoBuild()
		assertNoErrorsInWorkspace
		type1.setContents(new StringInputStream('''
			class Type3 {
			}
		'''),true,true, null)
		waitForAutoBuild()
		type2.assertHasErrors("Type1")
	}
	
	@Test def void testAddReferencedType() {
		val type1 = workbenchTestHelper.createFile('Type1.xtend', '''
			class Type3 {
			}
		''')
		val type2 = workbenchTestHelper.createFile('Type2.xtend', '''
			class Type2 {
				Type1 myRef
			}
		''')
		waitForAutoBuild
		type2.assertHasErrors("Type1")
		type1.setContents(new StringInputStream('''
			class Type1 {
			}
		'''),true,true, null)
		waitForAutoBuild()
		assertNoErrorsInWorkspace
	}
	
	@Test def void testAddMethod() {
		val type3 = workbenchTestHelper.createFile('Type3.xtend', '''
			class Type3 {
			}
		''')
		workbenchTestHelper.createFile('Type1.xtend', '''
			class Type1 {
				def Type3 foo() { return new Type3()}
			}
		''')
		val type2 = workbenchTestHelper.createFile('Type2.xtend', '''
			class Type2 {
				def void doStuff() {
					new Type1().foo().bar()
				}
			}
		''')
		waitForAutoBuild()
		type2.assertHasErrors("bar")
		type3.setContents(new StringInputStream('''
			class Type3 {
				def void bar() {}
			}
		'''),true,true, null)
		waitForAutoBuild()
		assertNoErrorsInWorkspace
	}
	
	@Test def void testExtendedClassChanges() {
		val type3 = workbenchTestHelper.createFile('Type3.xtend', '''
			class Type3 {
			}
		''')
		workbenchTestHelper.createFile('Type1.xtend', '''
			class Type1 extends Type3 {
			}
		''')
		val type2 = workbenchTestHelper.createFile('Type2.xtend', '''
			class Type2 {
				def void doStuff(Type1 foo) {
					foo.bar
				}
			}
		''')
		waitForAutoBuild()
		type2.assertHasErrors("bar")
		type3.setContents(new StringInputStream('''
			class Type3 {
				def void bar() {}
			}
		'''),true,true, null)
		waitForAutoBuild()
		assertNoErrorsInWorkspace
		
		type3.setContents(new StringInputStream('''
			class Type3 {
				def void baz() {}
			}
		'''),true,true, null)
		waitForAutoBuild()
		type2.assertHasErrors("bar")
	}
	
	/**
	 * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=388828
	 */
	@Test def void testBug388828() {
		val typeA = workbenchTestHelper.createFile('A.xtend', '''
			abstract class A {}
		''')
		val typeB = workbenchTestHelper.createFile('B.xtend', '''
			class B extends A {
			}
		''')
		waitForAutoBuild()
		assertNoErrorsInWorkspace
		typeA.setContents(new StringInputStream('''
			abst ract class A {
			}
		'''),true,true, null)
		waitForAutoBuild()
		typeA.assertHasErrors('abst')
		typeB.assertHasErrors('A')
		
		typeA.setContents(new StringInputStream('''
			abstract class A {}
		'''),true,true, null)
		waitForAutoBuild()
		assertNoErrorsInWorkspace
	}
	
	@Test
	def void testChangeInAnnotationProcessor() {
		val macroProject = JavaCore.create(workbenchTestHelper.project)
		workbenchTestHelper.createFile('anno/Anno.xtend', '''
			package anno
			import org.eclipse.xtend.lib.macro.Active
			
			@Active(AnnoProcessor)
			annotation Anno {}
		''')
		val processorClass = workbenchTestHelper.createFile('anno/AnnoProcessor.xtend', '''
			package anno
			import org.eclipse.xtend.lib.macro.AbstractFieldProcessor
			import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
			import org.eclipse.xtend.lib.macro.TransformationContext
			
			class AnnoProcessor extends AbstractFieldProcessor {
				override doTransform(MutableFieldDeclaration it, extension TransformationContext context) {
				}
			}
		''')
		WorkbenchTestHelper.addExportedPackages(macroProject.project, "anno")
		
		val clientProject = JavaCore.create(WorkbenchTestHelper.createPluginProject(WorkbenchTestHelper.TESTPROJECT_NAME + "-client"))
		JavaProjectSetupUtil.addProjectReference(clientProject, macroProject)
		val clientClass = workbenchTestHelper.createFileImpl(clientProject.project.fullPath.toString + "/src/" + 'Foo.xtend', '''
			import anno.Anno
			class Foo {
				@Anno String foo
				
				def bar() {
					foo - 3
				}
			}
		''')
		waitForAutoBuild
		clientClass.assertHasErrors("- cannot be resolved")
		
		processorClass.setContents(new StringInputStream('''
			package anno
			import org.eclipse.xtend.lib.macro.AbstractFieldProcessor
			import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
			import org.eclipse.xtend.lib.macro.TransformationContext
			
			class AnnoProcessor extends AbstractFieldProcessor {
				override doTransform(MutableFieldDeclaration it, extension TransformationContext context) {
					type = primitiveInt
				}
			}
		'''),true,true, null)
		waitForAutoBuild
		assertNoErrorsInWorkspace
		WorkbenchTestHelper.deleteProject(clientProject.project)
	}
	
	@Test
	def void testChangeInResourceReadFromAnnotationProcessor() {
		val macroProject = JavaCore.create(workbenchTestHelper.project)
		workbenchTestHelper.createFile('anno/Anno.xtend', '''
			package anno
			import com.google.common.base.Splitter
			import java.util.regex.Pattern
			import org.eclipse.xtend.lib.macro.AbstractClassProcessor
			import org.eclipse.xtend.lib.macro.Active
			import org.eclipse.xtend.lib.macro.TransformationContext
			import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
			
			@Active(AnnoProcessor)
			annotation Anno {
			}
			
			class AnnoProcessor extends AbstractClassProcessor {
				override doTransform(MutableClassDeclaration cls, extension TransformationContext context) {
					val content = context.getContents(cls.compilationUnit.filePath.parent.append("constants.txt"))
					val constants = Splitter.on(Pattern.compile("\\r?\\n")).split(content)
					constants.forEach [ constant |
						cls.addField(constant) [
							static = true
							final = true
							initializer = ['"' + constant + '"']
							type = string
						]
					]
				}
			}
		''')
		WorkbenchTestHelper.addExportedPackages(macroProject.project, "anno")
		
		val clientProject = JavaCore.create(WorkbenchTestHelper.createPluginProject(WorkbenchTestHelper.TESTPROJECT_NAME + "-client"))
		JavaProjectSetupUtil.addProjectReference(clientProject, macroProject)
		val constants = workbenchTestHelper.createFileImpl(clientProject.project.fullPath.toString + "/src/" + 'constants.txt', '''
			A
			B''')
		val fooClass = workbenchTestHelper.createFileImpl(clientProject.project.fullPath.toString + "/src/" + 'Foo.xtend', '''
			import anno.Anno
			@Anno
			class Foo {
				def test() {
					C
				}
			}
		''')
		waitForAutoBuild
		fooClass.assertHasErrors("C is undefined")
		
		constants.setContents(new StringInputStream('''
			A
			B
			C'''),true,true, null)
		waitForAutoBuild
		assertNoErrorsInWorkspace
		WorkbenchTestHelper.deleteProject(clientProject.project)
	}
	
	@Test
	def void testChangeJavaClassLookedUpInAnnotationProcessor() {
		val macroProject = JavaCore.create(workbenchTestHelper.project)
		workbenchTestHelper.createFile('anno/Anno.xtend', '''
			package anno
			import org.eclipse.xtend.lib.macro.Active
			import org.eclipse.xtend.lib.macro.AbstractClassProcessor
			import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
			import org.eclipse.xtend.lib.macro.TransformationContext
			
			@Active(AnnoProcessor)
			annotation Anno {}
			
			class AnnoProcessor extends AbstractClassProcessor {
				override doTransform(MutableClassDeclaration cls, extension TransformationContext context) {
					val theType = context.findTypeGlobally("anno.A")?:context.findTypeGlobally("anno.B")
					cls.addField("foo") [
						type = theType.newTypeReference
					]
				}
			}
		''')
		workbenchTestHelper.createFile('anno/B.xtend', '''
			package anno
			class B {}
		''')
		WorkbenchTestHelper.addExportedPackages(macroProject.project, "anno")
		
		val clientProject = JavaCore.create(WorkbenchTestHelper.createPluginProject(WorkbenchTestHelper.TESTPROJECT_NAME + "-client"))
		JavaProjectSetupUtil.addProjectReference(clientProject, macroProject)
		val fooClass = workbenchTestHelper.createFileImpl(clientProject.project.fullPath.toString + "/src/" + 'Foo.xtend', '''
			import anno.Anno
			@Anno
			class Foo {
				def test() {
					foo.bar
				}
			}
		''')
		waitForAutoBuild
		fooClass.assertHasErrors("bar is undefined")
		
		workbenchTestHelper.createFile('anno/A.xtend', '''
			package anno
			class A {
				def bar() {}
			}
		''')
		waitForAutoBuild
		assertNoErrorsInWorkspace
		WorkbenchTestHelper.deleteProject(clientProject.project)
	}
	
	
	def void assertNoErrorsInWorkspace() {
		val findMarkers = ResourcesPlugin::workspace.root.findMarkers(IMarker::PROBLEM, true, IResource::DEPTH_INFINITE)
		for (iMarker : findMarkers) {
			assertFalse(iMarker.message, iMarker.severity == IMarker::SEVERITY_ERROR)
		}
	}
	
	def void assertHasErrors(IFile file, String msgPart) {
		val findMarkers = file.findMarkers(IMarker::PROBLEM, true, IResource::DEPTH_INFINITE)
		for (iMarker : findMarkers) {
			if (iMarker.severity == IMarker::SEVERITY_ERROR && iMarker.message.contains(msgPart)) {
				return;
			}
		}
		fail("Expected an error marker containing '"+msgPart+"' on "+file.fullPath+" but found "+findMarkers.map[message].join(','))
	}
	
}