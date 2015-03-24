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

import org.gerzog.spock.modelcitizen.api.IModelCitizenBuilder
import org.gerzog.spock.modelcitizen.api.IModelCitizenConfigurar
import org.gerzog.spock.modelcitizen.api.Model
import org.gerzog.spock.modelcitizen.api.ModelCitizenBlueprints
import org.gerzog.spock.modelcitizen.test.data.Bean

import spock.lang.Specification

import com.tobedevoured.modelcitizen.policy.PolicyException


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@ModelCitizenBlueprints(configurar = CustomConfigurar)
class UncreatableConfigurarSpec extends Specification {

	static class CustomConfigurar implements IModelCitizenConfigurar {

		public CustomConfigurar(Object parameter) {
		}

		@Override
		void configure(ModelCitizenBlueprints annotation, IModelCitizenBuilder builder) {
			throw new PolicyException('')
		}
	}

	@Model
	Bean model

	Bean notModel
}
