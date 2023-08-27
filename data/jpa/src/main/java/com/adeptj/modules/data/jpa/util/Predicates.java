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

package com.adeptj.modules.data.jpa.util;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.criteria.BaseCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Utilities for Jpa {@link Predicate}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class Predicates {

    private static final Predicate[] NO_RESTRICTIONS = new Predicate[0];

    private Predicates() {
    }

    public static <T extends BaseEntity> Predicate[] from(@NotNull CriteriaBuilder cb,
                                                          @NotNull Root<T> root,
                                                          @NotNull BaseCriteria<T> criteria) {
        Map<String, Object> criteriaAttributes = criteria.getCriteriaAttributes();
        if (criteriaAttributes == null || criteriaAttributes.isEmpty()) {
            return NO_RESTRICTIONS;
        }
        return criteriaAttributes
                .entrySet()
                .stream()
                .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .toArray(Predicate[]::new);
    }
}
