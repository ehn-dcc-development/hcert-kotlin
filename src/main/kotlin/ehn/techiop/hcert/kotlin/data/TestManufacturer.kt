package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/ehn-digital-green-development/ehn-dgc-schema/blob/main/valuesets/test-manf.json
// 2021-04-27
@Serializable
enum class TestManufacturer(val value: String) {
    @SerialName("1232")
    ABBOTT("1232"),

    @SerialName("1304")
    AMEDA("1304"),

    @SerialName("1065")
    BECTON("1065"),

    @SerialName("1331")
    BEIJING_LEPU("1331"),

    @SerialName("1484")
    BEIJING_WANTAI("1484"),

    @SerialName("1242")
    BIONOTE("1242"),

    @SerialName("1223")
    BIOSYNEX("1223"),

    @SerialName("1173")
    CERTEST("1173"),

    @SerialName("1244")
    GENBODY("1244"),

    @SerialName("1360")
    GUANGDONG_WESAIL("1360"),

    @SerialName("1363")
    HANGZHOU_CLONGENE("1363"),

    @SerialName("1767")
    HEALGEN("1767"),

    @SerialName("1333")
    JOINSTAR("1333"),

    @SerialName("1268")
    LUMIRADX("1268"),

    @SerialName("1180")
    MEDSAN("1180"),

    @SerialName("1481")
    MP_BIOMEDICALS("1481"),

    @SerialName("1162")
    NAL_VON_MINDEN("1162"),

    @SerialName("1271")
    PRECISION_BIOSENSOR("1271"),

    @SerialName("1341")
    QINGDAO_HIGHTOP("1341"),

    @SerialName("1097")
    QUIDEL("1097"),

    @SerialName("1489")
    SAFECARE("1489"),

    @SerialName("344")
    SD_BIOSENSOR_FIA("344"),

    @SerialName("345")
    SD_BIOSENSOR_TEST("345"),

    @SerialName("1218")
    SIEMENS("1218"),

    @SerialName("1278")
    XIAMEN_BOSON("1278"),

    @SerialName("1343")
    ZHEJIANG_ORIENT("1343"),

    @SerialName("Unknown")
    UNKNOWN("Unknown");

    companion object {
        fun findByValue(value: String): TestManufacturer {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}