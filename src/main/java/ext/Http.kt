package ext

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

val client = HttpClient.newBuilder().build()

fun HttpRequest.Builder.send() = client.send(this.build(), HttpResponse.BodyHandlers.ofString())
fun HttpRequest.Builder.header(pair: Pair<Any, Any>) = this.header(pair.first.toString(), pair.second.toString())
fun String.request() = HttpRequest.newBuilder(URI.create(this))

fun HttpRequest.Builder.post(body: Any? = null) = this.POST(when (body) {
    is ByteArray -> HttpRequest.BodyPublishers.ofByteArray(body)
    is String -> HttpRequest.BodyPublishers.ofString(body)
    is HttpRequest.BodyPublisher -> body
    else -> throw Exception("Unsupported body type")
}).send()
