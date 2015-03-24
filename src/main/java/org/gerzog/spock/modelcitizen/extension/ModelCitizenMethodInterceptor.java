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

import org.gerzog.spock.modelcitizen.api.Model;
import org.gerzog.spock.modelcitizen.extension.internal.AbstractModelCitizenMethodInterceptor;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.model.FieldInfo;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;

/**
 * Interceptor for Spec's Setup method
 *
 * Initialized all @Model fields with corresponding values from @Blueprints and
 * call original Setup method. So all values initialized at Setup method and can
 * be used :-)
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenMethodInterceptor extends AbstractModelCitizenMethodInterceptor {

	private final Map<FieldInfo, Model> fields;

	public ModelCitizenMethodInterceptor(final ModelFactory modelFactory, final Map<FieldInfo, Model> fields) {
		super(modelFactory);
		this.fields = fields;
	}

	@Override
	public void intercept(final Object instance) throws Throwable {
		fields.forEach((field, annotation) -> initializeModel(instance, field, annotation));
	}

	private void initializeModel(final Object target, final FieldInfo field, final Model annotation) {
		try {
			final Object value = generateFixture(field, annotation);

			field.writeValue(target, value);
		} catch (final CreateModelException e) {
			throw new InvalidSpecException("", e);
		}
	}

	private Object generateFixture(final FieldInfo field, final Model annotation) throws CreateModelException {
		return getModelFactory().createModel(defineClass(field.getType(), annotation.target()));
	}

	private Class<?> defineClass(final Class<?> fieldClass, final Class<?> annotaitonClass) {
		Class<?> result;

		if (annotaitonClass.equals(Model.DEFAULT.class)) {
			// case 1 - annotation's target is default
			result = fieldClass;
		} else if (fieldClass.equals(Object.class)) {
			// case 2 - field class is object (declared with 'def')
			result = annotaitonClass;
		} else {
			// case 3 - use annotation's target but check if it compatible with
			// field's type
			if (fieldClass.isAssignableFrom(annotaitonClass)) {
				result = annotaitonClass;
			} else {
				throw new InvalidSpecException("@Model's target class <" + annotaitonClass + "> cannot be cast to field's type <" + fieldClass + ">");
			}
		}

		return result;
	}

}
