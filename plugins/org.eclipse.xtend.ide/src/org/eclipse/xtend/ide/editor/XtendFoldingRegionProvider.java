/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.ide.editor;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.xtend.core.xtend.XtendFile;
import org.eclipse.xtend.core.xtend.XtendFunction;
import org.eclipse.xtend.core.xtend.XtendPackage;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.FoldedPosition;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptorExtension;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.ITextRegion;

import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 * @author Holger Schill
 */
public class XtendFoldingRegionProvider extends DefaultFoldingRegionProvider {
	
	@Inject FoldingPreferences foldingPreferences;
	
	@Override
	public Collection<FoldedPosition> getFoldingRegions(IXtextDocument xtextDocument) {
		if (foldingPreferences.isEnabled()) {
			return super.getFoldingRegions(xtextDocument);
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	protected boolean isHandled(EObject object) {
		EClass clazz = object.eClass();
		return clazz == XtendPackage.Literals.XTEND_CLASS
				|| clazz == XtendPackage.Literals.XTEND_FILE
				|| clazz == XtendPackage.Literals.XTEND_INTERFACE
				|| clazz == XtendPackage.Literals.XTEND_ENUM
				|| clazz == XtendPackage.Literals.XTEND_ANNOTATION_TYPE
				|| clazz == XtendPackage.Literals.ANONYMOUS_CLASS
				|| clazz == XtendPackage.Literals.XTEND_FUNCTION 
				|| clazz == XtendPackage.Literals.XTEND_CONSTRUCTOR;
	}
	
	@Override
	protected boolean shouldProcessContent(EObject object) {
		return true;
	}
	
	@Override
	protected void computeCommentFolding(IXtextDocument xtextDocument,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor, ITypedRegion typedRegion, boolean initiallyFolded)
			throws BadLocationException {
		if (xtextDocument.get(0, typedRegion.getOffset()).trim().length()==0) {
			super.computeCommentFolding(xtextDocument, foldingRegionAcceptor, typedRegion, foldingPreferences.isFoldHeader());
		} else {
			super.computeCommentFolding(xtextDocument, foldingRegionAcceptor, typedRegion, foldingPreferences.isFoldComments());
		}
	}
	
	@Override
	protected void computeObjectFolding(EObject eObject, IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		if (eObject instanceof XtendFile) {
			computeImportFolding((XtendFile)eObject, foldingRegionAcceptor);
		} else {
			boolean initiallyFolded = false;
			if (eObject instanceof XtendFunction) {
				initiallyFolded = foldingPreferences.isFoldMethods();
			}
			if (eObject instanceof XtendTypeDeclaration && !(eObject.eContainer() instanceof XtendFile)) {
				initiallyFolded = foldingPreferences.isFoldInnerTypes();
			}
			super.computeObjectFolding(eObject, foldingRegionAcceptor, initiallyFolded);
		}
	}

	protected void computeImportFolding(XtendFile xtendFile, IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
		if(xtendFile.getImportSection() != null 
				&& xtendFile.getImportSection().getImportDeclarations().size() >1) {
			ICompositeNode node = NodeModelUtils.findActualNodeFor(xtendFile.getImportSection());
			if(node != null) {
				ITextRegion textRegion = node.getTextRegion();
				String preference = PreferenceConstants.getPreference(PreferenceConstants.EDITOR_FOLDING_IMPORTS,  null);
				((IFoldingRegionAcceptorExtension<ITextRegion>)foldingRegionAcceptor).accept(textRegion.getOffset(), textRegion.getLength(), Boolean.valueOf(preference));
			}
		}
	}

}
