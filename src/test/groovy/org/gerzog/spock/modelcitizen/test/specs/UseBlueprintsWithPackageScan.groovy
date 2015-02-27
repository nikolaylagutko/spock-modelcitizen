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
package org.gerzog.spock.modelcitizen.test.specs

import org.gerzog.spock.modelcitizen.api.Model
import org.gerzog.spock.modelcitizen.api.UseBlueprints
import org.gerzog.spock.modelcitizen.test.data.Bean

import spock.lang.Specification


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@UseBlueprints(packagesToScan = ['org.gerzog.spock.modelcitizen.test.data.blueprints1', 'org.gerzog.spock.modelcitizen.test.data.blueprints2'])
class UseBlueprintsWithPackageScan extends Specification {

	@Model
	Bean model
}