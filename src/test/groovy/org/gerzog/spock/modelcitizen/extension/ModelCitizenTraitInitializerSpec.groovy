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
package org.gerzog.spock.modelcitizen.extension

import org.gerzog.spock.modelcitizen.internal.ModelCitizenTrait
import org.gerzog.spock.modelcitizen.test.SpecCompilationTrait
import org.gerzog.spock.modelcitizen.test.specs.TestConstants
import org.spockframework.runtime.extension.IMethodInvocation

import spock.lang.Specification

import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenTraitInitializerSpec extends Specification implements SpecCompilationTrait {

	def modelFactory = Mock(ModelFactory)

	def interceptor = new ModelCitizenTraitInitializer(modelFactory)

	def invocation = Mock(IMethodInvocation)

	def instance

	def "check model not initialized for non-traited spec"() {
		setup:
		instance = newSpec(TestConstants.SAMPLE_SPEC)
		and:
		invocation.instance >> instance

		when:
		apply()

		then:
		!instance.metaClass.hasProperty('modelFactory')
	}

	def "check model initialized for traited spec"() {
		setup:
		instance = newSpec(TestConstants.SAMPLE_SPEC, ModelCitizenTrait)
		and:
		invocation.instance >> instance

		when:
		apply()

		then:
		instance.modelFactory == modelFactory
	}

	private apply() {
		interceptor.intercept(invocation)
	}
}
