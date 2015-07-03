/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dataArtisans.flinkCascading.planning.rules;

import cascading.flow.planner.iso.expression.ElementCapture;
import cascading.flow.planner.iso.expression.ExpressionGraph;
import cascading.flow.planner.iso.expression.FlowElementExpression;
import cascading.flow.planner.iso.finder.SearchOrder;
import cascading.flow.planner.iso.transformer.InsertionGraphTransformer;
import cascading.flow.planner.rule.RuleExpression;
import cascading.flow.planner.rule.transformer.RuleInsertionTransformer;
import cascading.pipe.HashJoin;

import static cascading.flow.planner.rule.PlanPhase.BalanceAssembly;

/**
 * Injects a Boundary before a CoGroup in order to split of the CoGroup as a separate node
 * and have all predecessors in individual pipes.
 */
public class BoundaryAfterHashJoinTransformer extends RuleInsertionTransformer
{
	public BoundaryAfterHashJoinTransformer() {
		super(
				BalanceAssembly,
				new HashJoinMatcher(),
				BoundaryElementFactory.BOUNDARY_FACTORY,
				InsertionGraphTransformer.Insertion.After
		);
	}

	public static class HashJoinMatcher extends RuleExpression
	{
		public HashJoinMatcher()
		{
			super( new HashJoinGraph() );
		}
	}

	public static class HashJoinGraph extends ExpressionGraph {

		public HashJoinGraph() {

			super(SearchOrder.ReverseTopological, new FlowElementExpression(ElementCapture.Primary, HashJoin.class));

		}
	}

}
