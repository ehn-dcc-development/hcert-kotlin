package ehn.techiop.hcert.kotlin.data

actual object ValueSetsInstanceHolder {
    actual val INSTANCE: ValueSetHolder by lazy {
        ValueSetHolder(emptyList())
    }
}