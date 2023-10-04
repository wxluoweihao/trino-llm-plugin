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
import io.trino.testing.DistributedQueryRunner;
import io.trino.testing.MaterializedResultWithQueryId;
import io.trino.testing.QueryRunner;
import org.testng.annotations.Test;

import java.io.File;
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
    public void testCsvConnector() {
        String testFile = getClass().getClassLoader().getResource("example-data" + File.separator + "numbers-2.csv").getPath();
        assertQuery(String.format("SELECT * FROM llm.openai.\"%s\"", testFile), "VALUES ('eleven', '11'), ('twelve', '12')");
    }

    @Test
    public void testCsvTableFunc()
    {
        String testFile = getClass().getClassLoader().getResource("example-data" + File.separator + "numbers-2.csv").getPath();
        assertQuery(
                String.format("SELECT * FROM TABLE(llm.system.read_file('openai', '%s'))", testFile),
                "VALUES ('eleven', '11'), ('twelve', '12')");
    }

    @Test
    public void testPdfConnector() {
        String data = "This is a test";
        String testFile = getClass().getClassLoader().getResource("example-data/test.pdf").getPath();
        assertQuery(
                String.format("SELECT * FROM llm.openai.\"%s\"", testFile),
                String.format("VALUES ('%s')", data)
        );
    }

    @Test
    public void testPdfFunc()
    {
        String data = "This is a test";
        String testFile = getClass().getClassLoader().getResource("example-data/test.pdf").getPath();
        assertQuery(
                String.format("SELECT * FROM TABLE(llm.system.read_file('openai', '%s'))", testFile),
                String.format("VALUES ('%s')", data)
        );
    }

    private static String toAbsolutePath(String resourceName)
    {
        return Resources.getResource(resourceName).toString();
    }
}
