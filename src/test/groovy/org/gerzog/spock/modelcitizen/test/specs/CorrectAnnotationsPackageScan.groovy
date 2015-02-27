package org.gerzog.spock.modelcitizen.test.specs

import org.gerzog.spock.modelcitizen.api.Model
import org.gerzog.spock.modelcitizen.api.UseBlueprints
import org.gerzog.spock.modelcitizen.test.data.Bean

import spock.lang.Specification


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@UseBlueprints(packagesToScan = 'org.gerzog.spock.modelcitizen.test.data')
class CorrectAnnotationsPackageScan extends Specification {

	@Model
	Bean model
}
