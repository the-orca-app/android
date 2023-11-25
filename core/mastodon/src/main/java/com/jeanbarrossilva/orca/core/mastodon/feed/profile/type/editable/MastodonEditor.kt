package com.jeanbarrossilva.orca.core.mastodon.feed.profile.type.editable

import com.jeanbarrossilva.orca.core.feed.profile.type.editable.Editor
import com.jeanbarrossilva.orca.core.mastodon.http.client.authenticationLock
import com.jeanbarrossilva.orca.core.mastodon.instance.ContextualMastodonInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.std.imageloader.SomeImageLoader
import com.jeanbarrossilva.orca.std.injector.Injector
import com.jeanbarrossilva.orca.std.styledstring.StyledString
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parametersOf
import io.ktor.utils.io.streams.asInput
import java.nio.file.Path
import kotlin.io.path.name

/** [Editor] whose actions communicate with the Mastodon API. */
internal class MastodonEditor : Editor {
  private val requester =
    (Injector.from<CoreModule>().instanceProvider().provide() as ContextualMastodonInstance)
      .requester
      .authenticated(authenticationLock)

  @Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
  override suspend fun setAvatarLoader(avatarLoader: SomeImageLoader) {
    val file: Path = TODO()
    val fileAsFile = file.toFile()
    val fileLength = fileAsFile.length()
    val inputProvider = InputProvider(fileLength) { fileAsFile.inputStream().asInput() }
    val contentDisposition = "form-data; name=\"avatar\" filename=\"${file.name}\""
    val headers = Headers.build { append(HttpHeaders.ContentDisposition, contentDisposition) }
    val form = formData { append("avatar", inputProvider, headers) }
    requester.post(ROUTE, form = form)
  }

  override suspend fun setName(name: String) {
    requester.post(ROUTE, parametersOf("display_name", name))
  }

  override suspend fun setBio(bio: StyledString) {
    requester.post(ROUTE, parametersOf("note", "$bio"))
  }

  companion object {
    /** Route to which the [HttpRequest]s will be sent for editing an [MastodonEditableProfile]. */
    private const val ROUTE = "api/v1/accounts/update_credentials"
  }
}
