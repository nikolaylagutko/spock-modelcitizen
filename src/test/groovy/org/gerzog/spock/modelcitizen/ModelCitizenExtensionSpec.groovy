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

import org.apache.commons.lang.reflect.FieldUtils
import org.gerzog.spock.modelcitizen.api.UseBlueprints
import org.gerzog.spock.modelcitizen.test.TestUtilsTrait
import org.gerzog.spock.modelcitizen.test.data.blueprints1.AnotherBeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints2.ThirdBeanBlueprint
import org.gerzog.spock.modelcitizen.test.specs.ModelWithDef
import org.gerzog.spock.modelcitizen.test.specs.NoModelSpec
import org.gerzog.spock.modelcitizen.test.specs.SampleSpec
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithClasses
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithNoBlueprintClass
import org.gerzog.spock.modelcitizen.test.specs.UseBlueprintsWithPackageScan
import org.spockframework.runtime.InvalidSpecException

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenExtensionSpec extends Specification implements TestUtilsTrait {

	def extension = new ModelCitizenExtension()

	def "check interceptor was added"() {
		setup:
		def spec = spec(UseBlueprintsWithClasses)

		when:
		applyExtension(UseBlueprintsWithClasses, spec)

		then:
		findInterceptor(spec) != null
	}

	@Unroll("check blueprints initialized in model factory for #specClass")
	def "check blueprints initialized in model factory"(specClass, blueprintClasses) {
		setup:
		def spec = spec(specClass)

		when:
		applyExtension(specClass, spec)

		then:
		validateBlueprints(spec, blueprintClasses)

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

		when:
		applyExtension(SampleSpec, spec)

		then:
		FieldUtils.readDeclaredField(findInterceptor(spec), 'fields', true) == fields
	}

	def "check no interceptor added for no @Model-containing spec"() {
		setup:
		def spec = spec(NoModelSpec)

		when:
		applyExtension(NoModelSpec, spec)

		then:
		findInterceptor(spec) == null
	}

	private void validateBlueprints(spec, blueprintClasses) {
		def modelFactory = findModelFactory(spec)

		assert modelFactory.blueprints.size() == blueprintClasses.size()

		blueprintClasses.forEach {
			assert findBlueprint(modelFactory, it) != null
		}
	}

	private findBlueprint(modelFactory, blueprintClass) {
		modelFactory.blueprints.findResult {
			blueprintClass.isInstance(it) ? blueprintClass : null
		}
	}

	private applyExtension(specClass, spec = spec(specClass)) {
		extension.visitSpecAnnotation(extractAnnotation(specClass), spec)
	}

	private extractAnnotation(clazz) {
		clazz.getAnnotation(UseBlueprints)
	}

	private findInterceptor(spec) {
		spec.setupInterceptors.findResult { it instanceof ModelCitizenMethodInterceptor ? it : null }
	}

	private findModelFactory(spec) {
		FieldUtils.readDeclaredField(findInterceptor(spec), 'modelFactory', true)
	}
}
