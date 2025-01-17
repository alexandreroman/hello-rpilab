/*
 * Copyright (c) 2025 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.rpilab.hello.hardware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

@Service
public class HardwareService {
    private final Logger logger = LoggerFactory.getLogger(HardwareService.class);
    private Optional<String> cpuModel;

    public Optional<String> getCpuModel() {
        if (cpuModel == null) {
            cpuModel = loadCpuModel();
        }
        return cpuModel;
    }

    private Optional<String> loadCpuModel() {
        logger.info("Reading CPU model");
        final var cpuInfoFile = Path.of("/proc/cpuinfo");
        if (Files.isReadable(cpuInfoFile)) {
            final Properties p = new Properties();
            try (final var in = new FileInputStream(cpuInfoFile.toFile())) {
                p.load(in);
            } catch (IOException e) {
                logger.warn("Failed to read /proc/cpuinfo", e);
            }
            return Optional.ofNullable(p.getProperty("Model"));
        }
        return Optional.empty();
    }
}
