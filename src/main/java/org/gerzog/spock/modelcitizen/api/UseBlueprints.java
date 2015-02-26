package org.gerzog.spock.modelcitizen.api;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public @interface UseBlueprints {

	Class<?>[] classes();

	String packageToScan() default "";

}
