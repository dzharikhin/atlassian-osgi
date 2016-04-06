package org.jrx.atlassian.mybatis.cl;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dzharikhin on 08.12.2015.
 */
public class BundleDelegatingClassLoader extends ClassLoader {
    private final Bundle bundle;

    public BundleDelegatingClassLoader(final Bundle bundle) {
        super(null);
        this.bundle = checkNotNull(bundle);
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        if (name != null && name.contains("org.osgi.framework")) {
            return this.getClass().getClassLoader().loadClass(name);
        }
        return bundle.loadClass(name);
    }

    @Override
    public Enumeration<URL> findResources(final String name) throws IOException {
        Enumeration<URL> e = bundle.getResources(name);

        if (e == null) {
            e = new IteratorEnumeration(Collections.emptyList().iterator());
        } else {
            if (!e.hasMoreElements()) {
                final URL resource = findResource(name);
                if (resource != null) {
                    e = new IteratorEnumeration(Arrays.asList(resource).iterator());
                }
            }
        }
        return e;
    }

    @Override
    public URL findResource(final String name) {
        return bundle.getResource(name);
    }

    @Override
    public String toString() {
        return "BundleDelegatingClassLoader{" +
            "bundle=" + bundle.getSymbolicName() +
            '}';
    }
}
