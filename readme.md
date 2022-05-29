```shell
sam build -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml

sam deploy -t template-dynamodb.yaml --config-file samconfig-dynamodb.toml
```