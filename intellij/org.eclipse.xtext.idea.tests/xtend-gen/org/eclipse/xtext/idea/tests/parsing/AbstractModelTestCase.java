package org.eclipse.xtext.idea.tests.parsing;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.inject.Provider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.idea.lang.BaseXtextASTFactory;
import org.eclipse.xtext.idea.lang.IXtextLanguage;
import org.eclipse.xtext.idea.resource.IResourceSetProvider;
import org.eclipse.xtext.idea.resource.PsiToEcoreTransformator;
import org.eclipse.xtext.idea.tests.LibraryUtil;
import org.eclipse.xtext.idea.tests.parsing.ModelChecker;
import org.eclipse.xtext.idea.tests.parsing.XtextResourceAsserts;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.psi.impl.BaseXtextFile;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class AbstractModelTestCase extends LightCodeInsightFixtureTestCase implements ModelChecker {
  public final static DefaultLightProjectDescriptor JAVA_INTERNAL = new DefaultLightProjectDescriptor() {
    public Sdk getSdk() {
      JavaAwareProjectJdkTableImpl _instanceEx = JavaAwareProjectJdkTableImpl.getInstanceEx();
      return _instanceEx.getInternalJdk();
    }
    
    public void configureModule(final Module module, final ModifiableRootModel model, final ContentEntry contentEntry) {
      final LanguageLevelModuleExtension languageLevelModuleExtension = model.<LanguageLevelModuleExtension>getModuleExtension(LanguageLevelModuleExtension.class);
      boolean _notEquals = (!Objects.equal(languageLevelModuleExtension, null));
      if (_notEquals) {
        languageLevelModuleExtension.setLanguageLevel(LanguageLevel.JDK_1_6);
      }
      LibraryUtil.addXbaseLibrary(model);
    }
  };
  
  @Inject
  @Accessors(AccessorType.PROTECTED_GETTER)
  private BaseXtextASTFactory astFactory;
  
  @Inject
  @Accessors(AccessorType.PROTECTED_GETTER)
  private ValidationTestHelper validationHelper;
  
  @Inject
  @Accessors(AccessorType.PROTECTED_GETTER)
  private IResourceSetProvider resourceSetProvider;
  
  @Inject
  @Accessors(AccessorType.PROTECTED_GETTER)
  private Provider<PsiToEcoreTransformator> psiToEcoreTransformatorProvider;
  
  @Inject
  @Accessors(AccessorType.PROTECTED_GETTER)
  @Extension
  private XtextResourceAsserts xtextResourceAsserts;
  
  private final LanguageFileType fileType;
  
  public AbstractModelTestCase(final LanguageFileType fileType) {
    this.fileType = fileType;
    IXtextLanguage _xtextLanguage = this.getXtextLanguage();
    _xtextLanguage.injectMembers(this);
  }
  
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  protected IXtextLanguage getXtextLanguage() {
    Language _language = this.fileType.getLanguage();
    return ((IXtextLanguage) _language);
  }
  
  protected LightProjectDescriptor getProjectDescriptor() {
    return AbstractModelTestCase.JAVA_INTERNAL;
  }
  
  public <T extends EObject> T checkModel(final String code, final boolean validate) {
    T _xblockexpression = null;
    {
      this.checkModel(code);
      XtextResource _actualResource = this.getActualResource();
      EList<EObject> _contents = _actualResource.getContents();
      EObject _head = IterableExtensions.<EObject>head(_contents);
      final T model = ((T) _head);
      if (validate) {
        this.validationHelper.assertNoErrors(model);
      }
      _xblockexpression = model;
    }
    return _xblockexpression;
  }
  
  protected void checkModel(final String code) {
    this.myFixture.configureByText(this.fileType, code);
    final XtextResource actualResource = this.getActualResource();
    final XtextResource expectedResource = this.createExpectedResource();
    this.xtextResourceAsserts.assertResource(expectedResource, actualResource);
  }
  
  protected XtextResource getActualResource() {
    BaseXtextFile _xtextFile = this.getXtextFile();
    Resource _resource = _xtextFile.getResource();
    return ((XtextResource) _resource);
  }
  
  protected XtextResource createExpectedResource() {
    Project _project = this.myFixture.getProject();
    ResourceSet resourceSet = this.resourceSetProvider.get(_project);
    BaseXtextFile _xtextFile = this.getXtextFile();
    VirtualFile _virtualFile = _xtextFile.getVirtualFile();
    String _url = _virtualFile.getUrl();
    URI _createURI = URI.createURI(_url);
    Resource _createResource = resourceSet.createResource(_createURI);
    XtextResource resource = ((XtextResource) _createResource);
    try {
      BaseXtextFile _xtextFile_1 = this.getXtextFile();
      String _text = _xtextFile_1.getText();
      byte[] _bytes = _text.getBytes();
      ByteArrayInputStream _byteArrayInputStream = new ByteArrayInputStream(_bytes);
      resource.load(_byteArrayInputStream, null);
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        throw new RuntimeException(e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return resource;
  }
  
  protected BaseXtextFile getXtextFile() {
    PsiFile _file = this.myFixture.getFile();
    return ((BaseXtextFile) _file);
  }
  
  @Pure
  protected BaseXtextASTFactory getAstFactory() {
    return this.astFactory;
  }
  
  @Pure
  protected ValidationTestHelper getValidationHelper() {
    return this.validationHelper;
  }
  
  @Pure
  protected IResourceSetProvider getResourceSetProvider() {
    return this.resourceSetProvider;
  }
  
  @Pure
  protected Provider<PsiToEcoreTransformator> getPsiToEcoreTransformatorProvider() {
    return this.psiToEcoreTransformatorProvider;
  }
  
  @Pure
  protected XtextResourceAsserts getXtextResourceAsserts() {
    return this.xtextResourceAsserts;
  }
}
