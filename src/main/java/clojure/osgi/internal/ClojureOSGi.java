package clojure.osgi.internal;

import clojure.lang.*;
import clojure.lang.Compiler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import clojure.osgi.RunnableWithException;

public class ClojureOSGi {

	static final private Var REQUIRE = RT.var("clojure.core", "require");
	static final private Var WITH_BUNDLE = RT.var("clojure.osgi.core", "with-bundle*");
	static final private Var BUNDLE = RT.var("clojure.osgi.core", "*bundle*").setDynamic();
	
	private static boolean s_Initialized;

	public static void initialize(final BundleContext aContext) throws Exception {
		if (!s_Initialized) {
			RT.var("clojure.osgi.core", "*clojure-osgi-bundle*", aContext.getBundle());
			withLoader(ClojureOSGi.class.getClassLoader(), new RunnableWithException<Void>() {
				public Void run() {
					boolean pushed = false;
					
					try {
						REQUIRE.invoke(Symbol.intern("clojure.main"));
						
						Var.pushThreadBindings(RT.map(BUNDLE, aContext.getBundle()));
						pushed = true;
						
						REQUIRE.invoke(Symbol.intern("clojure.osgi.core"));
						REQUIRE.invoke(Symbol.intern("clojure.osgi.services"));
					} catch (Exception e) {
						throw new RuntimeException("cannot initialize clojure.osgi", e);
					} finally {
                        if (pushed) {
                            Var.popThreadBindings();
                        }
					}
					return null;
				}
			});
			s_Initialized = true;
		}
	}

	public static void require(final Bundle aBundle, final String aName) {
		try {
			withBundle(aBundle, new RunnableWithException() {
				public Object run() throws Exception {
					REQUIRE.invoke(Symbol.intern(aName));
					return null;
				}
			});
		} catch (Exception aEx) {
            aEx.printStackTrace();
			throw new RuntimeException(aEx);
		}
	}

	public static <T> T withLoader(final ClassLoader aLoader, final RunnableWithException<T> aRunnable) throws Exception {
		try {
			Var.pushThreadBindings(RT.map(Compiler.LOADER, aLoader));
			return aRunnable.run();
		}
		finally {
			Var.popThreadBindings();
		}
	}
	
	static Object withBundle(final Bundle aBundle, final RunnableWithException aCode) throws Exception {
		return WITH_BUNDLE.invoke(aBundle, false, aCode);
	}

}
