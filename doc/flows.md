# AFtheM - flows

In the default configuration module, flows are files located in the `etc/flows` directory.

## Anatomy of a flow

A flow is a number of steps, some of which are meant to work in a sequence, and some in parallel.

There are 3 essential steps a flow cannot do without and are required in every flow:

* A request-parsing step, explicitly named `proxy/request`
* An upstream step, performing the actul call to the upstream
* A send-back step, returning the retrieved content to the user

With the exception of `proxy/request` naming is free, as well as implementations.

Each step has a set of fixed instructions and extra fields.

* The key is a combination of the type and the ID, declared in the `implementers.yml` file
* `next` determines what's the next step in the flow
* `sidecars` (not always applicable) are the IDs of actors that will receive a copy of the message in parallel but do
   not alter the main message
* `config` other implementation-specific configuration keys  

If a certain step is referenced either as `next` or in `sidecars`, it **must** to be present in the flow.

Example:

```yaml
proxy/request:
  next: filter/header_filter
  sidecars:
    - sidecar/access_logger

filter/header_filter:
  next: proxy/upstream_http
  sidecars:
    - sidecar/access_logger
  config:
    accept:
      - value: "#msg.request().getHeader('key')=='ABC123'"
        evaluated: true
      - value: "#msg.request().getHeader('accept')=='application/json'"
        evaluated: true
    reject:
      - value: "#msg.request().method()!='GET'"
        evaluated: true

proxy/upstream_http:
  next: proxy/send_back

proxy/send_back:
  sidecars:
    - sidecar/access_logger

sidecar/access_logger:
  config: {}
```
