package org.gerzog.spock.modelcitizen.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gerzog.spock.modelcitizen.ModelCitizenExtension;
import org.spockframework.runtime.extension.ExtensionAnnotation;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(ModelCitizenExtension.class)
public @interface UseBlueprints {

	Class<?>[] classes() default {};

	String packagesToScan() default "";

}
