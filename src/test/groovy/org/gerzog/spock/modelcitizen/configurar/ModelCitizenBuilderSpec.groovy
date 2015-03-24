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

import org.gerzog.spock.modelcitizen.test.data.Bean
import org.gerzog.spock.modelcitizen.test.data.blueprints1.AnotherBeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints2.ThirdBeanBlueprint
import org.gerzog.spock.modelcitizen.test.specs.TestConstants

import spock.lang.Specification

import com.tobedevoured.modelcitizen.RegisterBlueprintException
import com.tobedevoured.modelcitizen.policy.MappedSingletonPolicy
import com.tobedevoured.modelcitizen.policy.PolicyException
import com.tobedevoured.modelcitizen.policy.SkipReferenceFieldPolicy

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenBuilderSpec extends Specification {

	def builder = new ModelCitizenBuilder()

	def fieldPolicy = new SkipReferenceFieldPolicy('field', Bean)

	def blueprintPolicy = new MappedSingletonPolicy(Bean)

	def failedPolicy = new MappedSingletonPolicy(BeanBlueprint)

	def "check builder do not create new instance"() {
		when:
		def result = builder.fromClasses(BeanBlueprint)

		then:
		result.is builder
	}

	def "check classes initialization"() {
		setup:
		builder.fromClasses(BeanBlueprint)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprints.size == 1
		findBlueprint(result, BeanBlueprint) != null
	}

	def "check multi-from classes initialization"() {
		setup:
		builder.fromClasses(BeanBlueprint)
		builder.fromClasses(AnotherBeanBlueprint)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprints.size == 2
		findBlueprint(result, BeanBlueprint) != null
		findBlueprint(result, AnotherBeanBlueprint) != null
	}

	def "check packages initialization"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_2)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprints.size == 1
		findBlueprint(result, ThirdBeanBlueprint) != null
	}

	def "check multi-from packages initialization"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_1)
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_2)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprints.size == 3
		findBlueprint(result, BeanBlueprint) != null
		findBlueprint(result, AnotherBeanBlueprint) != null
		findBlueprint(result, ThirdBeanBlueprint) != null
	}

	def "check field policy initialization"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_1)
		builder.withPolicies(blueprintPolicy)

		when:
		def result = builder.build()

		then:
		result != null
		result.fieldPolicies.size() == 1
		result.fieldPolicies.get(Bean) != null
	}

	def "check blueprint policy initialization"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_1)
		builder.withPolicies(fieldPolicy)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprintPolicies.size() == 1
		result.blueprintPolicies.get(Bean) != null
	}

	def "check multi-policy appliance"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_1)
		builder.withPolicies(fieldPolicy)
		builder.withPolicies(blueprintPolicy)

		when:
		def result = builder.build()

		then:
		result != null
		result.blueprintPolicies.size() == 1
		result.blueprintPolicies.get(Bean) != null
		result.fieldPolicies.size() == 1
		result.fieldPolicies.get(Bean) != null
	}

	def "check policy exception was thrown on build"() {
		setup:
		builder.fromPackages(TestConstants.BLUEPRINT_PACKAGE_1)
		builder.withPolicies(failedPolicy)

		when:
		builder.build()

		then:
		thrown(PolicyException)
	}

	def "check blueprint exception was thrown on build"() {
		setup:
		builder.fromClasses(Bean)

		when:
		builder.build()

		then:
		thrown(RegisterBlueprintException)
	}

	private findBlueprint(modelFactory, blueprintClass) {
		modelFactory.blueprints.findResult {
			blueprintClass.isInstance(it) ? blueprintClass : null
		}
	}
}
