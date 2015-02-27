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

package com.dataArtisans.flinkCascading.planning.translation;

import cascading.flow.planner.graph.FlowElementGraph;
import cascading.pipe.Merge;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

import java.util.List;

public class MergeOperator extends Operator {

	public MergeOperator(Merge merge, List<Operator> inputOps, FlowElementGraph flowGraph) {
		super(inputOps, merge, merge, flowGraph);
	}

	@Override
	protected DataSet translateToFlink(ExecutionEnvironment env,
										List<DataSet> inputs, List<Operator> inputOps) {

		if(inputs.size() <= 1) {
			throw new RuntimeException("Merge requires at least two inputs");
		}

		DataSet unioned = inputs.get(0);

		for(int i=1; i < inputs.size(); i++) {
			unioned = unioned.union(inputs.get(i));
		}

		return unioned;

	}


}