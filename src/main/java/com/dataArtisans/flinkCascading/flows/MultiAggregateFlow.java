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

package com.dataArtisans.flinkCascading.flows;

import cascading.flow.FlowDef;
import cascading.flow.FlowProcess;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.operation.aggregator.Count;
import cascading.operation.aggregator.Sum;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class MultiAggregateFlow {

	public static FlowDef getFlow() {

		Fields token = new Fields( "token" );
		Fields text = new Fields( "line" );
		Fields offset = new Fields( "offset" );
		Fields num = new Fields( "num" );

		RegexSplitGenerator splitter = new RegexSplitGenerator( token, "[ \\[\\]\\(\\),.]" );
		// only returns "token"
		Pipe docPipe = new Each( "token", text, splitter, Fields.ALL );

		Pipe wcPipe = new Pipe( "wc", docPipe );
		wcPipe = new GroupBy( wcPipe, token );
//		wcPipe = new Each( wcPipe, num, new DoubleFunc(offset), Fields.ALL);
		wcPipe = new Every( wcPipe, Fields.ALL, new Count(), Fields.ALL );
		wcPipe = new Every( wcPipe, num, new Sum(new Fields("num")), Fields.ALL );
		wcPipe = new Each( wcPipe, num, new DoubleFunc(offset), Fields.ALL);
//		wcPipe = new Every( wcPipe, offset, new Sum(new Fields("secondSum")), Fields.ALL );

		return FlowDef.flowDef().setName( "wc" )
				.addTails(wcPipe);
	}

	public static class DoubleFunc implements Function {

		Fields fields;

		public DoubleFunc(Fields f) {
			fields = f;
		}

		@Override
		public void operate(FlowProcess flowProcess, FunctionCall functionCall) {
			TupleEntry x = functionCall.getArguments();
			long newNum = x.getLong(0)*2;
			Tuple t = new Tuple(newNum);
			functionCall.getOutputCollector().add(t);
		}

		@Override
		public void prepare(FlowProcess flowProcess, OperationCall operationCall) {

		}

		@Override
		public void flush(FlowProcess flowProcess, OperationCall operationCall) {

		}

		@Override
		public void cleanup(FlowProcess flowProcess, OperationCall operationCall) {

		}

		@Override
		public Fields getFieldDeclaration() {
			return this.fields;
		}

		@Override
		public int getNumArgs() {
			return 1;
		}

		@Override
		public boolean isSafe() {
			return false;
		}
	}

}
