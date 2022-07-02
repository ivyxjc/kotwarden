```shell
sam build -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml

sam deploy -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml
```

# Credentials

**Warning: Do not use private key in `resources/credentials` in production**