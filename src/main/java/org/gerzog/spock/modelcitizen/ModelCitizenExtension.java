package org.gerzog.spock.modelcitizen;

import org.gerzog.spock.modelcitizen.api.UseBlueprints;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.SpecInfo;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ModelCitizenExtension extends AbstractAnnotationDrivenExtension<UseBlueprints> {

	@Override
	public void visitSpecAnnotation(final UseBlueprints annotation, final SpecInfo spec) {

	}

}
