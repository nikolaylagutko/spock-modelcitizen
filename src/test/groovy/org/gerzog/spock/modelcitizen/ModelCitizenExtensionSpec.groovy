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
