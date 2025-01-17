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

package dev.rpilab.hello.index;

import dev.rpilab.hello.date.DateService;
import dev.rpilab.hello.fact.FactService;
import dev.rpilab.hello.hardware.HardwareService;
import dev.rpilab.hello.weather.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
class IndexController {
    private final String serverHostname;
    private final WeatherService ws;
    private final FactService fs;
    private final HardwareService hs;
    private final DateService ds;

    IndexController(WeatherService ws, FactService fs, HardwareService hs, DateService ds,
                    @Value("${app.info.server.hostname}") String serverHostName) {
        this.ws = ws;
        this.fs = fs;
        this.hs = hs;
        this.ds = ds;
        this.serverHostname = serverHostName;
    }

    @GetMapping("/")
    String index(Model model) throws UnknownHostException {
        model.addAttribute("serverHostname", serverHostname);
        model.addAttribute("springBootVersion", SpringBootVersion.getVersion());
        model.addAttribute("podHostname", InetAddress.getLocalHost().getHostName());
        model.addAttribute("serverType", hs.getCpuModel().orElse("Dev Machine"));
        model.addAttribute("currentDate", ds.getLocalDate());
        return "index";
    }

    @GetMapping("/motd")
    String motd(Model model) {
        model.addAttribute("motd", fs.getFact().fact());
        return "fragments/motd";
    }

    @GetMapping("/weather")
    String weather(Model model) {
        model.addAttribute("weather", ws.getCurrent());
        return "fragments/weather";
    }
}
