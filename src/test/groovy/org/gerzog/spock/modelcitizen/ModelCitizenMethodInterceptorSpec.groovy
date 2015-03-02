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
package org.gerzog.spock.modelcitizen

import org.gerzog.spock.modelcitizen.test.TestUtilsTrait
import org.gerzog.spock.modelcitizen.test.data.Bean
import org.gerzog.spock.modelcitizen.test.specs.SampleSpec
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IMethodInvocation

import spock.lang.Specification

import com.tobedevoured.modelcitizen.CreateModelException
import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenMethodInterceptorSpec extends Specification implements TestUtilsTrait {

	def modelFactory = Mock(ModelFactory)

	def invocation = Mock(IMethodInvocation)

	def target = new SampleSpec()

	def model = Mock(Bean)

	def setup() {
		invocation.target >> target
	}

	def "check model factory was called for model fields initialization"() {
		setup:
		def spec = spec(SampleSpec)

		when:
		callInterceptor(spec)

		then:
		1 * modelFactory.createModel(Bean)
	}

	def "check spec exception occurs if model factory raises error"() {
		setup:
		def spec = spec(SampleSpec)

		when:
		modelFactory.createModel(Bean) >> { throw new CreateModelException('') }
		and:
		callInterceptor(spec)

		then:
		thrown(InvalidSpecException)
	}

	def "check field was initialized with corresponding value"() {
		setup:
		def spec = spec(SampleSpec)

		when:
		modelFactory.createModel(Bean) >> model
		and:
		callInterceptor(spec)

		then:
		validateValue(spec)
	}

	def "check feature method was called after model initialization"() {
		when:
		callInterceptor(spec(SampleSpec))

		then:
		1 * invocation.proceed()
	}

	private void validateValue(spec) {
		def modelField = modelFields(spec).findResult {it.name == 'model' ? it : null}

		assert modelField.readValue(target) == model
	}

	private callInterceptor(spec) {
		new ModelCitizenMethodInterceptor(modelFactory, modelFields(spec)).intercept(invocation)
	}
}
