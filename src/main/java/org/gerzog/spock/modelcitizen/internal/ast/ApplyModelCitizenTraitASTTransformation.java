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
package org.gerzog.spock.modelcitizen.internal.ast;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.gerzog.spock.modelcitizen.internal.ModelCitizenTrait;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ApplyModelCitizenTraitASTTransformation extends AbstractASTTransformation {

	private static final String FLAG_FIELD = "enableTrait";

	private static final ClassNode TRAIT_CLASS_NODE = ClassHelper.makeWithoutCaching(ModelCitizenTrait.class);

	@Override
	public void visit(final ASTNode[] nodes, final SourceUnit source) {
		final AnnotationNode annotation = (AnnotationNode) nodes[0];
		final ClassNode parent = (ClassNode) nodes[1];

		if (isEnabled(annotation)) {
			parent.addInterface(TRAIT_CLASS_NODE);
		}
	}

	private boolean isEnabled(final AnnotationNode annotation) {
		final String flagValue = getMemberStringValue(annotation, FLAG_FIELD);
		return (flagValue == null) || Boolean.parseBoolean(flagValue);
	}
}
