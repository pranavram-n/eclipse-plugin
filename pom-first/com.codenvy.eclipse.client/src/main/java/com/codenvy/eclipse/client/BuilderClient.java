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
package com.codenvy.eclipse.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.ws.rs.client.Invocation;

import com.codenvy.eclipse.client.auth.AuthenticationManager;
import com.codenvy.eclipse.client.model.BuilderStatus;
import com.codenvy.eclipse.client.model.Project;

/**
 * The Codenvy builder API client.
 * 
 * @author Kevin Pollet
 */
public class BuilderClient extends AbstractClient {
    /**
     * Constructs an instance of {@link BuilderClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param authenticationManager the {@link AuthenticationManager}.
     * @throws NullPointerException if url or authenticationManager parameter is {@code null}.
     */
    BuilderClient(String url, AuthenticationManager authenticationManager) {
        super(url, "builder", authenticationManager);
    }

    /**
     * Builds the given {@link Project} on codenvy.
     * 
     * @param project the project to build.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<BuilderStatus> build(Project project) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("build")
                                                 .queryParam("project", project.name)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildPost(null);

        return new SimpleRequest<>(request, BuilderStatus.class, getAuthenticationManager());
    }

    /**
     * Gets the status of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<BuilderStatus> status(Project project, long taskId) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("status")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleRequest<>(request, BuilderStatus.class, getAuthenticationManager());
    }

    /**
     * Gets the logs of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the builder logs.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<String> logs(Project project, long taskId) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("logs")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(TEXT_PLAIN)
                                                 .buildGet();

        return new SimpleRequest<>(request, String.class, getAuthenticationManager());
    }

    /**
     * Cancels the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<BuilderStatus> cancel(Project project, long taskId) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("cancel")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleRequest<>(request, BuilderStatus.class, getAuthenticationManager());
    }
}
