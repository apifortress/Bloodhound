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
