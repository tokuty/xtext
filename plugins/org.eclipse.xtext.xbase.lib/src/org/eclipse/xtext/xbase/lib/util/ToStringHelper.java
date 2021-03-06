/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.lib.util;

import com.google.common.annotations.Beta;

/**
 * A helper class to assist construction of proper {@link Object#toString()} implementation for value objects.
 * 
 * @author Sven Efftinge - Initial contribution and API
 * 
 * @since 2.3
 * @deprected use {@link ToStringBuilder}
 */
@Beta
@Deprecated
public class ToStringHelper {

	/**
	 * @deprecated use {@link ToStringBuilder}
	 */
	@Deprecated
	public ToStringHelper() {
	}

	/**
	 * Creates a string representation of the given object by listing the internal state of all fields.
	 * 
	 * @param obj
	 *            the object that should be printed.
	 * @return the string representation. Never <code>null</code>.
	 * @deprecated use <code>new ToStringBuilder().addAllFields().toString()</code>
	 */
	@Deprecated
	public String toString(Object obj) {
		return new ToStringBuilder(obj).addAllFields().toString();
	}

}
