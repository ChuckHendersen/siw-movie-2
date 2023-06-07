package it.uniroma3.siw.controller;

public class ControllerUtils {

	
	/**
	 * Returns successful path if the object passed is not null, returns failure path otherwise
	 * @param modelObj Object to be tested if null or not
	 * @param successPath path of success
	 * @param failurePath path of failure
	 * @return path of redirection
	 */
	public static String redirection(Object modelObj, String successPath, String failurePath) {
		if(modelObj != null) {
			return successPath;
		}else {
			return failurePath;
		}
	}
	
	public static String redirection(boolean condition, String successPath, String failurePath) {
		if(condition) {
			return successPath;
		}else {
			return failurePath;
		}
	}
}
