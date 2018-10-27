package org.http4k.k8s

import org.http4k.core.HttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.ServerConfig
import org.http4k.server.asServer

fun HttpHandler.asK8sServer(serverConfig: (port: Int) -> ServerConfig,
                            port: Int = 8000,
                            healthApp: HttpHandler = Health(),
                            healthPort: Int = 8001) = Http4kK8sServer(asServer(serverConfig(port)), healthApp.asServer(serverConfig(healthPort)))

/**
 * A K8S server consists of a main application and a health application, running on 2 different ports.
 */
class Http4kK8sServer(private val main: Http4kServer, private val health: Http4kServer) : Http4kServer {
    override fun port() = main.port()
    fun healthPort() = health.port()

    override fun start(): Http4kServer = apply {
        main.start()
        health.start()
    }

    override fun stop(): Http4kServer = apply {
        health.stop()
        main.stop()
    }
}