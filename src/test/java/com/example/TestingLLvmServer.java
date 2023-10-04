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

import io.trino.testing.ResourcePresence;
import io.trino.util.AutoCloseableCloser;
import org.testcontainers.containers.Network;

import static org.testcontainers.containers.Network.newNetwork;

public class TestingLLvmServer
        implements AutoCloseable
{
    private final AutoCloseableCloser closer = AutoCloseableCloser.create();

    private boolean isRunning;

    public TestingLLvmServer()
    {
        isRunning = true;
    }

    @Override
    public void close()
            throws Exception
    {
        closer.close();
        isRunning = false;
    }

    @ResourcePresence
    public boolean isRunning()
    {
        return this.isRunning;
    }
}
