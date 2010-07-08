/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

/**
 *
 */
public class MifosSpringContextListener extends ContextLoaderListener {

    @Override
    protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
        super.customizeContext(servletContext, applicationContext);

        List<String> configLocations = new ArrayList<String>();

        String[] oldConfigLocations = applicationContext.getConfigLocations();
        configLocations.addAll(Arrays.asList(oldConfigLocations));

        String financialActionMappingsCustomisation;
        try {
            financialActionMappingsCustomisation = new ConfigurationLocator().getFilePath(FilePaths.FINANCIAL_ACTION_MAPPING_CONFIG_CUSTOM_BEAN);
        } catch (IOException e) {
            financialActionMappingsCustomisation = null;
        }
        if (financialActionMappingsCustomisation != null) {
            configLocations.add("file:" + financialActionMappingsCustomisation);
        }

        applicationContext.setConfigLocations(configLocations.toArray(new String[configLocations.size()]));
    }
}
