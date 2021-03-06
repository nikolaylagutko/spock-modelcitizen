/**
 * Copyright 2015 Nikolay Lagutko <nikolay.lagutko@mail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gerzog.spock.modelcitizen.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gerzog.spock.modelcitizen.configurar.DefaultModelCitizenConfigurar;
import org.gerzog.spock.modelcitizen.extension.ModelCitizenExtension;
import org.spockframework.runtime.extension.ExtensionAnnotation;

/**
 * Mark Spec to be initialized with set of Blueprints
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(ModelCitizenExtension.class)
@Inherited
public @interface ModelCitizenBlueprints {

	/**
	 * Classes to be registered as Blueprints
	 *
	 * @return classes to be registred as blueprints
	 */
	Class<?>[] classes() default {};

	/**
	 * Package names to scan for Blueprint classes
	 *
	 * @return packages to be scanned for blueprint classes
	 */
	String[] packagesToScan() default {};

	/**
	 * Configurar class for ModelFactory
	 *
	 * @return extra-configurar for Model-Citizen
	 */
	Class<? extends IModelCitizenConfigurar> configurar() default DefaultModelCitizenConfigurar.class;

}
