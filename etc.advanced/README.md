## Route 127.0.0.1:8080/apikeys

Validates whether the request contains a valid API key, expected as part of the query string.

```text
curl 127.0.0.1:8080/apikeys?key=ABC123
```

## Route 127.0.0.1:8080/auth

Validates whether the request contains a valid basic authentication header.

```text
curl http://afthem:foobar@127.0.0.1:8080/auth
```

## Route 127.0.0.1:8080/replace-upstream

In case a certain precondition in met, the upstream URL is replaced.

```text
curl http://127.0.0.1:8080/replace-upstream
curl -H 'replace-me:true' http://127.0.0.1:8080/replace-upstream
```

## Route 127.0.0.1:8080/transform-payload
Transforms a payload. In this mode, a regex looks for a string and replaces it.

```text
curl -H 'replace-me:true' http://127.0.0.1:8080/transform-payload
```

## Route 127.0.0.1:8080/endpoint-identifier
Labels the call based on URL and method. The label is then used in the generic logger and added as response header.

```text
curl -v 127.0.0.1:8080/endpoint-identifier/products
curl -v 127.0.0.1:8080/endpoint-identifier/products/77
```