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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.gerzog.spock.modelcitizen.api.IModelCitizenConfigurar;
import org.gerzog.spock.modelcitizen.api.Model;
import org.gerzog.spock.modelcitizen.api.ModelCitizenBlueprints;
import org.gerzog.spock.modelcitizen.configurar.ModelCitizenBuilder;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;

import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.ModelFactoryException;

/**
 * Extension's entry point
 *
 * Initialized ModelCitizen's ModelFactory according to Annotation configuration
 * and registers interceptor for Spec's Setup method
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenExtension extends AbstractAnnotationDrivenExtension<ModelCitizenBlueprints> {

	@Override
	public void visitSpecAnnotation(final ModelCitizenBlueprints annotation, final SpecInfo spec) {
		if (spec.getIsTopSpec()) {
			final ModelFactory factory = initializeFactory(annotation, spec);

			registerTraitInitializerInterceptor(factory, spec);
			registerModelAnnotationInterceptor(factory, spec, getModelFields(spec));
		}
	}

	private void registerTraitInitializerInterceptor(final ModelFactory factory, final SpecInfo spec) {
		spec.getInitializerInterceptors().add(0, new ModelCitizenTraitInitializer(factory));
	}

	private void registerModelAnnotationInterceptor(final ModelFactory factory, final SpecInfo spec, final Map<FieldInfo, Model> modelFields) {
		if (!modelFields.isEmpty()) {
			validateModelFields(spec.getName(), modelFields);

			spec.addSetupInterceptor(new ModelCitizenMethodInterceptor(factory, modelFields));
		}
	}

	private void validateModelFields(final String specName, final Map<FieldInfo, Model> modelFields) {
		modelFields.forEach((field, annotation) -> validateModelField(specName, field, annotation));
	}

	private void validateModelField(final String specName, final FieldInfo modelField, final Model annotation) {
		if (Objects.equals(modelField.getType(), Object.class) && annotation.target().equals(Model.DEFAULT.class)) {
			throw new InvalidSpecException("Object class was detected as @Model source in <" + specName + ">. Please check you declared field type or set target of @Model field");
		}
	}

	private Map<FieldInfo, Model> getModelFields(final SpecInfo spec) {
		return spec.getAllFields().stream().filter(field -> field.isAnnotationPresent(Model.class)).collect(Collectors.toMap(field -> field, field -> field.getAnnotation(Model.class)));
	}

	private ModelFactory initializeFactory(final ModelCitizenBlueprints annotation, final SpecInfo spec) {
		try {
			final IModelCitizenConfigurar configurar = createConfigurar(annotation, spec);
			final ModelCitizenBuilder builder = new ModelCitizenBuilder();

			configurar.configure(annotation, builder);

			return builder.build();
		} catch (final ModelFactoryException e) {
			throw new InvalidSpecException("An error occured during ModelCitizen initialization. Please check your @UseBlueprints configuration for " + spec.getName() + " spec", e);
		} catch (final Exception e) {
			throw new InvalidSpecException("An error occured during configuring ModelCitizen factory for spec <" + spec.getName() + ">", e);
		}
	}

	private IModelCitizenConfigurar createConfigurar(final ModelCitizenBlueprints annotation, final SpecInfo spec) {
		final Class<? extends IModelCitizenConfigurar> configurar = annotation.configurar();

		try {
			return configurar.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidSpecException("Cannot create configurar for spec <" + spec.getName() + ">", e);
		}
	}
}
