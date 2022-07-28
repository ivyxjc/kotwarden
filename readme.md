### Alternative Serverless implementation of the Bitwarden server API written in Kotlin and compatible with upstream Bitwarden clients

### **This project is not associated with the [Bitwarden](https://bitwarden.com/) project nor 8bit Solutions LLC.**

### **This project is under development, database structure is not stable**

### **Warning: Do not use private key in `resources/credentials` in production**

# Top-level design

HTTP API: AWS API Gateway --> AWS Lambda

DATABASE: DynamoDB

# Deployment

## Deploy dynamodb

```shell
sam build -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml

sam deploy -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml
```

# Serverless Compatibility

The project is only compatible with AWS lambda nowadays

- [x] aws lambda

# Notes

## Why not to use JetBrains Kotless directly?

[JetBrains kotless](https://github.com/JetBrains/kotless) is a kotlin-based serverless framework.
From the perspective of mine, it is more like an infrastructure deployment tool/framework rather than a serverless
lib. I would like to choose one lib as sidecar of the project. So I extract part of code which is related with ktor
serverless from JetBrains kotless into folder `src/main/kotlin/com/ivyxjc/kotless`.

There is another factor to be considered that kotless does not support AWS ApiGateway V2 HttpApi.

# License

1. The code in folder `src/main/kotlin/com/ivyxjc/kotless` is licensed under the Apache 2.0 license since most of the
   code is from the [JetBrains kotless](https://github.com/JetBrains/kotless) which is licensed under the Apache 2.0
   license.
2. Other part of the code is licensed under the GPL-3.0 license since that I learn some system design and api
   implementations
   from another project [vaultwarden](https://github.com/dani-garcia/vaultwarden) project which is licensed under the
   GPL-3.0.