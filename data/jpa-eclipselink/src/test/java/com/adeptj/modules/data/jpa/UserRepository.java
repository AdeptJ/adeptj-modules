/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.data.jpa;

import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.entity.User;
import com.adeptj.modules.data.jpa.util.JpaUtil;

import java.util.List;

/**
 * Basic {@link User} repository.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class UserRepository extends AbstractJpaRepository<User, Long> {

    void closeEntityManagerFactory() {
        JpaUtil.closeEntityManagerFactory(this.getEntityManagerFactory());
    }

    List<User> findAllUsers() {
        return super.doWithEntityManager(em -> em.createQuery("Select u from User u", User.class)
                        .getResultList(),
                false);
    }
}
