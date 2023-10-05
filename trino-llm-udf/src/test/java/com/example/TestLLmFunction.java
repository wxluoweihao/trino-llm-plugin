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

package com.example;

import com.google.common.collect.ImmutableMap;
import io.trino.testing.AbstractTestQueryFramework;
import io.trino.testing.QueryRunner;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Optional;

import static com.example.LLmQueryRunner.createLLmQueryRunner;

public class TestLLmFunction extends AbstractTestQueryFramework
{
    private TestingLLmServer server;

    @Override
    protected QueryRunner createQueryRunner()
            throws Exception
    {
        server = closeAfterClass(new TestingLLmServer());
        return createLLmQueryRunner(Optional.of(server), ImmutableMap.of(), ImmutableMap.of());
    }

    @Test
    public void shouldPass1()
    {
        assertQuerySucceeds("SELECT * FROM (VALUES ask_llm('1+2=?'))");
    }

    @Test
    public void shouldPass2()
    {
        assertQuerySucceeds("SELECT * FROM (VALUES ask_llm_data('what is the total of these data {%s} ?', ARRAY['1', '2', '3', '4']))");
    }

    @Test
    public void shouldPass3()
    {
        String testFile = TestLLmFunction.class.getClassLoader().getResource("example-data/users.csv").getPath();
        assertQuerySucceeds(
                "SELECT ask_llm_data('Data inside this array {%s} " +
                String.format(
                        "is order by height and age, do you this height and age is normal ? " +
                        "response yes or no only.', ARRAY[height, age]) " +
                        "from llm.openai.\"%s\"",
                        testFile
                )
        );
    }

    @Test
    public void shouldPass4()
    {
        assertQuerySucceeds("SELECT * FROM (VALUES ask_llm('This girl is horrible, return positive or negative'))");
    }


    @Test
    public void shouldPass5()
    {
        String testFile = TestLLmFunction.class.getClassLoader().getResource("example-data/car-news.pdf").getPath();
        assertQuerySucceeds(
                "SELECT ask_llm_data('what are the sentiments in the data: {%s}', ARRAY[pdf_content]) " +
                        String.format(
                                "from llm.openai.\"%s\"",
                                testFile
                        )
        );
    }
}
