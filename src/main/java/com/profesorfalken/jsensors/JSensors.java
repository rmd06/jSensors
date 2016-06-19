/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jsensors;

import com.profesorfalken.jsensors.model.components.Component;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Javier Garcia Alonso
 */
public enum JSensors {

    get;

    private static final Logger LOGGER = LoggerFactory.getLogger(JSensors.class);

    final Map<String, String> baseConfig;

    Map<String, String> usedConfig = null;

    private JSensors() {
        //Load config from file
        baseConfig = SensorsConfig.getConfigMap();
    }

    public JSensors config(Map<String, String> config) {
        //Override config
        this.usedConfig = this.baseConfig;
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Overriding config entry %s, %s by %s",
                        entry.getKey(), this.usedConfig.get(entry.getKey()), entry.getValue()));
            }
            this.usedConfig.put(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public Components components() {
        if (this.usedConfig == null) {
            this.usedConfig = new HashMap<String, String>();
        }

        Components components = SensorsLocator.get.getComponents(this.usedConfig);

        //Reset config
        this.usedConfig = this.baseConfig;

        return components;
    }

    public static void main(String[] args) {
        Map<String, String> overriddenConfig = new HashMap<String, String>();
        for (final String arg : args) {
            if ("--debug".equals(arg)) {
                overriddenConfig.put("debugMode", "true");
            }
        }

        Components components = JSensors.get.config(overriddenConfig).components();

        Cpu cpu = components.cpu;
        if (cpu != null) {
            System.out.println("Found CPU component " + cpu.name);
            readComponent(cpu);
        }
    }

    private static void readComponent(Component component) {
        if (component.sensors != null) {
            System.out.println("Sensors: ");
            
            List<Temperature> temps = component.sensors.temperatures;            
            for (final Temperature temp : temps) {
                System.out.println(temp.name + ": " + temp.value + " C");
            }
            
            List<Fan> fans = component.sensors.fans;            
            for (final Fan fan : fans) {
                System.out.println(fan.name + ": " + fan.value + " RPM");
            }
        }
    }
}
