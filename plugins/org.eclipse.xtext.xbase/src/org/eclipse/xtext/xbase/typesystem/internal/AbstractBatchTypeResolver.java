/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.internal;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.scoping.batch.FeatureScopes;
import org.eclipse.xtext.xbase.scoping.batch.IBatchScopeProvider;
import org.eclipse.xtext.xbase.scoping.batch.IFeatureScopeSession;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.IResolvedTypes;
import org.eclipse.xtext.xbase.typesystem.references.StandardTypeReferenceOwner;
import org.eclipse.xtext.xbase.typesystem.util.CommonTypeComputationServices;

import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public abstract class AbstractBatchTypeResolver implements IBatchTypeResolver {

	@Inject
	private IBatchScopeProvider scopeProvider;
	@Inject
	private FeatureScopes featureScopes;
	@Inject
	private CommonTypeComputationServices services;
	
	protected AbstractBatchTypeResolver() {
	}
	
	/* @NonNull */
	@Override
	public IResolvedTypes resolveTypes(/* @NonNull */ Resource resource) {
		return resolveTypes(resource, null);
	}

	/* @NonNull */
	@Override
	public final IResolvedTypes resolveTypes(final /* @Nullable */ EObject object) {
		return resolveTypes(object, null);
	}
	
	/* @NonNull */
	@Override
	public final IResolvedTypes resolveTypes(final /* @Nullable */ EObject object, CancelIndicator monitor) {
		if (object == null || object.eIsProxy()) {
			return IResolvedTypes.NULL;
		}
		return doResolveTypes(object, monitor);
	}
	
	/* @NonNull */
	@Override
	public IResolvedTypes resolveTypes(/* @NonNull */ Resource resource, /* @Nullable */ CancelIndicator monitor) {
		List<EObject> resourceContents = resource.getContents();
		if (resourceContents.isEmpty()) {
			IFeatureScopeSession session = scopeProvider.newSession(resource);
			return new EmptyResolvedTypes(session, featureScopes, new StandardTypeReferenceOwner(services, resource));
		} else {
			return resolveTypes(resourceContents.get(0), monitor);
		}
	}

	/* @NonNull */
	protected abstract IResolvedTypes doResolveTypes(/* @NonNull */ EObject object, /* @Nullable */ CancelIndicator monitor);
}
