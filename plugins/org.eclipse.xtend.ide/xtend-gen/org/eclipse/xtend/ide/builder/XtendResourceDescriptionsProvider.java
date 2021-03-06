package org.eclipse.xtend.ide.builder;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtend.ide.builder.FilteringResourceDescriptions;
import org.eclipse.xtext.builder.clustering.CurrentDescriptions;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.resource.CompilerPhases;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

@SuppressWarnings("all")
public class XtendResourceDescriptionsProvider extends ResourceDescriptionsProvider {
  @Inject
  private IJavaProjectProvider projectProvider;
  
  @Inject
  private CompilerPhases compilerPhases;
  
  /**
   * In the builder we use the Java representation for any upstream resources, so we filter them out here.
   * And if we are in the indexing phase, we don't even want to see the local resources.
   */
  public IResourceDescriptions getResourceDescriptions(final ResourceSet resourceSet) {
    final IResourceDescriptions result = super.getResourceDescriptions(resourceSet);
    final IJavaProject project = this.projectProvider.getJavaProject(resourceSet);
    boolean _matched = false;
    if (!_matched) {
      if (result instanceof CurrentDescriptions.ResourceSetAware) {
        _matched=true;
        IResourceDescriptions _delegate = ((CurrentDescriptions.ResourceSetAware)result).getDelegate();
        boolean _matched_1 = false;
        if (!_matched_1) {
          if (_delegate instanceof CurrentDescriptions) {
            _matched_1=true;
            IProject _project = project.getProject();
            String _name = _project.getName();
            final String encodedProjectName = URI.encodeSegment(_name, true);
            boolean _isIndexing = this.compilerPhases.isIndexing(resourceSet);
            if (_isIndexing) {
              final Function1<URI, Boolean> _function = new Function1<URI, Boolean>() {
                public Boolean apply(final URI uri) {
                  boolean _or = false;
                  boolean _equals = Objects.equal(uri, null);
                  if (_equals) {
                    _or = true;
                  } else {
                    int _segmentCount = uri.segmentCount();
                    boolean _lessThan = (_segmentCount < 2);
                    _or = _lessThan;
                  }
                  if (_or) {
                    return Boolean.valueOf(false);
                  }
                  boolean _and = false;
                  String _segment = uri.segment(1);
                  boolean _notEquals = (!Objects.equal(_segment, encodedProjectName));
                  if (!_notEquals) {
                    _and = false;
                  } else {
                    String _fileExtension = uri.fileExtension();
                    boolean _notEquals_1 = (!Objects.equal(_fileExtension, "xtend"));
                    _and = _notEquals_1;
                  }
                  return Boolean.valueOf(_and);
                }
              };
              return new FilteringResourceDescriptions(result, _function);
            }
            final Function1<URI, Boolean> _function_1 = new Function1<URI, Boolean>() {
              public Boolean apply(final URI uri) {
                boolean _or = false;
                boolean _equals = Objects.equal(uri, null);
                if (_equals) {
                  _or = true;
                } else {
                  int _segmentCount = uri.segmentCount();
                  boolean _lessThan = (_segmentCount < 2);
                  _or = _lessThan;
                }
                if (_or) {
                  return Boolean.valueOf(false);
                }
                boolean _or_1 = false;
                String _segment = uri.segment(1);
                boolean _equals_1 = Objects.equal(_segment, encodedProjectName);
                if (_equals_1) {
                  _or_1 = true;
                } else {
                  String _fileExtension = uri.fileExtension();
                  boolean _notEquals = (!Objects.equal(_fileExtension, "xtend"));
                  _or_1 = _notEquals;
                }
                return Boolean.valueOf(_or_1);
              }
            };
            return new FilteringResourceDescriptions(result, _function_1);
          }
        }
      }
    }
    boolean _isIndexing = this.compilerPhases.isIndexing(resourceSet);
    if (_isIndexing) {
      IProject _project = project.getProject();
      String _name = _project.getName();
      final String encodedProjectName = URI.encodeSegment(_name, true);
      final Function1<URI, Boolean> _function = new Function1<URI, Boolean>() {
        public Boolean apply(final URI uri) {
          boolean _or = false;
          boolean _equals = Objects.equal(uri, null);
          if (_equals) {
            _or = true;
          } else {
            int _segmentCount = uri.segmentCount();
            boolean _lessThan = (_segmentCount < 2);
            _or = _lessThan;
          }
          if (_or) {
            return Boolean.valueOf(false);
          }
          String _segment = uri.segment(1);
          return Boolean.valueOf((!Objects.equal(_segment, encodedProjectName)));
        }
      };
      return new FilteringResourceDescriptions(result, _function);
    } else {
      return result;
    }
  }
}
