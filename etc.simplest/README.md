## Route 127.0.0.1:8080/any

Calls an echo service: `curl -v http://127.0.0.1:8080/any`

The flow modifies the user-agent to add the AFtheM identifier.

## Route 127.0.0.1:8080/any/with/filter

Calls an echo server. Requests will be accepted if headers contain:

```text
key: ABC123
accept: application/json
``` 
Filtered otherwise.

Working call:
```text
curl -v -H "key:ABC123" -H "accept:application/json" 127.0.0.1:8080/any/with/filter
```

Filtered call:
```text
curl -v 127.0.0.1:8080/any/with/filter
```