/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.engine.server.checkpoint;

import org.apache.seatunnel.api.table.factory.FactoryUtil;
import org.apache.seatunnel.engine.checkpoint.storage.PipelineState;
import org.apache.seatunnel.engine.checkpoint.storage.api.CheckpointStorage;
import org.apache.seatunnel.engine.checkpoint.storage.api.CheckpointStorageFactory;
import org.apache.seatunnel.engine.checkpoint.storage.common.ProtoStuffSerializer;
import org.apache.seatunnel.engine.checkpoint.storage.exception.CheckpointStorageException;
import org.apache.seatunnel.engine.core.checkpoint.CheckpointType;
import org.apache.seatunnel.engine.core.job.JobStatus;
import org.apache.seatunnel.engine.server.AbstractSeaTunnelServerTest;

import com.hazelcast.map.IMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DisabledOnOs(OS.WINDOWS)
public class CheckpointManagerTest extends AbstractSeaTunnelServerTest {

    @Test
    public void testHAByIMapCheckpointIDCounter() throws CheckpointStorageException {
        long jobId = (long) (Math.random() * 1000000L);
        CheckpointStorage checkpointStorage = FactoryUtil.discoverFactory(Thread.currentThread().getContextClassLoader(), CheckpointStorageFactory.class,
                CheckpointStorageConfiguration.builder().build().getStorage())
            .create(new HashMap<>());
        CompletedCheckpoint completedCheckpoint = new CompletedCheckpoint(jobId, 1, 1,
            Instant.now().toEpochMilli(),
            CheckpointType.COMPLETED_POINT_TYPE,
            Instant.now().toEpochMilli(),
            new HashMap<>(),
            new HashMap<>());
        checkpointStorage.storeCheckPoint(PipelineState.builder().jobId(jobId + "").pipelineId(1).checkpointId(1)
            .states(new ProtoStuffSerializer().serialize(completedCheckpoint)).build());
        IMap<Integer, Long> checkpointIdMap = nodeEngine.getHazelcastInstance().getMap("checkpoint-id-" + jobId);
        checkpointIdMap.put(1, 2L);
        Map<Integer, CheckpointPlan> planMap = new HashMap<>();
        planMap.put(1, CheckpointPlan.builder().pipelineId(1).build());
        CheckpointManager checkpointManager = new CheckpointManager(
            jobId,
            nodeEngine,
            planMap,
            CheckpointCoordinatorConfiguration.builder().build(),
            CheckpointStorageConfiguration.builder().build());
        Assertions.assertTrue(checkpointManager.isCompletedPipeline(1));
        CompletableFuture<Void> future = checkpointManager.listenPipeline(1, org.apache.seatunnel.engine.core.job.PipelineState.FINISHED);
        future.join();
        Assertions.assertNull(checkpointIdMap.get(1));
        future = checkpointManager.shutdown(JobStatus.FINISHED);
        future.join();
        Assertions.assertTrue(checkpointStorage.getAllCheckpoints(jobId + "").isEmpty());
    }
}
