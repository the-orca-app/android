/*
 * Copyright © 2023 Orca
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see https://www.gnu.org/licenses.
 */

package com.jeanbarrossilva.orca.std.injector.module

import kotlin.reflect.KClass

/** Container in which dependencies within a given context can be injected. */
abstract class Module {
  /** Dependencies that have been injected associated to their assigned types. */
  @PublishedApi internal val injections = hashMapOf<KClass<*>, Lazy<Any>>()

  /**
   * [NoSuchElementException] thrown if a dependency that hasn't been injected is requested to be
   * obtained.
   *
   * @param dependencyClass [KClass] of the requested dependency.
   */
  inner class DependencyNotInjectedException
  @PublishedApi
  internal constructor(dependencyClass: KClass<*>) :
    NoSuchElementException(
      "No dependency of type ${dependencyClass.qualifiedName} has been injected into " +
        "${this::class.simpleName}."
    )

  /**
   * Injects the dependency returned by the [injection].
   *
   * @param T Dependency to be injected.
   * @param injection Returns the dependency to be injected.
   */
  inline fun <reified T : Any> inject(noinline injection: Module.() -> T) {
    inject(T::class, injection)
  }

  /**
   * Lazily obtains the injected dependency whose type is [T].
   *
   * @param T Dependency to be lazily obtained.
   */
  inline fun <reified T : Any> lazy(): Lazy<T> {
    return lazy(::get)
  }

  /**
   * Obtains the injected dependency whose type is [T].
   *
   * @param T Dependency to be obtained.
   * @throws DependencyNotInjectedException If no dependency of type [T] has been injected.
   */
  @Throws(NoSuchElementException::class)
  inline fun <reified T : Any> get(): T {
    return injections[T::class]?.value as T? ?: throw DependencyNotInjectedException(T::class)
  }

  /** Removes all injected dependencies. */
  fun clear() {
    injections.clear()
    onClear()
  }

  /**
   * Injects the dependency returned by the [injection].
   *
   * @param T Dependency to be injected.
   * @param dependencyClass [KClass] to which the dependency will be associated.
   * @param injection Returns the dependency to be injected.
   */
  @PublishedApi
  internal fun <T : Any> inject(dependencyClass: KClass<T>, injection: Module.() -> T) {
    if (dependencyClass !in injections) {
      injections[dependencyClass] = lazy { injection() }
    }
  }

  /** Callback run whenever this [Module] is cleared. */
  internal open fun onClear() {}
}
