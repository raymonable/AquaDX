package icu.samnyan.aqua.sega.util.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ext.jsonArray
import ext.jsonMap
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Component
class StringMapper: IMapper {
    override fun write(o: Any?) = STRING_MAPPER.writeValueAsString(o)
    fun <T> convert(map: Any?, toClass: Class<T>?) = STRING_MAPPER.convertValue(map, toClass)
    final inline fun <reified T> convert(map: Any?) = STRING_MAPPER.convertValue(map, object : TypeReference<T>() {})
    fun toMap(obj: Any?) = STRING_MAPPER.convertValue(obj, object : TypeReference<LinkedHashMap<String, Any>>() {})

    companion object {
        val BOOLEAN_SERIALIZER = object : StdSerializer<Boolean>(Boolean::class.java) {
            override fun serialize(v: Boolean, gen: JsonGenerator, p: SerializerProvider) {
                gen.writeString(v.toString())
            }
        }
        var STRING_MAPPER = jacksonObjectMapper().apply {
            enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true)
            findAndRegisterModules()
            registerModule(SimpleModule().apply {
                addSerializer(
                    LocalDateTime::class.java,
                    LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
                addDeserializer(
                    LocalDateTime::class.java,
                    LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
                addSerializer(Boolean::class.javaObjectType, BOOLEAN_SERIALIZER)
                addSerializer(Boolean::class.javaPrimitiveType, BOOLEAN_SERIALIZER)
            })
        }
    }
}
