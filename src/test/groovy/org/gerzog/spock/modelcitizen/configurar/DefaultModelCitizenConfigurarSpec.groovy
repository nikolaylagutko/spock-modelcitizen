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
package org.gerzog.spock.modelcitizen.configurar

import org.gerzog.spock.modelcitizen.api.IModelCitizenBuilder
import org.gerzog.spock.modelcitizen.api.ModelCitizenBlueprints
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class DefaultModelCitizenConfigurarSpec extends Specification {

	final static CLASSES = [BeanBlueprint]

	final static PACKAGES = [
		'org.gerzog.spock.modelcitizen.test.data.blueprints2'
	]

	def builder = Mock(IModelCitizenBuilder)

	def annotation = Mock(ModelCitizenBlueprints)

	def configurar = new DefaultModelCitizenConfigurar()

	def setup() {
		annotation.classes() >> CLASSES
		annotation.packagesToScan() >> PACKAGES
	}

	def "check annotation values was received"() {
		when:
		configurar.configure(annotation, builder)

		then:
		1 * annotation.classes()
		1 * annotation.packagesToScan()
	}

	def "check builder initialized with annotation values"() {
		when:
		configurar.configure(annotation, builder)

		then:
		1 * builder.fromClasses(CLASSES)
		1 * builder.fromPackages(PACKAGES)
	}

	def "check no policies was initialized by default"() {
		when:
		configurar.configure(annotation, builder)

		then:
		0 * builder.withPolicies(_)
	}
}
