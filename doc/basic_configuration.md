# AFtheM - Basic configuration

The default AFtheM configuration module is file driven. All configuration files are located in the `etc` directory.

## System configuration

### afthem.yml

The `config_loader` section describes which configuration loading mechanism needs to be used. Modules can be created to
store and load configuration in other locations and systems, such as databases.

The `mime.text_content_types_contain` array contains a list of substrings meant to help the system detecting which
content types are meant to represent textual content.
  
### application.properties

the `logging.config=etc/logback.xml` describes where the logging configuration file is located.

### ehcache.xml

Certain operations may require some short lived caching. This is where that caching happens.

`configs` is a cache meant to store the system configuration, so that it doesn't need to be read multiple
times in a short period of time.

`expressions` is a cache meant to store the interpreted version of Spring SpEL scripts.

`api_keys` is a cache used by the default ApiKeysFilterActor to store API keys in memory.

New caches can be introduced to support other modules if necessary.

### logback.xml

The configuration of the logging system.

## Proxy configuration

### implementers.yml

This is where all actors involved in flows get listed and configured. If an actor is going to be used in a flow, it
needs to appear here.

A typical implementer is configured like this:
```yaml
  - id: request
    class: com.apifortress.afthem.actors.proxy.RequestActor
    type: proxy
    instances: 2
```
`id` the ID of the actor

`class` the class implementing the actor

`type` a type among `proxy` `filter` `transformer` and `sidecar`

`instances` (optional) the number of instances of the actor to be instantiated

`thread_pool` (optional) the name of the thread pool assigned to this actor

This file also defines thread pools in the `thread_pools` section. Thread pools describe pools of threads to be assigned
to actors. A typical thread pool looks like this:

```yaml
  default:
    min: 1
    max: 2
    factor: 1
```

The key of the thread pool (in this case `default`) is a single word that identifies the thread pool. A `default` thread
pool is always required.

`min` is the minimum number of threads created for this thread pool.

`max` is the maximum number of threads created for this thread pool (the threads exceeding `min` get decommissioned when
not in use)

`factor` is a multiplier that depends on the server AFtheM is operating on and it works like this
`factor*cpu=n_of_threads`. A way to make the system more adaptive to the context.

Check out `fine_tuning.md` for further readings on this topic.

### backends.yml

This file connects the inbound requests to the outbound destinations.

A typicial backend looks like this:
```yaml
- prefix: '127.0.0.1/any'
  upstream: 'https://httpbin.org/anything'
  flow_id: default
```

`prefix` how the inbound request will look like, without protocol and port.

`upstream` where to send the request to. If this field is omitted, the full request URL will be used (useful in
conjunction with a forward proxy)

`flow_id` which flow needs to be used.

**Everything exceeding prefix on the right side** will be passed over to the upstream. In this example if I send a
request to `http://127.0.0.1:8080/any/whatever` it will be forwarded to `https://httpbin.org/anything/whatever`

Optionally, a `headers` filter can also be applied. For example:

```yaml
- prefix: '[^/]*/only/with/header'
  headers:
    x-my-header: anything
  upstream: 'https://httpbin.org/anything'
  flow_id: default

- prefix: '[^/]*/only/with/header'
  headers:
    x-my-header: mastiff
  upstream: 'https://mastiff.apifortress.com/app/api/rest/relay'
  flow_id: default
```

If the `x-my-header` header is present and is equal to `anything`, the first configuration will be chosen.
If the given header is equal to `mastiff`, the second configuration will be chosen.

Furthermore, a load balancing functionality is available. Please refer to the [load balancing guide](load_balancing.md). 

## Flows

Flows are discussed in a separate document: `flows.md`