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
package org.gerzog.spock.modelcitizen.configurar;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.gerzog.spock.modelcitizen.api.IModelCitizenBuilder;

import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.ModelFactoryException;
import com.tobedevoured.modelcitizen.policy.Policy;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenBuilder implements IModelCitizenBuilder {

	private static final Policy[] EMPTY_POLICIES = new Policy[0];

	private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

	private Policy[] policies;

	private Class<?>[] classes;

	private String[] packages;

	public ModelCitizenBuilder() {
		this(EMPTY_POLICIES, EMPTY_CLASSES, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	private ModelCitizenBuilder(final Policy[] policies, final Class<?>[] classes, final String[] packages) {
		this.policies = policies;
		this.classes = classes;
		this.packages = packages;
	}

	@Override
	public IModelCitizenBuilder withPolicies(final Policy... policies) {
		this.policies = merge(this.policies, policies);

		return this;
	}

	@Override
	public IModelCitizenBuilder fromPackages(final String... packages) {
		this.packages = merge(this.packages, packages);

		return this;
	}

	@Override
	public IModelCitizenBuilder fromClasses(final Class<?>... classes) {
		this.classes = merge(this.classes, classes);

		return this;
	}

	@Override
	public ModelFactory build() throws ModelFactoryException {
		final ModelFactory factory = new ModelFactory();

		factory.setRegisterBlueprints(Arrays.asList(classes));

		for (final String packageName : packages) {
			factory.setRegisterBlueprintsByPackage(packageName);
		}

		for (final Policy policy : policies) {
			factory.addPolicy(policy);
		}

		return factory;
	}

	private <T> T[] merge(final T[] existing, final T[] updates) {
		return ArrayUtils.addAll(existing, updates);
	}

}
