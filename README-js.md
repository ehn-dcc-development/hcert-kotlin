# Notes for the JS port of the kotlin code base

## Cose

The only available JS implementation of COSE was designed as a Node.js (CommonJS) module. Since it makes use of some Node-specific APIs, we couldn't simply integrate it using kotlin's npm dependency mechanism. Instead, a few additional steps were needed to integrate it with our kotlin code base. 

We utilise a custom build step based on browserify for turning the NodeJS module into a self-contained browser library for consumption from the kotlin side.

The current first draft of the general process follows these steps:

1) Create an empty folder ("temp") with a file called "cose_node.js" and add a require call to cose-js:
```
const cose = require('cose-js');
```

2) Install the cose-js library and all its dependencies in the temp folder:
```
npm install cose-js
```

3) Since the current release version of `cose-js` is not browser-compliant, we need to replace the corresponding folder 
   in node_modules with the newest version from the master branch (https://github.com/erdtman/cose-js).
   
4) Now bundle the cose-js lib and all its dependencies into using browserify. 
   Make sure to export the cose-js and buffer libraries:
```
(in temp)
browserify -r cose-js -r buffer test.js > cose.js
```

5) Because kotlin-js's webpack integration defines its own require function,
the one exported from cose.js (providing the cose-js and Buffer dependencies) 
   now needs to be renamed. Simply replace all occurrences of 'require' with 'extrequire'.

6) Copy the resulting cose.js file to src/jsMain/cose.js in this project

7) Include it in the client html file that consumes the hcert-kotlin js library

8) From Kotlin code, Buffer and COSE can be accessed through js() calls or via corresponding external declarations (TODO)
