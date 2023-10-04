package com.example;/*
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

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import io.trino.testing.AbstractTestQueryFramework;
import io.trino.testing.QueryRunner;
import org.testng.annotations.Test;

import java.util.Optional;

import static com.example.LLmQueryRunner.createLLmQueryRunner;
public final class TestLLmConnector
        extends AbstractTestQueryFramework
{
    private TestingLLmServer server;

    @Override
    protected QueryRunner createQueryRunner()
            throws Exception
    {
        server = closeAfterClass(new TestingLLmServer());
        return createLLmQueryRunner(Optional.of(server),ImmutableMap.of(), ImmutableMap.of());
    }

    @Test
    public void testSimple() {
        assertQuery("SELECT * FROM llm.openai." + "\"E:/projects/trino-llvm/src/test/resources/example-data/numbers-2.csv\"", "VALUES ('eleven', '11'), ('twelve', '12')");
    }

    @Test
    public void testSelectCsv()
    {
        assertQuery(
                "SELECT * FROM TABLE(llm.system.read_file('openai', '" + "E:/projects/trino-llvm/src/test/resources/example-data/numbers-2.csv" + "'))",
                "VALUES ('eleven', '11'), ('twelve', '12')");
    }

    private static String toAbsolutePath(String resourceName)
    {
        return Resources.getResource(resourceName).toString();
    }
}
