package se.torsteneriksson.recepihandler.database
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
//https://github.com/Kotlin/kotlinx.serialization

@Serializable
data class Project(val name: String, val language: String)

fun json() {
    // Serializing objects
    val data = Project("kotlinx.serialization", "Kotlin")
    val string = Json.encodeToString(data)
    println(string) // {"name":"kotlinx.serialization","language":"Kotlin"}
    // Deserializing back into objects
    val obj = Json.decodeFromString<Project>(string)
    println(obj) // Project(name=kotlinx.serialization, language=Kotlin)
}
