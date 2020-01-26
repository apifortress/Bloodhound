## Route 127.0.0.1:8080/any

Calls an echo service: `curl -v http://127.0.0.1:8080/any`

## Route 127.0.0.1:8080/transform/headers

Adds a `x-original-remoteIP` header with the requesting agent IP and sets the `User-Agent` header to `Afthem`.

`curl -v http://127.0.0.1:8080/transform/headers`

## Route 127.0.0.1:8080/filtered

Accepts `GET` calls that have the following headers:

```text
key: ABC123
accept: application/json
``` 

Working call:

```text
curl -v -H "key:ABC123" -H "accept:application/json" 127.0.0.1:8080/filtered
```

Filtered call:

```text
curl -v 127.0.0.1:8080/filtered
```

## Route 127.0.0.1:8080/serialize

Serializes an API conversation to a file.

```text
curl -v 127.0.0.1:8080/serialize
```

## Route 127.0.0.1:8080/file

Uses a directory of files as backend.

```text
curl -v 127.0.0.1:8080/file/file1.json
curl -v 127.0.0.1:8080/file/file2.xml
curl -v 127.0.0.1:8080/file/file3
```