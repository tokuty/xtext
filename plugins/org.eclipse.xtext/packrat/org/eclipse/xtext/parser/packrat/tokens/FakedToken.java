/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.parser.packrat.tokens;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.packrat.IParsedTokenVisitor;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class FakedToken extends ParsedToken {

	private final ParsedToken fake;

	public FakedToken(int offset, int length, EObject grammarElement, ParsedToken fake) {
		super(offset, length, grammarElement, null, false);
		this.fake = fake;
	}

	@Override
	public void accept(IParsedTokenVisitor visitor) {
		visitor.visitFakedToken(this);
	}

	public ParsedToken getFake() {
		return fake;
	}

}
