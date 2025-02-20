/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.jaxrs.fat.microProfileApp.MicroProfileLoginConfigMpJwtInWebXmlMPJWTInApp;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;

import com.ibm.ws.security.fat.common.mp.jwt.CommonMicroProfileApp;

// http://localhost:<nonSecurePort>/microProfileApp/rest/microProfileNoLoginConfig/MicroProfileApp
// allow the same methods to invoke GET, POST, PUT, ... invocation type determines which is invoked.

@Path("loginConfig_MpJwtInWebXml_MpJwtInApp")
@RequestScoped
public class MicroProfileApp extends CommonMicroProfileApp {

}
