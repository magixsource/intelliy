package com.github.magixsource.intelliy.idp

import com.github.magixsource.intelliy.idp.model.*
import com.github.magixsource.intelliy.listeners.EchoWebSocketListener
import com.github.magixsource.intelliy.toolwindow.LogPanel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * Idp v1 Service
 * 负责连接i-DP API,实现数据查询与操作
 */
class IdpService(logPanel: LogPanel) {
    private val PRIVATE_TOKEN_KEY = "Private-Token"
    private val client: OkHttpClient = OkHttpClient()
    private val listener: EchoWebSocketListener = EchoWebSocketListener(logPanel)
    private val maxPageSize = 1000


    /**
     * get user self information
     * http://idp-api/iam/v1/users/self
     */
    private fun getSelf(baseApi: String, privateToken: String): User? {
        val req = get(baseApi, "/iam/v1/users/self", privateToken)
        val resp = client.newCall(req).execute()
        val body = parseResponse(resp)
        val type = object : TypeToken<User>() {}.type
        return if (body.isNotEmpty()) {
            Gson().fromJson(body, type)
        } else {
            null
        }
    }

    /**
     * get project list has assigned to user
     * http://idp-api/iam/v1/users/{userid}/projects
     */
    fun getProjects(baseApi: String, privateToken: String): List<Project>? {
        val user = getSelf(baseApi, privateToken) ?: return null
        val path = "/iam/v1/users/${user.id}/projects"
        val req = get(baseApi, path, privateToken)
        val resp = client.newCall(req).execute()
        val type = object : TypeToken<List<Project>>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson(body, type)
        } else {
            null
        }
    }

    /**
     * get active environment of project
     * http://idp-api/devops/v1/projects/{project_id}/envs?active=true
     */
    fun getEnvironments(baseApi: String, privateToken: String, projectId: Int): List<Env>? {
        val path = "/devops/v1/projects/$projectId/envs?active=true"
        val req = get(baseApi, path, privateToken)
        val resp = client.newCall(req).execute()
        val type = object : TypeToken<List<Env>>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson(body, type)
        } else {
            null
        }
    }

    /**
     * get instance of environment
     * POST http://idp-api/devops/v1/projects/{project_id}/app_instances/list_by_options?page=1&size={page_size}&envId={env_id}
     */
    fun getInstances(baseApi: String, privateToken: String, projectId: Int, envId: Int): List<Instance>? {
        val path = "/devops/v1/projects/$projectId/app_instances/list_by_options?page=1&size=$maxPageSize&envId=$envId"
        val queryBody = "{\"searchParam\":{},\"param\":\"\"}"
        val req = post(baseApi, path, privateToken, queryBody)
        val resp = client.newCall(req).execute()
        val type = object : TypeToken<PageInstances>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson<PageInstances?>(body, type).list
        } else {
            null
        }
    }

    /**
     * get pods of environment
     * http://idp-api/devops/v1/projects/{project_id}/app_pod/list_by_options?page=1&size=15&sort=id,desc&envId={env_id}
     */
    fun getPods(baseApi: String, privateToken: String, projectId: Int, envId: Int): List<Pod>? {
        val path =
            "/devops/v1/projects/$projectId/app_pod/list_by_options?page=1&size=$maxPageSize&sort=id,desc&envId=$envId"
        val queryBody = "{\"searchParam\":{},\"param\":\"\"}"
        val req = post(baseApi, path, privateToken, queryBody)
        val resp = client.newCall(req).execute()
        val type = object : TypeToken<PagePods>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson<PagePods?>(body, type).list
        } else {
            null
        }
    }

    /**
     * get pod log by web socket
     * ws://idp-devops/ws/log?key=cluster:106
     */
    fun getLogs(
        baseApi: String,
        privateToken: String,
        project: Project,
        env: Env,
        pod: Pod
    ) {
        val containerLogs = getContainerLog(baseApi, privateToken, project, pod)
        checkNotNull(containerLogs)
        // get first element as default container
        val containerLog = containerLogs[0]

        val host = baseApi.toHttpUrl().host.replace("api", "devops")
        val base = "ws://$host"
        val clusterId = pod.clusterId
        val logId = containerLog.logId
        val key = "cluster:$clusterId.log:$logId"
        val envCode = pod.envCode
        val podName = pod.name
        val containerName = containerLog.containerName

        val path =
            "/ws/log?key=$key&env=$envCode&podName=$podName&containerName=$containerName&logId=$logId&privateToken=$privateToken"
        val url = base + path
        val request = Request.Builder().url(url).build()
        client.newWebSocket(request, listener)
        //client.dispatcher.executorService.shutdown()
    }

    /**
     * get container log
     * http://idp-api/devops/v1/projects/{project_id}/app_pod/{pod_id}/containers/logs
     */
    private fun getContainerLog(
        baseApi: String,
        privateToken: String,
        project: Project, pod: Pod
    ): List<ContainerLog>? {
        val projectId = project.id
        val podId = pod.id
        val path = "/devops/v1/projects/$projectId/app_pod/$podId/containers/logs"
        val req = get(baseApi, path, privateToken)
        val resp = client.newCall(req).execute()
        val type = object : TypeToken<List<ContainerLog>>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson(body, type)
        } else {
            null
        }
    }

    private fun parseResponse(resp: Response): String {
        return resp.body?.string() ?: ""
    }

    private fun post(baseApi: String, path: String, privateToken: String, queryBody: String): Request {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val url = baseApi + path
        val requestBody = queryBody.toRequestBody(mediaType)
        return Request.Builder().url(url).addHeader(PRIVATE_TOKEN_KEY, privateToken).post(requestBody).build()
    }

    private fun get(baseApi: String, path: String, privateToken: String): Request {
        var url = baseApi + path
        return Request.Builder().url(url).addHeader(PRIVATE_TOKEN_KEY, privateToken).build()
    }

}