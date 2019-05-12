## Route 127.0.0.1:8080/any

Calls an echo service: `curl -v http://127.0.0.1:8080/any`


## Route 127.0.0.1:8080/any/with/filter

Calls an echo server. Requests will be accepted if headers contain:

```aidl
key: ABC123
accept: application/json
``` 
Filtered otherwise.

Working call:
```
curl -v -H "key:ABC123" -H "accept:application/json" 127.0.0.1:8080/any/with/filter
```

Filtered call:
```
curl -v 127.0.0.1:8080/any/with/filter
```