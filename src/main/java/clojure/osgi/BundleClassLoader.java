/*
 * Copyright (c) 2009 Laurent Petit and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *    Laurent PETIT - initial API and implementation
 */

package clojure.osgi;

import java.net.URL;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {

	private Bundle _bundle;
	private boolean _forceDirect;

	public BundleClassLoader(final Bundle bundle) {
		this(bundle, false);
	}

	public BundleClassLoader(final Bundle bundle, final boolean forceDirect) {
		_bundle = bundle;
		_forceDirect = forceDirect;
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		return _bundle.loadClass(name);
	}

	@Override
	public URL getResource(final String name) {
		if(_forceDirect) {
			return _bundle.getEntry(name);
		}
		return _bundle.getResource(name);
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BundleClassLoader");
        sb.append("{_bundle=").append(_bundle);
        sb.append(",_forceDirect=").append(_forceDirect ? "true" : "false");
        sb.append('}');
        return sb.toString();
    }

}
