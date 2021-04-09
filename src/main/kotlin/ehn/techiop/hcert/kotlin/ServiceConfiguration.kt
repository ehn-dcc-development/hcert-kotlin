package ehn.techiop.hcert.kotlin

import com.google.zxing.BarcodeFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {

    @Bean
    fun qrCodeService(): TwoDimCodeService {
        return TwoDimCodeService(350, BarcodeFormat.QR_CODE)
    }

    @Bean
    fun aztecService(): TwoDimCodeService {
        return TwoDimCodeService(350, BarcodeFormat.AZTEC)
    }

    @Bean
    fun cryptoService(): CryptoService {
        return RandomKeyCryptoService()
    }

    @Bean
    fun cborService(cryptoService: CryptoService): CborService {
        return CborService(cryptoService)
    }

    @Bean
    fun valSuiteService(): ValSuiteService {
        return ValSuiteService()
    }

    @Bean
    fun compressorService(): CompressorService {
        return CompressorService()
    }

    @Bean
    fun base45Service(): Base45Service {
        return Base45Service()
    }

    @Bean
    fun cborProcessingChain(
        cborService: CborService,
        valSuiteService: ValSuiteService,
        compressorService: CompressorService,
        base45Service: Base45Service
    ): CborProcessingChain {
        return CborProcessingChain(cborService, valSuiteService, compressorService, base45Service)
    }

    @Bean
    fun coseProcessStrategy(
        cborProcessingChain: CborProcessingChain,
        base45Service: Base45Service,
        qrCodeService: TwoDimCodeService,
        aztecService: TwoDimCodeService
    ): CborViewAdapter {
        return CborViewAdapter(cborProcessingChain, base45Service, qrCodeService, aztecService)
    }


}
