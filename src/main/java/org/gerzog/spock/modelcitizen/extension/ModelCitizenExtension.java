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
package org.gerzog.spock.modelcitizen.extension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.gerzog.spock.modelcitizen.api.Model;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;

import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.RegisterBlueprintException;

/**
 * Extension's entry point
 *
 * Initialized ModelCitizen's ModelFactory according to Annotation configuration
 * and registers interceptor for Spec's Setup method
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenExtension extends AbstractAnnotationDrivenExtension<UseBlueprints> {

	@Override
	public void visitSpecAnnotation(final UseBlueprints annotation, final SpecInfo spec) {
		ModelFactory factory = new ModelFactory();

		initializeBlueprints(factory, annotation, spec);
		registerTraitInitializerInterceptor(factory, spec);
		registerModelAnnotationInterceptor(factory, spec);
	}

	private void registerTraitInitializerInterceptor(final ModelFactory factory, final SpecInfo spec) {
		spec.getInitializerInterceptors().add(0, new ModelCitizenTraitInitializer(factory));
	}

	private void registerModelAnnotationInterceptor(final ModelFactory factory, final SpecInfo spec) {
		List<FieldInfo> modelFields = getModelFields(spec);

		if (!modelFields.isEmpty()) {
			validateModelFields(modelFields);

			spec.addSetupInterceptor(new ModelCitizenMethodInterceptor(factory, modelFields));
		}
	}

	private void validateModelFields(final List<FieldInfo> modelFields) {
		modelFields.forEach(this::validateModelField);
	}

	private void validateModelField(final FieldInfo modelField) {
		if (modelField.getType().equals(Object.class)) {
			throw new InvalidSpecException("Object class was detected as @Model source. Please check you didn't use 'def' keyword to define @Model field");
		}
	}

	private List<FieldInfo> getModelFields(final SpecInfo spec) {
		return spec.getAllFields().stream().filter(field -> field.isAnnotationPresent(Model.class)).collect(Collectors.toList());
	}

	private void initializeBlueprints(final ModelFactory factory, final UseBlueprints annotation, final SpecInfo spec) {
		try {
			// register blueprints from classes
			factory.setRegisterBlueprints(Arrays.asList(annotation.classes()));

			// register blueprints from packages
			for (String packageName : annotation.packagesToScan()) {
				factory.setRegisterBlueprintsByPackage(packageName);
			}
		} catch (RegisterBlueprintException e) {
			throw new InvalidSpecException("An error occured during ModelCitizen initialization. Please check your @UseBlueprints configuration for " + spec.getName() + " spec", e);
		}
	}
}
