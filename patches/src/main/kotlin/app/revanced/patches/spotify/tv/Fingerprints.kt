@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package app.revanced.patches.spotify.tv

import app.revanced.patcher.fingerprint
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.AccessFlags

context(BytecodePatchContext)
internal val accountAttributeFingerprint get() = fingerprint {
    custom { _, classDef -> classDef.type == "Lcom/spotify/remoteconfig/internal/AccountAttribute;" }
}

context(BytecodePatchContext)
internal val productStateProtoGetMapFingerprint get() = fingerprint {
    returns("Ljava/util/Map;")
    custom { _, classDef -> classDef.type == "Lcom/spotify/remoteconfig/internal/ProductStateProto;" }
}
