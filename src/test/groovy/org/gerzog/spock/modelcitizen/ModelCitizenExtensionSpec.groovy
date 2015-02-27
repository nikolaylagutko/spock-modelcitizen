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
import org.gerzog.spock.modelcitizen.test.specs.CorrectAnnotationsPackageScan
import org.spockframework.runtime.SpecInfoBuilder

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ModelCitizenExtensionSpec extends Specification {

	def extension = new ModelCitizenExtension()

	def "check actions on annotation applying"() {
		setup:
		def spec = spec(CorrectAnnotationsPackageScan)

		when:
		applyExtension(CorrectAnnotationsPackageScan, spec)

		then:
		validateSpec()
	}

	void validateSpec() {
	}

	def spec(specClass) {
		new SpecInfoBuilder(specClass).build()
	}

	def applyExtension(specClass, spec) {
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
