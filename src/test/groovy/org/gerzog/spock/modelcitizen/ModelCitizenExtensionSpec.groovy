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

import org.gerzog.spock.modelcitizen.api.UseBlueprints
import org.gerzog.spock.modelcitizen.test.TestUtilsTrait
import org.gerzog.spock.modelcitizen.test.data.blueprints1.AnotherBeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints2.ThirdBeanBlueprint
import org.gerzog.spock.modelcitizen.test.specs.ModelWithDef
import org.gerzog.spock.modelcitizen.test.specs.SampleSpec
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithClasses
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithNoBlueprintClass
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithPackageScan
import org.junit.Rule
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import org.spockframework.runtime.InvalidSpecException

import spock.lang.Specification
import spock.lang.Unroll

import com.tobedevoured.modelcitizen.ModelFactory

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@PrepareForTest(ModelCitizenExtension)
class ModelCitizenExtensionSpec extends Specification implements TestUtilsTrait {

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

	def "check an error occured for @Model applied to 'def' field"() {
		when:
		applyExtension(ModelWithDef)

		then:
		thrown(InvalidSpecException)
	}

	def "check method interceptor was applied"(){
		setup:
		def spec = spec(SampleSpec)
		def fields = modelFields(spec)

		setupModelFactoryMock()
		setupInterceptorMock(fields)

		when:
		applyExtension(SampleSpec, spec)

		then:
		PowerMockito.verifyNew(ModelCitizenMethodInterceptor).withArguments(modelFactory, fields)
	}

	private void validateBlueprints(blueprintClasses) {
		assert modelFactory.blueprints.size() == blueprintClasses.size()

		blueprintClasses.forEach {
			assert findBlueprint(it) != null
		}
	}

	private findBlueprint(blueprintClass) {
		modelFactory.blueprints.findResult {
			blueprintClass.isInstance(it) ? blueprintClass : null
		}
	}

	private setupInterceptorMock(fields) {
		PowerMockito.whenNew(ModelCitizenMethodInterceptor).withArguments(modelFactory, fields).thenReturn(new ModelCitizenMethodInterceptor(modelFactory, fields))
	}

	private setupModelFactoryMock() {
		PowerMockito.whenNew(ModelFactory).withNoArguments().thenReturn(modelFactory)
	}

	private applyExtension(specClass, spec = spec(specClass)) {
		extension.visitSpecAnnotation(extractAnnotation(specClass), spec)
	}

	private extractAnnotation(clazz) {
		clazz.getAnnotation(UseBlueprints)
	}
}
