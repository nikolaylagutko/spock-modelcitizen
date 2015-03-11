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
package org.gerzog.spock.modelcitizen.internal.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.trait.Traits
import org.gerzog.spock.modelcitizen.internal.ModelCitizenTrait

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ApplyModelCitizenTraitASTTransformationSpec extends Specification {

	def source = Mock(SourceUnit)

	def spec = Mock(ClassNode)

	def annotation = Mock(AnnotationNode)

	def transformer = new ApplyModelCitizenTraitASTTransformation()

	def "check an interface was added to class"() {
		when:
		transformer.visit([annotation, spec] as ASTNode[], source)

		then:
		1 * spec.addInterface(_)
	}

	def "check an interface added to class is correct trait"() {
		when:
		transformer.visit([annotation, spec] as ASTNode[], source)

		then:
		1 * spec.addInterface {
			Traits.isTrait(it)
			it.typeClass == ModelCitizenTrait
		}
	}

	def "check no interfaces was added if annotation contains disabled flag"() {
		setup:
		annotation.getMember('enableTrait') >> new ConstantExpression(false)

		when:
		transformer.visit([annotation, spec] as ASTNode[], source)

		then:
		0 * spec.addInterface(_)
	}
}
