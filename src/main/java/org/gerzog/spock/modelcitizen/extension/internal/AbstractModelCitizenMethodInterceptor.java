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
package org.gerzog.spock.modelcitizen.extension.internal;

import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;

import com.tobedevoured.modelcitizen.ModelFactory;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public abstract class AbstractModelCitizenMethodInterceptor implements IMethodInterceptor {

	private final ModelFactory modelFactory;

	protected AbstractModelCitizenMethodInterceptor(final ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	protected ModelFactory getModelFactory() {
		return modelFactory;
	}

	@Override
	public void intercept(final IMethodInvocation invocation) throws Throwable {
		intercept(invocation.getInstance());

		invocation.proceed();
	}

	protected abstract void intercept(Object instance) throws Throwable;

}
