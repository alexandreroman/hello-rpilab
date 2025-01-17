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

package dev.rpilab.hello.weather;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

import java.time.ZonedDateTime;

class WeatherNativeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        try {
            hints.reflection().registerType(Weather.class);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(Weather.class, "type"), ExecutableMode.INVOKE);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(Weather.class, "updatedAt"), ExecutableMode.INVOKE);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(Weather.class, "location"), ExecutableMode.INVOKE);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(Weather.class, "temperature"), ExecutableMode.INVOKE);
            hints.reflection().registerConstructor(ReflectionUtils.accessibleConstructor(Weather.class, String.class, ZonedDateTime.class, WeatherType.class, Double.TYPE), ExecutableMode.INVOKE);

            hints.reflection().registerType(WeatherType.class);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(WeatherType.class, "name"), ExecutableMode.INVOKE);

            hints.reflection().registerType(WeatherUtils.class);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(WeatherUtils.class, "formatTemperatureCelsius", Double.TYPE), ExecutableMode.INVOKE);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to configure Weather service with native image support", e);
        }
    }
}
