package cn.sound.model

data class Sound(val assetPath: String, val soundId: Int) {
    val name: String

    init {
        val components = assetPath.split("/")
        val fileName = components[components.size - 1]
        name = fileName.replace(".wav", "").replace("_", " ").capitalize()
    }
}