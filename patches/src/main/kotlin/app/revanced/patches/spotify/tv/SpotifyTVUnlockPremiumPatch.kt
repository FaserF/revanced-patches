package app.revanced.patches.spotify.tv

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patches.spotify.misc.extension.sharedExtensionPatch
import app.revanced.util.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

internal const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/revanced/extension/spotify/misc/UnlockPremiumPatch;"

@Suppress("unused")
val spotifyTVUnlockPremiumPatch = bytecodePatch(
    name = "Spotify TV Unlock Premium",
    description = "Unlocks Spotify Premium features for Android TV. Server-sided features like downloading songs are still locked.",
) {
    compatibleWith(
        "com.spotify.tv.android"(
            "1.118.1",
        ),
    )

    dependsOn(
        sharedExtensionPatch,
    )

    execute {
        fun MutableClass.publicizeField(fieldName: String) {
            fields.firstOrNull { it.name == fieldName }?.apply {
                accessFlags = accessFlags.toPublicAccessFlags()
            }
        }

        // Make _value accessible for the extension.
        accountAttributeFingerprint.classDef.publicizeField("value_")

        // Override the attributes map logic.
        productStateProtoGetMapFingerprint.method.apply {
            val getAttributesMapIndex = indexOfFirstInstructionOrThrow(Opcode.IGET_OBJECT)
            val attributesMapRegister = getInstruction<TwoRegisterInstruction>(getAttributesMapIndex).registerA

            addInstruction(
                getAttributesMapIndex + 1,
                "invoke-static { v$attributesMapRegister }, " +
                        "$EXTENSION_CLASS_DESCRIPTOR->overrideAttributes(Ljava/util/Map;)V"
            )
        }
    }
}
