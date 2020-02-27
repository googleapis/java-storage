/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

import com.google.api.services.storage.model.Expr;
import com.google.api.services.storage.model.Policy.Bindings;
import com.google.cloud.Binding;
import com.google.cloud.Condition;
import com.google.cloud.Policy;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for converting between the Policy model provided by the API and the Policy model provided
 * by this library.
 */
class PolicyHelper {

  static Policy convertFromApiPolicy(com.google.api.services.storage.model.Policy apiPolicy) {
    Policy.Builder policyBuilder = Policy.newBuilder();
    List<Bindings> bindings = apiPolicy.getBindings();
    if (null != bindings && !bindings.isEmpty()) {
      ImmutableList.Builder<Binding> coreBindings = ImmutableList.builder();
      for (Bindings binding : bindings) {
        Binding.Builder bindingBuilder = Binding.newBuilder();
        bindingBuilder.setRole(binding.getRole());
        bindingBuilder.setMembers(binding.getMembers());
        if (binding.getCondition() != null) {
          Condition.Builder conditionBuilder = Condition.newBuilder();
          conditionBuilder.setTitle(binding.getCondition().getTitle());
          conditionBuilder.setDescription(binding.getCondition().getDescription());
          conditionBuilder.setExpression(binding.getCondition().getExpression());
          bindingBuilder.setCondition(conditionBuilder.build());
        }
        coreBindings.add(bindingBuilder.build());
      }
      policyBuilder.setBindings(coreBindings.build());
    } else {
      throw new IllegalStateException("Missing required bindings.");
    }
    return policyBuilder.setEtag(apiPolicy.getEtag()).setVersion(apiPolicy.getVersion()).build();
  }

  static com.google.api.services.storage.model.Policy convertToApiPolicy(Policy policy) {
    List<Bindings> bindings = new ArrayList<>(policy.getBindingsList().size());
    for (Binding binding : policy.getBindingsList()) {
      Bindings apiBinding = new Bindings();
      apiBinding.setRole(binding.getRole());
      apiBinding.setMembers(new ArrayList<>(binding.getMembers()));
      if (binding.getCondition() != null) {
        Expr expr = new Expr();
        expr.setTitle(binding.getCondition().getTitle());
        expr.setDescription(binding.getCondition().getDescription());
        expr.setExpression(binding.getCondition().getExpression());
        apiBinding.setCondition(expr);
      }
      bindings.add(apiBinding);
    }
    return new com.google.api.services.storage.model.Policy()
        .setBindings(bindings)
        .setEtag(policy.getEtag())
        .setVersion(policy.getVersion());
  }

  private PolicyHelper() {
    // Intentionally left blank.
  }
}
