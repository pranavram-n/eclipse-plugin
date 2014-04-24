/*
 * CODENVY CONFIDENTIAL
 * ________________
 * 
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.eclipse.ui.test.mock;

import static com.codenvy.eclipse.ui.test.mock.UserServiceMock.MOCK_USERNAME;

import com.codenvy.eclipse.core.AuthenticationService;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * The Codenvy authentication client service mock.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationServiceMock implements AuthenticationService {
    public static final String MOCK_PASSWORD = "secret";

    public AuthenticationServiceMock(String url) {
    }

    @Override
    public CodenvyToken login(String username, String password) {
        if (MOCK_USERNAME.equals(username) && MOCK_PASSWORD.equals(password)) {
            return new CodenvyToken("codenvy-token");
        }
        return null;
    }
}