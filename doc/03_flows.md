# AFtheM - flows

In the default configuration module, flows are files located in the `etc/flows` directory.

## Anatomy of a flow

A flow is a number of steps (actors) that will be performed between the inbound request and the response to the outbound
request. Some of which are meant to work in a sequence, and some in parallel.

In order to use an actor, its implementation needs to be defined among the *implementers*. In the default configuration
mode, this happens in the `implementers.yml` file.

There are 3 essential actors a flow cannot do without and are required in every flow:

* A request-parsing step, explicitly named `proxy/request`;
* An upstream step, performing the actual call to the upstream;
* A send-back step, returning the retrieved content to the user;

With the exception of `proxy/request`, naming is free, as well as implementations, but the structure needs to follow
the `<type>/<name>` pattern.

Each step has a set of fixed instructions and extra fields.

* The key is a combination of the type and the ID, declared in the `implementers.yml` file
* `next` determines what's the next actor in the flow
* `sidecars` (not always applicable) are the IDs of actors that will receive a copy of the message in parallel but do
   not alter the main message. Mind that sidecars can have different behaviors based on where they're placed in the
   flow. For example, access loggers log inbound calls when placed before the SendBack, and outbound calls when placed
   after the SendBack
* `config` other implementation-specific configuration keys  

If a certain actor is referenced either as `next` or in `sidecars`, it **must** to be present in the flow.

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

