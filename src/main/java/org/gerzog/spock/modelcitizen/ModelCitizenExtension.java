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
package org.gerzog.spock.modelcitizen;

import java.util.Arrays;
import java.util.stream.Stream;

import org.gerzog.spock.modelcitizen.api.Model;
import org.gerzog.spock.modelcitizen.api.UseBlueprints;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;

import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.RegisterBlueprintException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenExtension extends AbstractAnnotationDrivenExtension<UseBlueprints> {

	@Override
	public void visitSpecAnnotation(final UseBlueprints annotation, final SpecInfo spec) {
		ModelFactory factory = new ModelFactory();

		initializeBlueprints(factory, annotation, spec);
		initializeModels(factory, spec);
	}

	private void initializeModels(final ModelFactory factory, final SpecInfo spec) {
		initializeModels(factory, getModelFields(spec));
	}

	private void initializeModels(final ModelFactory factory, final Stream<FieldInfo> modelFiels) {
		modelFiels.forEach(field -> initializeModel(factory, field));
	}

	private void initializeModel(final ModelFactory factory, final FieldInfo modelField) {
		validateModelField(modelField);
	}

	private void validateModelField(final FieldInfo modelField) {
		if (modelField.getType().equals(Object.class)) {
			throw new InvalidSpecException("Object class was detected as @Model source. Please check you didn't use 'def' keyword to define @Model field");
		}
	}

	private Stream<FieldInfo> getModelFields(final SpecInfo spec) {
		return spec.getAllFields().stream().filter(field -> field.isAnnotationPresent(Model.class));
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
