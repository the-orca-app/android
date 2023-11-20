package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.typeInfoImpl
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.javaType

/**
 * Obtains and converts the body returned by this [HttpResponse] into a [T].
 *
 * @param T Value into which the returned body will be converted.
 * @param bodyClass [KClass] of [T].
 */
internal suspend fun <T> HttpResponse.body(bodyClass: KClass<T & Any>): T {
  val type = bodyClass.createType()

  @OptIn(ExperimentalStdlibApi::class) val typeInfo = typeInfoImpl(type.javaType, bodyClass, type)

  return body(typeInfo)
}
