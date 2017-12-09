package com.ibm.util.merge.template.directive.enrich.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class Providers extends HashMap<String, Class<ProviderInterface>> {
	private static final long serialVersionUID = 1L;

	public Providers() {
		super();
	}
	
	// Provider Management
	public ProviderInterface getProviderInstance(String className, String source, String parameter) throws MergeException {
		if (!this.containsKey(className)) {
			throw new Merge500("Provider not found, did you register it?");
		}
		
		ProviderInterface theProvider;
		try {
			@SuppressWarnings("rawtypes")
			Class[] cArg = new Class[2]; 
			cArg[0] = String.class;
			cArg[1] = String.class;
			theProvider = (ProviderInterface) this.get(className).getDeclaredConstructor(cArg).newInstance(source, parameter);
		} catch (InstantiationException e) {
			throw new Merge500("Error instantiating class: " + className + " message: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Merge500("Error accessing class: " + className + " message: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new Merge500("IllegalArgumentException : " + className + " message: " + e.getMessage());
		} catch (SecurityException e) {
			throw new Merge500("Error accessing class: " + className + " message: " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new Merge500("InvocationTargetException: " + className + " message: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Merge500("NoSuchMethodException: " + className + " message: " + e.getMessage());
		}
		
		return theProvider;
	}
	
	public void registerDefaultProviders(String[] providers) throws MergeException {
		for (String provider : providers) {
			registerProvider(provider);
		}
	}

	@SuppressWarnings("unchecked")
	public void registerProvider(String className) throws MergeException {
		Class<ProviderInterface> clazz;
		try {
			clazz = (Class<ProviderInterface>) Class.forName(className);
			this.put(className, clazz);
		} catch (ClassNotFoundException e) {
			throw new Merge500("Class Not Found exception: " + className + " message: " + e.getMessage());
		}
	}

}
