package org.eclipse.xtext.formatting2.internal.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting2.internal.formattertestlanguage.FormattertestlanguagePackage;
import org.eclipse.xtext.formatting2.internal.formattertestlanguage.IDList;
import org.eclipse.xtext.formatting2.internal.formattertestlanguage.KWList;
import org.eclipse.xtext.formatting2.internal.services.FormatterTestLanguageGrammarAccess;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.GenericSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;

@SuppressWarnings("all")
public class FormatterTestLanguageSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private FormatterTestLanguageGrammarAccess grammarAccess;
	
	public void createSequence(EObject context, EObject semanticObject) {
		if(semanticObject.eClass().getEPackage() == FormattertestlanguagePackage.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case FormattertestlanguagePackage.ID_LIST:
				if(context == grammarAccess.getIDListRule() ||
				   context == grammarAccess.getRootRule()) {
					sequence_IDList(context, (IDList) semanticObject); 
					return; 
				}
				else break;
			case FormattertestlanguagePackage.KW_LIST:
				if(context == grammarAccess.getKWListRule() ||
				   context == grammarAccess.getRootRule()) {
					sequence_KWList(context, (KWList) semanticObject); 
					return; 
				}
				else break;
			}
		if (errorAcceptor != null) errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Constraint:
	 *     (ids+=ID*)
	 */
	protected void sequence_IDList(EObject context, IDList semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (kw1?='kw1'? kw2?='kw2'? kw3?='kw3'? kw4?='kw4'? kw5?='kw5'?)
	 */
	protected void sequence_KWList(EObject context, KWList semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
}
