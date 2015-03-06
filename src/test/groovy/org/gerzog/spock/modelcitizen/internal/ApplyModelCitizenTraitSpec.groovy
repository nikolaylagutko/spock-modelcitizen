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

import spock.lang.Specification
import spock.util.EmbeddedSpecCompiler

import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ApplyModelCitizenTraitSpec extends Specification {

	def spec = """
		@ApplyModelCitizenTrait
		class TraitedSpec extends Specification {
		}
		"""

	EmbeddedSpecCompiler compiler = new EmbeddedSpecCompiler()

	def modelFactory = Mock(ModelFactory)

	def setup() {
		compiler.addClassImport(ApplyModelCitizenTrait)
		compiler.addClassImport(Specification)
	}

	def "check a trait was added for spec"() {
		when:
		def result = compile()

		then:
		ModelCitizenTrait.isAssignableFrom(result)
	}

	def "check no error during spec compilation with ast transformation"() {
		when:
		compile()

		then:
		noExceptionThrown()
	}

	def "check delegation works"() {
		setup:
		def clazz = compile()
		def instance = clazz.newInstance(modelFactory: modelFactory)

		when:
		instance.model(new Object())

		then:
		1 * modelFactory.createModel(_)
	}

	private compile() {
		compiler.compile(spec).first()
	}
}
