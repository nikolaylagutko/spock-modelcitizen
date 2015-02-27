/*
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

import org.gerzog.spock.modelcitizen.api.Model
import org.gerzog.spock.modelcitizen.api.UseBlueprints
import org.gerzog.spock.modelcitizen.test.data.blueprints1.AnotherBeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints2.ThirdBeanBlueprint
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithClasses
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithNoBlueprintClass
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithPackageScan
import org.junit.Rule
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.SpecInfoBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@PrepareForTest(ModelCitizenExtension)
class ModelCitizenExtensionSpec extends Specification {

	@Rule
	PowerMockRule powerMockRule = new PowerMockRule()

	def extension = new ModelCitizenExtension()

	def modelFactory = new ModelFactory()

	def "check model factory was created"() {
		setup:
		setupModelFactoryMock()

		when:
		applyExtension(UseBlueprintsWithClasses)

		then:
		PowerMockito.verifyNew(ModelFactory).withNoArguments()
	}

	@Unroll("check blueprints initialized in model factory for #specClass")
	def "check blueprints initialized in model factory"(specClass, blueprintClasses) {
		setup:
		setupModelFactoryMock()

		when:
		applyExtension(specClass)

		then:
		validateBlueprints(blueprintClasses)

		where:
		specClass 						| blueprintClasses
		UseBlueprintsWithClasses 		| [
			AnotherBeanBlueprint,
			ThirdBeanBlueprint
		]
		UseBlueprintsWithPackageScan 	| [
			BeanBlueprint,
			AnotherBeanBlueprint,
			ThirdBeanBlueprint
		]
	}

	def "check an exception thrown for incorrect model factory config"() {
		when:
		applyExtension(UseBlueprintsWithNoBlueprintClass)

		then:
		thrown(InvalidSpecException)
	}

	void validateBlueprints(blueprintClasses) {
		assert modelFactory.blueprints.size() == blueprintClasses.size()

		blueprintClasses.forEach {
			assert findBlueprint(it) != null
		}
	}

	def findBlueprint(blueprintClass) {
		modelFactory.blueprints.findResult {
			blueprintClass.isInstance(it) ? blueprintClass : null
		}
	}

	def setupModelFactoryMock() {
		PowerMockito.whenNew(ModelFactory).withNoArguments().thenReturn(modelFactory)
	}

	def spec(specClass) {
		new SpecInfoBuilder(specClass).build()
	}

	def applyExtension(specClass, spec = spec(specClass)) {
		extension.visitSpecAnnotation(extractAnnotation(specClass), spec)
	}

	def extractAnnotation(clazz) {
		clazz.getAnnotation(UseBlueprints)
	}

	def allFields(spec) {
		spec.allFields
	}

	def modelFields(spec) {
		allFields(spec).filter(isAnnotationPresent(Model))
	}
}
