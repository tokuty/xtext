/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.internal;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.typesystem.IResolvedTypes;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * TODO JavaDoc, toString
 */
public class DefaultBatchTypeResolver extends AbstractBatchTypeResolver {
	
	@Inject
	private Provider<AbstractRootedReentrantTypeResolver> typeResolverProvider;
	
	@Override
	protected IResolvedTypes doResolveTypes(/* @Nullable */ EObject object,  /* @Nullable */ CancelIndicator monitor) {
		// TODO: remove when we switch to an Xtend scope provider without artificial feature calls  
		EObject nonArtificialObject = getNonArtificialObject(object);
		// TODO end
		IReentrantTypeResolver reentrantResolver = getTypeResolver(nonArtificialObject);
		return reentrantResolver.reentrantResolve(monitor == null ? CancelIndicator.NullImpl : monitor);
	}

	private EObject getNonArtificialObject(EObject object) {
		EObject nonArtificialObject = object;
		if(object.eResource() == null && object instanceof XAbstractFeatureCall) {
			nonArtificialObject = ((XAbstractFeatureCall) object).getFeature();
		}
		return nonArtificialObject;
	}
	
	protected IReentrantTypeResolver getTypeResolver(EObject object) {
		Set<EObject> allRootedExpressions = Sets.newHashSet();
		List<EObject> roots = getEntryPoints(object);
		if (roots.size() == 1) {
			AbstractRootedReentrantTypeResolver result = createResolver(roots.get(0));
			result.setAllRootedExpressions(allRootedExpressions);
			return result;
		}
		if (roots.isEmpty()) {
			return IReentrantTypeResolver.NULL;
		}
		CompoundReentrantTypeResolver result = new CompoundReentrantTypeResolver(allRootedExpressions);
		for(EObject root: roots) {
			AbstractRootedReentrantTypeResolver resolver = createResolver(root);
			result.addResolver(resolver);
		}
		return result;
	}
	
	protected List<EObject> getEntryPoints(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		if (rootContainer instanceof XExpression) {
			return Collections.singletonList(rootContainer);	
		}
		return Collections.emptyList();
	}
	
	protected AbstractRootedReentrantTypeResolver createResolver(EObject root) {
		AbstractRootedReentrantTypeResolver resolver = createResolver();
		resolver.initializeFrom(root);
		return resolver;
	}

	protected AbstractRootedReentrantTypeResolver createResolver() {
		return typeResolverProvider.get();
	}

}
