//Increase timeouts, since RSA key generation can take ages in JS
//See https://discuss.kotlinlang.org/t/configuring-timeouts-when-running-mocha-based-tests-in-mpp-or-js-projects/16567/6
config.set({
    client: {
        mocha: {
            timeout: 281000
        }
    },
    browserNoActivityTimeout: 191337,
    browserDisconnectTimeout: 192668,
    processKillTimeout: 193995
});
