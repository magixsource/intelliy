package com.github.magixsource.intelliy.idp

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
 */
class IdpService(private val logPanel: LogPanel) {
    private val client: OkHttpClient = OkHttpClient()
    private val listener: EchoWebSocketListener = EchoWebSocketListener(logPanel)
    val maxPageSize = 1000


    /**
     * get self information
     * http://y-api.dtyunxi.cn/iam/v1/users/self
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
     * http://y-api.dtyunxi.cn/iam/v1/users/self
     * http://y-api.dtyunxi.cn/iam/v1/users/87/projects
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
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/envs?active=true
     */
    fun getEnvs(baseApi: String, privateToken: String, projectId: Int): List<Env>? {
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
     * POST http://y-api.dtyunxi.cn/devops/v1/projects/21/app_instances/list_by_options?page=1&size=15&envId=27
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
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/app_pod/list_by_options?page=1&size=15&sort=id,desc&envId=105
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
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/app_instances/2686/resources
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/app_pod/list_by_options?page=1&size=15&sort=id,desc&envId=105&appId=1954
     *
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/app_pod/2073/containers/logs
     *
     * [{"podName":"picbur-d1b80-566d469957-lvlcx","containerName":"picbur-d1b80","logId":"b91475d3-e415-4d90-8103-677061cadf32"}]
     *
     * ws://y-devops.dtyunxi.cn/ws/log?key=cluster:106.log:b91475d3-e415-4d90-8103-677061cadf32&env=lppz-demo&podName=picbur-d1b80-566d469957-lvlcx&containerName=picbur-d1b80&logId=b91475d3-e415-4d90-8103-677061cadf32&token=GA1.2.1213895659.1585043447;%20monitorCustomerKey
     *
     */
    fun getLogs(
        baseApi: String,
        privateToken: String,
        project: Project,
        env: Env,
        pod: Pod
    ) {
        println("--------------------get log-----------------------------------")
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

        // How to get token by ws ?
        // val token = "GA1.2.1213895659.1585043447; monitorCustomerKey"
        // val token = privateToken

        val path =
            "/ws/log?key=$key&env=$envCode&podName=$podName&containerName=$containerName&logId=$logId&privateToken=$privateToken"
        val url = base + path
        println("====url is $url")
        val request = Request.Builder().url(url).build()
        listener
        client.newWebSocket(request, listener)
        //client.dispatcher.executorService.shutdown()
    }

    /**
     * http://y-api.dtyunxi.cn/devops/v1/projects/21/app_pod/2073/containers/logs
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
        val headers = resp.headers
        println(headers)
        val type = object : TypeToken<List<ContainerLog>>() {}.type
        val body = parseResponse(resp)
        return if (body.isNotEmpty()) {
            Gson().fromJson(body, type)
        } else {
            null
        }
    }

    private fun parseResponse(resp: Response): String {
        val body = resp.body?.string() ?: ""
//        println("===response body $body")
        return body

    }

    private fun post(baseApi: String, path: String, privateToken: String, queryBody: String): Request {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val url = baseApi + path
        val requestBody = queryBody.toRequestBody(mediaType)
        return Request.Builder().url(url).addHeader("Private-Token", privateToken).post(requestBody).build()
    }

    private fun get(baseApi: String, path: String, privateToken: String): Request {
        var url = baseApi + path
        return Request.Builder().url(url).addHeader("Private-Token", privateToken).build()
    }

}