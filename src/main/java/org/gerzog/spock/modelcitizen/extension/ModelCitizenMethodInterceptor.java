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

import java.util.List;

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

	private final List<FieldInfo> fields;

	public ModelCitizenMethodInterceptor(final ModelFactory modelFactory, final List<FieldInfo> fields) {
		super(modelFactory);
		this.fields = fields;
	}

	@Override
	public void intercept(final Object instance) throws Throwable {
		fields.forEach(field -> initializeModel(instance, field));
	}

	private void initializeModel(final Object target, final FieldInfo field) {
		try {
			Object value = generateFixture(field);

			field.writeValue(target, value);
		} catch (CreateModelException e) {
			throw new InvalidSpecException("", e);
		}
	}

	private Object generateFixture(final FieldInfo field) throws CreateModelException {
		return getModelFactory().createModel(field.getType());
	}

}
