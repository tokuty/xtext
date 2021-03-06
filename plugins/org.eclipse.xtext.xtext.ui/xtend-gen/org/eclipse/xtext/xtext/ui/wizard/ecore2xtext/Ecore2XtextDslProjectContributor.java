package org.eclipse.xtext.xtext.ui.wizard.ecore2xtext;

import com.google.common.base.Objects;
import java.util.Collection;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.ui.util.IProjectFactoryContributor;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xtext.ui.wizard.ecore2xtext.EPackageInfo;
import org.eclipse.xtext.xtext.ui.wizard.ecore2xtext.Ecore2XtextGrammarCreator;
import org.eclipse.xtext.xtext.ui.wizard.ecore2xtext.Ecore2XtextProjectInfo;
import org.eclipse.xtext.xtext.ui.wizard.project.DefaultProjectFactoryContributor;

/**
 * Contributes a workflow file and the grammar to the new Ecore2Xtext DSL project
 * @author Dennis Huebner - Initial contribution and API
 * @since 2.3
 */
@SuppressWarnings("all")
public class Ecore2XtextDslProjectContributor extends DefaultProjectFactoryContributor {
  private Ecore2XtextProjectInfo projectInfo;
  
  private String modelFolder = "";
  
  public Ecore2XtextDslProjectContributor(final Ecore2XtextProjectInfo projectInfo) {
    this.projectInfo = projectInfo;
  }
  
  public void setModelFolder(final String modelFolder) {
    this.modelFolder = modelFolder;
  }
  
  public void contributeFiles(final IProject project, final IProjectFactoryContributor.IFileCreator creator) {
    this.createWorkflowFile(creator);
    this.createGrammarFile(creator);
  }
  
  public IFile createWorkflowFile(final IProjectFactoryContributor.IFileCreator creator) {
    CharSequence _workflow = this.workflow();
    String _basePackagePath = this.projectInfo.getBasePackagePath();
    String _plus = ((this.modelFolder + "/") + _basePackagePath);
    String _plus_1 = (_plus + "/Generate");
    String _languageNameAbbreviation = this.projectInfo.getLanguageNameAbbreviation();
    String _plus_2 = (_plus_1 + _languageNameAbbreviation);
    String _plus_3 = (_plus_2 + ".mwe2");
    return creator.writeToFile(_workflow, _plus_3);
  }
  
  public IFile createGrammarFile(final IProjectFactoryContributor.IFileCreator creator) {
    Ecore2XtextGrammarCreator _ecore2XtextGrammarCreator = new Ecore2XtextGrammarCreator();
    CharSequence _grammar = _ecore2XtextGrammarCreator.grammar(this.projectInfo);
    String _grammarFilePath = this.projectInfo.getGrammarFilePath();
    String _plus = ((this.modelFolder + "/") + _grammarFilePath);
    return creator.writeToFile(_grammar, _plus);
  }
  
  private CharSequence workflow() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("module ");
    String _basePackagePath = this.projectInfo.getBasePackagePath();
    String _plus = (_basePackagePath + "/");
    String _languageNameAbbreviation = this.projectInfo.getLanguageNameAbbreviation();
    String _plus_1 = (_plus + _languageNameAbbreviation);
    String _replaceAll = _plus_1.replaceAll("/", ".");
    _builder.append(_replaceAll, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import org.eclipse.emf.mwe.utils.*");
    _builder.newLine();
    _builder.append("import org.eclipse.xtext.generator.*");
    _builder.newLine();
    _builder.append("import org.eclipse.xtext.ui.generator.*");
    _builder.newLine();
    _builder.newLine();
    _builder.append("var grammarURI = \"classpath:/");
    String _basePackagePath_1 = this.projectInfo.getBasePackagePath();
    _builder.append(_basePackagePath_1, "");
    _builder.append("/");
    String _languageNameAbbreviation_1 = this.projectInfo.getLanguageNameAbbreviation();
    _builder.append(_languageNameAbbreviation_1, "");
    _builder.append(".xtext\"");
    _builder.newLineIfNotEmpty();
    _builder.append("var fileExtensions = \"");
    String _firstFileExtension = this.projectInfo.getFirstFileExtension();
    _builder.append(_firstFileExtension, "");
    _builder.append("\"");
    _builder.newLineIfNotEmpty();
    _builder.append("var projectName = \"");
    String _projectName = this.projectInfo.getProjectName();
    _builder.append(_projectName, "");
    _builder.append("\"");
    _builder.newLineIfNotEmpty();
    _builder.append("var runtimeProject = \"../${projectName}\"");
    _builder.newLine();
    _builder.append("var generateXtendStub = true");
    _builder.newLine();
    _builder.newLine();
    _builder.append("Workflow {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("bean = StandaloneSetup {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("scanClassPath  = true");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("platformUri = \"${runtimeProject}/..\"");
    _builder.newLine();
    {
      Collection<EPackageInfo> _ePackageInfos = this.projectInfo.getEPackageInfos();
      final Function1<EPackageInfo, Boolean> _function = new Function1<EPackageInfo, Boolean>() {
        public Boolean apply(final EPackageInfo it) {
          URI _genmodelURI = it.getGenmodelURI();
          String _fileExtension = _genmodelURI.fileExtension();
          return Boolean.valueOf((!Objects.equal(_fileExtension, "xcore")));
        }
      };
      Iterable<EPackageInfo> _filter = IterableExtensions.<EPackageInfo>filter(_ePackageInfos, _function);
      final Function1<EPackageInfo, String> _function_1 = new Function1<EPackageInfo, String>() {
        public String apply(final EPackageInfo it) {
          return it.getEPackageJavaFQN();
        }
      };
      Iterable<String> _map = IterableExtensions.<EPackageInfo, String>map(_filter, _function_1);
      Iterable<String> _filterNull = IterableExtensions.<String>filterNull(_map);
      for(final String ePackageInfo : _filterNull) {
        _builder.append("\t\t");
        _builder.append("registerGeneratedEPackage = \"");
        _builder.append(ePackageInfo, "\t\t");
        _builder.append("\"");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      Collection<EPackageInfo> _ePackageInfos_1 = this.projectInfo.getEPackageInfos();
      final Function1<EPackageInfo, Boolean> _function_2 = new Function1<EPackageInfo, Boolean>() {
        public Boolean apply(final EPackageInfo it) {
          URI _genmodelURI = it.getGenmodelURI();
          String _fileExtension = _genmodelURI.fileExtension();
          return Boolean.valueOf((!Objects.equal(_fileExtension, "xcore")));
        }
      };
      Iterable<EPackageInfo> _filter_1 = IterableExtensions.<EPackageInfo>filter(_ePackageInfos_1, _function_2);
      final Function1<EPackageInfo, String> _function_3 = new Function1<EPackageInfo, String>() {
        public String apply(final EPackageInfo it) {
          URI _genmodelURI = it.getGenmodelURI();
          return _genmodelURI.toString();
        }
      };
      Iterable<String> _map_1 = IterableExtensions.<EPackageInfo, String>map(_filter_1, _function_3);
      Set<String> _set = IterableExtensions.<String>toSet(_map_1);
      for(final String genmodelURI : _set) {
        _builder.append("\t\t");
        _builder.append("registerGenModelFile = \"");
        _builder.append(genmodelURI, "\t\t");
        _builder.append("\"");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("component = DirectoryCleaner {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("directory = \"${runtimeProject}/src-gen\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("component = DirectoryCleaner {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("directory = \"${runtimeProject}.ui/src-gen\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("component = Generator {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("pathRtProject = runtimeProject");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("pathUiProject = \"${runtimeProject}.ui\"");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("pathTestProject = \"${runtimeProject}.tests\"");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("projectNameRt = projectName");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("projectNameUi = \"${projectName}.ui\"");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("language = auto-inject {");
    _builder.newLine();
    {
      Collection<EPackageInfo> _ePackageInfos_2 = this.projectInfo.getEPackageInfos();
      final Function1<EPackageInfo, Boolean> _function_4 = new Function1<EPackageInfo, Boolean>() {
        public Boolean apply(final EPackageInfo it) {
          URI _genmodelURI = it.getGenmodelURI();
          String _fileExtension = _genmodelURI.fileExtension();
          return Boolean.valueOf(Objects.equal(_fileExtension, "xcore"));
        }
      };
      Iterable<EPackageInfo> _filter_2 = IterableExtensions.<EPackageInfo>filter(_ePackageInfos_2, _function_4);
      final Function1<EPackageInfo, String> _function_5 = new Function1<EPackageInfo, String>() {
        public String apply(final EPackageInfo it) {
          URI _genmodelURI = it.getGenmodelURI();
          return _genmodelURI.toString();
        }
      };
      Iterable<String> _map_2 = IterableExtensions.<EPackageInfo, String>map(_filter_2, _function_5);
      Set<String> _set_1 = IterableExtensions.<String>toSet(_map_2);
      for(final String genmodelURI_1 : _set_1) {
        _builder.append("\t\t\t");
        _builder.append("loadedResource = \"");
        _builder.append(genmodelURI_1, "\t\t\t");
        _builder.append("\"");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t\t\t");
    _builder.append("uri = grammarURI");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// Java API to access grammar elements (required by several other fragments)");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = grammarAccess.GrammarAccessFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// generates Java API for the generated EPackages");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = ecore.EcoreGeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// the Ecore2Xtext specific terminal converter");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = ecore2xtext.Ecore2XtextValueConverterServiceFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// serializer 2.0");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = serializer.SerializerFragment auto-inject {");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("//generateStub = false");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// the old serialization component");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = parseTreeConstructor.ParseTreeConstructorFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// a custom ResourceFactory for use with EMF ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = resourceFactory.ResourceFactoryFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// the Antlr parser");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = parser.antlr.XtextAntlrGeneratorFragment auto-inject {");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("options = {");
    _builder.newLine();
    _builder.append("\t\t\t\t\t");
    _builder.append("classSplitting = true");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// Xtend-based API for validation ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = validation.ValidatorFragment auto-inject {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// composedCheck = \"org.eclipse.xtext.validation.ImportUriValidator\"");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// composedCheck = \"org.eclipse.xtext.validation.NamesAreUniqueValidator\"");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// old scoping and exporting API ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = scoping.ImportNamespacesScopingFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = exporting.QualifiedNamesFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// scoping and exporting API");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = scoping.ImportURIScopingFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = exporting.SimpleNamesFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = builder.BuilderIntegrationFragment auto-inject {}\t\t");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// generator API");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = generator.GeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// formatter API ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = formatting.FormatterFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = ecore2xtext.FormatterFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// labeling API ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = labeling.LabelProviderFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// outline API ");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("fragment = outline.OutlineTreeProviderFragment auto-inject {}");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("fragment = outline.QuickOutlineFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// quickfix API");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = quickfix.QuickfixProviderFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("//content assist API ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = contentAssist.ContentAssistFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// antlr parser generator tailored for content assist ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = parser.antlr.XtextAntlrUiGeneratorFragment auto-inject {");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("options = {");
    _builder.newLine();
    _builder.append("\t\t\t\t\t");
    _builder.append("classSplitting = true");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// generates junit test support classes into Generator#pathTestProject");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = junit.Junit4Fragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// project wizard (optional) ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// fragment = projectWizard.SimpleProjectWizardFragment auto-inject {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("//\t\tgeneratorProjectName = \"${projectName}.generator\" ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// rename refactoring");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = refactoring.RefactorElementNameFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// provides the necessary bindings for java types integration");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = types.TypesGeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// generates the required bindings only if the grammar inherits from Xbase");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = xbase.XbaseGeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// generates the required bindings only if the grammar inherits from Xtype");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = xbase.XtypeGeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// provides a preference page for template proposals");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("fragment = templates.CodetemplatesGeneratorFragment auto-inject {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("// provides a compare view");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("fragment = compare.CompareFragment auto-inject {}");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
}
