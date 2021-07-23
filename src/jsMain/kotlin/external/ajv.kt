@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

@JsNonModule
@JsModule("ajv")
open external class AJV {
    fun validate(schema: dynamic, data: dynamic): Boolean
    fun validateSchema(schema: dynamic): Boolean
    fun addKeyword(keyword: String)

    val errors: dynamic
}

@JsNonModule
@JsModule("ajv/dist/2019")
external class AJV2019 : AJV

@JsNonModule
@JsModule("ajv/dist/2020")
external class AJV2020 : AJV

@JsNonModule
@JsModule("ajv-formats")
external fun addFormats(ajv: AJV)