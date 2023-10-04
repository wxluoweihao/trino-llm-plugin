/*
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
package com.example.ptf;

import com.example.LLvmClient;
import com.example.LLvmColumnHandle;
import com.example.LLvmTable;
import com.example.LLvmTableHandle;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.airlift.slice.Slice;
import io.trino.spi.connector.ConnectorAccessControl;
import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.connector.ConnectorTableHandle;
import io.trino.spi.connector.ConnectorTransactionHandle;
import io.trino.spi.function.table.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.LLvmSplit.Mode.TABLE;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.trino.spi.function.table.ReturnTypeSpecification.GenericTable.GENERIC_TABLE;
import static io.trino.spi.type.VarcharType.VARCHAR;
import static java.util.Objects.requireNonNull;

public class ReadFileTableFunction
        implements Provider<ConnectorTableFunction>
{
    private final LLvmClient llvmClient;

    @Inject
    public ReadFileTableFunction(LLvmClient llvmClient)
    {
        this.llvmClient = requireNonNull(llvmClient, "llvmClient is null");
    }

    @Override
    public ConnectorTableFunction get()
    {
        return new QueryFunction(llvmClient);
    }

    public static class QueryFunction
            extends AbstractConnectorTableFunction
    {
        private final LLvmClient llvmClient;

        public QueryFunction(LLvmClient llvmClient)
        {
            super(
                    "system",
                    "read_file",
                    ImmutableList.of(
                            ScalarArgumentSpecification.builder()
                                    .name("TYPE")
                                    .type(VARCHAR)
                                    .build(),
                            ScalarArgumentSpecification.builder()
                                    .name("PATH")
                                    .type(VARCHAR)
                                    .build()),
                    GENERIC_TABLE);
            this.llvmClient = requireNonNull(llvmClient, "llvmClient is null");
        }

        @Override
        public TableFunctionAnalysis analyze(ConnectorSession session, ConnectorTransactionHandle transaction, Map<String, Argument> arguments, ConnectorAccessControl accessControl)
        {
            String type = ((Slice) ((ScalarArgument) arguments.get("TYPE")).getValue()).toStringUtf8();
            String path = ((Slice) ((ScalarArgument) arguments.get("PATH")).getValue()).toStringUtf8();

            LLvmTable table = llvmClient.getTable(session, type, path);

            Descriptor returnedType = new Descriptor(table.getColumns().stream()
                    .map(column -> new Descriptor.Field(column.getName(), Optional.of(column.getType())))
                    .collect(toImmutableList()));

            ReadFunctionHandle handle = new ReadFunctionHandle(new LLvmTableHandle(TABLE, type, path), table.getColumns());

            return TableFunctionAnalysis.builder()
                    .returnedType(returnedType)
                    .handle(handle)
                    .build();
        }
    }

    public static class ReadFunctionHandle
            implements ConnectorTableFunctionHandle
    {
        private final LLvmTableHandle tableHandle;
        private final List<LLvmColumnHandle> columns;

        @JsonCreator
        public ReadFunctionHandle(
                @JsonProperty("tableHandle") LLvmTableHandle tableHandle,
                @JsonProperty("columns") List<LLvmColumnHandle> columns)
        {
            this.tableHandle = requireNonNull(tableHandle, "tableHandle is null");
            this.columns = ImmutableList.copyOf(requireNonNull(columns, "columns is null"));
        }

        @JsonProperty
        public ConnectorTableHandle getTableHandle()
        {
            return tableHandle;
        }

        @JsonProperty
        public List<LLvmColumnHandle> getColumns()
        {
            return columns;
        }
    }
}
