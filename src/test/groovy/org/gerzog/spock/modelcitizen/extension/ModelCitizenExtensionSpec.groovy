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
package org.gerzog.spock.modelcitizen.extension

import org.apache.commons.lang.reflect.FieldUtils
import org.gerzog.spock.modelcitizen.test.SpecCompilationTrait
import org.gerzog.spock.modelcitizen.test.TestUtilsTrait
import org.gerzog.spock.modelcitizen.test.data.blueprints1.AnotherBeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints1.BeanBlueprint
import org.gerzog.spock.modelcitizen.test.data.blueprints2.ThirdBeanBlueprint
import org.gerzog.spock.modelcitizen.test.specs.TestSpecs
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IMethodInterceptor

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenExtensionSpec extends Specification implements TestUtilsTrait, SpecCompilationTrait {

	def extension = new ModelCitizenExtension()

	def "check interceptor was added"() {
		setup:
		def specClass = compileSpec(TestSpecs.USE_BLUEPRINTS_WITH_CLASSES)
		def spec = spec(specClass)

		when:
		applyExtension(specClass, spec)

		then:
		findModelAnnotationInterceptor(spec) != null
	}

	@Unroll('check blueprints initialized in model factory for #specClassName')
	def "check blueprints initialized in model factory"(specClassName, blueprintClasses) {
		setup:
		def specClass = compileSpec(specClassName)
		def spec = spec(specClass)

		when:
		applyExtension(specClass, spec)

		then:
		validateBlueprints(spec, blueprintClasses)

		where:
		specClassName 						| blueprintClasses
		TestSpecs.USE_BLUEPRINTS_WITH_CLASSES 		| [
			AnotherBeanBlueprint,
			ThirdBeanBlueprint
		]
		TestSpecs.USE_BLUEPRINTS_WITH_PACKAGE_SCAN	| [
			BeanBlueprint,
			AnotherBeanBlueprint,
			ThirdBeanBlueprint
		]
	}

	def "check an exception thrown for incorrect model factory config"() {
		when:
		def specClass = compileSpec(TestSpecs.USE_BLUEPRINTS_WITH_NO_BLUEPRINT_CLASS)
		applyExtension(specClass)

		then:
		thrown(InvalidSpecException)
	}

	def "check an error occured for @Model applied to 'def' field"() {
		when:
		def specClass = compileSpec(TestSpecs.MODEL_WITH_DEF)
		applyExtension(specClass)

		then:
		thrown(InvalidSpecException)
	}

	def "check method interceptor was applied"() {
		setup:
		def specClass = compileSpec(TestSpecs.SAMPLE_SPEC)
		def spec = spec(specClass)
		def fields = modelFields(spec)

		when:
		applyExtension(specClass, spec)

		then:
		FieldUtils.readDeclaredField(findModelAnnotationInterceptor(spec), 'fields', true) == fields
	}

	def "check no interceptor added for no @Model-containing spec"() {
		setup:
		def specClass = compileSpec(TestSpecs.NO_MODEL_SPEC)
		def spec = spec(specClass)

		when:
		applyExtension(specClass, spec)

		then:
		findModelAnnotationInterceptor(spec) == null
	}

	def "check trait initializer was registered"() {
		setup:
		def specClass = compileSpec(TestSpecs.NO_MODEL_SPEC)
		def spec = spec(specClass)

		when:
		applyExtension(specClass, spec)

		then:
		findTraitInitializerInterceptor(spec) != null
	}

	def "check trait initializer is first"() {
		setup:
		def specClass = compileSpec(TestSpecs.NO_MODEL_SPEC)
		def spec = spec(specClass)
		spec.initializerInterceptors << [Mock(IMethodInterceptor)]

		when:
		applyExtension(specClass, spec)

		then:
		spec.initializerInterceptors[0] instanceof ModelCitizenTraitInitializer
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

	private findModelAnnotationInterceptor(spec) {
		spec.setupInterceptors.findResult { it instanceof ModelCitizenMethodInterceptor ? it : null }
	}

	private findTraitInitializerInterceptor(spec) {
		spec.initializerInterceptors.findResult { it instanceof ModelCitizenTraitInitializer ? it : null }
	}

	private findModelFactory(spec) {
		FieldUtils.readField(findModelAnnotationInterceptor(spec), 'modelFactory', true)
	}
}
