//Increase timeouts
// See https://discuss.kotlinlang.org/t/configuring-timeouts-when-running-mocha-based-tests-in-mpp-or-js-projects/16567/6
config.set({
    client: {
        mocha: {
            timeout: 18000
        }
    },
    browserNoActivityTimeout: 21337,
    browserDisconnectTimeout: 22668,
    processKillTimeout: 23995
});
