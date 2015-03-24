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
package org.gerzog.spock.modelcitizen.internal

import org.gerzog.spock.modelcitizen.test.data.Bean
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint

import spock.lang.Specification
import spock.lang.Unroll

import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenTraitSpec extends Specification {

	final static FIELD_VALUE = 'value'

	def modelFactory = Mock(ModelFactory)

	def modelCitizenTrait = new Object() as ModelCitizenTrait

	def setup() {
		modelCitizenTrait.modelFactory = modelFactory
	}

	@Unroll
	def "check a model factory's createModel was called"(def template) {
		when:
		modelCitizenTrait.model(template)

		then:
		1 * modelFactory.createModel(template)

		where:
		template << [Object, new Object()]
	}

	def "check model creating with field overwriting"() {
		setup:
		def instance = new Bean()
		modelFactory.createModel(_) >> instance

		when:
		modelCitizenTrait.model(BeanBlueprint, [ property:FIELD_VALUE ])

		then:
		instance.property == FIELD_VALUE
	}

	def "check null is not causing error"() {
		when:
		modelCitizenTrait.model(BeanBlueprint, null)

		then:
		noExceptionThrown()
	}
}
