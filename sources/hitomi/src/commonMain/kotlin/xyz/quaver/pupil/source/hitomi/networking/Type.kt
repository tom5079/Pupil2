package xyz.quaver.pupil.source.hitomi.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val artist: String,
    val url: String
)

@Serializable
data class Group(
    val group: String,
    val url: String
)

@Serializable
data class Parody(
    val parody: String,
    val url: String
)

@Serializable
data class Character(
    val character: String,
    val url: String
)

@Serializable
data class Tag(
    val tag: String,
    val url: String,
    val female: String? = null,
    val male: String? = null
) {
    override fun toString() = buildString {
        if (!female.isNullOrEmpty()) append("female:")
        if (!male.isNullOrEmpty()) append("male:")
        append(tag)
    }
}

@Serializable
data class Language(
    @SerialName("galleryid") val galleryID: String,
    val url: String,
    @SerialName("language_localname") val localLanguageName: String,
    val name: String
)

@Serializable
data class GalleryFiles(
    val width: Int,
    val hash: String,
    @SerialName("haswebp") val hasWebP: Int = 0,
    val name: String,
    val height: Int,
    @SerialName("hasavif") val hasAVIF: Int = 0,
    @SerialName("hasavifsmalltn") val hasSmallAVIFThumbnail: Int? = 0
)

@Serializable
data class GalleryInfo(
    val id: String,
    val title: String,
    @SerialName("japanese_title") val japaneseTitle: String? = null,
    val language: String? = null,
    val type: String,
    val date: String,
    val artists: List<Artist>? = null,
    val groups: List<Group>? = null,
    @SerialName("parodys") val parodies: List<Parody>? = null,
    val tags: List<Tag>? = null,
    val related: List<Int> = emptyList(),
    val languages: List<Language> = emptyList(),
    val characters: List<Character>? = null,
    val files: List<GalleryFiles> = emptyList(),
    val video: String? = null,
    @SerialName("videofilename") val videoFileName: String? = null
)

